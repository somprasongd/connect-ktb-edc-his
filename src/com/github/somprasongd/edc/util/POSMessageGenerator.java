/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.somprasongd.edc.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sompr
 */
public class POSMessageGenerator {

    private static final Logger LOG = Logger.getLogger(POSMessageGenerator.class.getName());

    // Length ของ Field Data ตาม spec
    private static final int AMOUNT_LENGTH = 12;
    private static final int CARD_NO_LENGTH = 13;
    private static final int VISIT_NUMBER_LENGTH = 13;
    private static final int INVOICE_NO_LENGTH = 6;

    private static final String FIELD_SEPARATOR = "1C"; // Fix value "1Ch" ใช้สำหรับคั่นข้อมูลระหว่าง Field

    public static String getSaleText(String txCode, double amount,
            String ownerCardNo, String childCardNo, String foreignerCardNo) {
        if (!isValidSaleTxCode(txCode)) {
            LOG.warning("invalid transaction code: " + txCode);
            return null;
        }

        String amountData = formatAmount(amount);

        if (isTooLong("amount", amountData, AMOUNT_LENGTH)
                || isTooLong("ownerCardNo", ownerCardNo, CARD_NO_LENGTH)
                || isTooLong("childCardNo", childCardNo, CARD_NO_LENGTH)
                || isTooLong("foreignerCardNo", foreignerCardNo, CARD_NO_LENGTH)) {
            return null;
        }

        // POS Interface massage spec. V 1.10
//        if (amountString == null || amountString.isEmpty()) {
//            amountString = "000000000000";
//        }
//        if (ownerCardNo == null || ownerCardNo.isEmpty()) {
//            ownerCardNo = "0000000000000";
//        }
//        if (childCardNo == null || childCardNo.isEmpty()) {
//            childCardNo = "0000000000000";
//        }
//        if (foreignerCardNo == null || foreignerCardNo.isEmpty()) {
//            foreignerCardNo = "B000000000000";
//        }
        StringBuilder messageData = new StringBuilder();
        messageData.append(buildField("40", AMOUNT_LENGTH, amountData));

        if (!isEmpty(childCardNo)) {
            messageData.append(buildField("71", CARD_NO_LENGTH, childCardNo));
        }

        if (!isEmpty(foreignerCardNo)) {
            messageData.append(buildField("72", CARD_NO_LENGTH, foreignerCardNo));
        }

        if (!isEmpty(ownerCardNo)) {
            messageData.append(buildField("73", CARD_NO_LENGTH, ownerCardNo));

            // Field 74 หาก POS ส่ง Message Type 74 มา EDC จะเอาเลขบัตรจากการอ่าน Chip เทียบกับ Message Type 74 ให้ Reject รายการ
            if (!ownerCardNo.equals("0000000000000")) {
                messageData.append(buildField("74", CARD_NO_LENGTH, ownerCardNo));
            }
        }

        return genText(txCode, messageData.toString());
    }

    private static boolean isValidSaleTxCode(String code) {
        String[] saleTxCodes = new String[]{
            "11", // ผู้ป่วยนอกทั่วไป สิทธิตนเองและครอบครัว
            "12", // ผู้ป่วยนอกทั่วไป สิทธิบุตร 0-7 ปี
            "13", // ผู้ป่ายนอกทั่วไป สิทธิคู่สมรสต่างชาติ
            "14", // ผู้ป่วยนอกทั่วไป ไม่สามารถใช้บัตรได้
            "21", // หน่วยไตเทียม สิทธิตนเองและครอบครัว
            "22", // หน่วยไตเทียม สิทธิบุตร 0-7 ปี
            "23", // หน่วยไตเทียม สิทธิคู่สมรสต่างชาติ
            "24", // หน่วยไตเทียม ไม่สามารถใช้บัตรได้
            "31", // หน่วยรังสีผู้เป็นมะเร็ง สิทธิตนเองและครอบครัว
            "32", // หน่วยรังสีผู้เป็นมะเร็ง สิทธิบุตร 0-7 ปี
            "33", // หน่วยรังสีผู้เป็นมะเร็ง สิทธิคู่สมรสต่างชาติ
            "34" // หน่วยรังสีผู้เป็นมะเร็ง ไม่สามารถใช้บัตรได้
        };
        return Arrays.asList(saleTxCodes).contains(code);
    }

    // สิทธิบัตรทอง from spec v2.13
    public static String getSaleTextUC(String txCode,
            double totalAmount,
            double privilegeAmount,
            double paidAmount,
            String ownerCardNo,
            String visitNumber) {
        if (!isValidSaleTxCodeUC(txCode)) {
            LOG.warning("invalid transaction code: " + txCode);
            return null;
        }

        String totalAmountData = formatAmount(totalAmount);
        String privilegeAmountData = formatAmount(privilegeAmount);
        String paidAmountData = formatAmount(paidAmount);

        if (isTooLong("totalAmount", totalAmountData, AMOUNT_LENGTH)
                || isTooLong("privilegeAmount", privilegeAmountData, AMOUNT_LENGTH)
                || isTooLong("paidAmount", paidAmountData, AMOUNT_LENGTH)
                || isTooLong("ownerCardNo", ownerCardNo, CARD_NO_LENGTH)
                || isTooLong("visitNumber", visitNumber, VISIT_NUMBER_LENGTH)) {
            return null;
        }

        StringBuilder messageData = new StringBuilder();
        messageData.append(buildField("43", AMOUNT_LENGTH, totalAmountData));
        messageData.append(buildField("44", AMOUNT_LENGTH, privilegeAmountData));
        messageData.append(buildField("45", AMOUNT_LENGTH, paidAmountData));

        if (!isEmpty(ownerCardNo)) {
            // Field 74 หาก POS ส่ง Message Type 74 มา EDC จะเอาเลขบัตรจากการอ่าน Chip เทียบกับ Message Type 74 ให้ Reject รายการ
            messageData.append(buildField("74", CARD_NO_LENGTH, ownerCardNo));
        }

        if (!isEmpty(visitNumber)) {
            messageData.append(buildField("VN", VISIT_NUMBER_LENGTH, visitNumber));
        }

        return genText(txCode, messageData.toString());
    }

    private static boolean isValidSaleTxCodeUC(String code) {
        String[] saleTxCodes = new String[]{
            "60" // ใช้สิทธิบัตรทอง from spec v2.13
        };
        return Arrays.asList(saleTxCodes).contains(code);
    }

    public static String getVoidText(String invoiceNumber) {
        if (isEmpty(invoiceNumber) || isTooLong("invoiceNumber", invoiceNumber, INVOICE_NO_LENGTH)) {
            return null;
        }
        // 26 = Void (รายการยกเลิก)
        return genText("26", buildField("65", INVOICE_NO_LENGTH, invoiceNumber));
    }

    public static String getRePrintText(String invoiceNumber) {
        if (isEmpty(invoiceNumber) || isTooLong("invoiceNumber", invoiceNumber, INVOICE_NO_LENGTH)) {
            return null;
        }
        // 92 = re print (รายการพิมพ์สลิปซ้ำ)
        return genText("92", buildField("65", INVOICE_NO_LENGTH, invoiceNumber));
    }

    public static String getSettlementText() {
        // 50 = Settlement (รายการโอนยอด), no field data
        return genText("50", "");
    }

    /**
     * Format STX + LENGTH + MESSAGE DATA(Reserve + Presentation Header + Field
     * Data) + ETX + LRC
     *
     * @param txCode
     * @param messageData
     * @return
     */
    private static String genText(String txCode, String messageData) {
        String H_STX = "02"; // Fix value "02h" ใช้สำหรับบ่งบอกจุดเริ่มต้นของชุดข้อมูล
        String H_Reserve = "30 30 30 30 30 30 30 30 30 30"; // Fix value "0000000000" กำหนดไว้เพื่อใช้ในอนาคตหากมีความต้องการ
        String H_FormatVer = "31"; // Fix value "1" format version
        String H_ReqRespIndcstor = "30"; // Fix value "0" = Request
        String H_TransCode = HexConverter.asciiToHexWithSpace(txCode);
        String H_RespCode = "30 30";
        String H_MoreDataIndicator = "30"; // Fix value "1"
        String H_FieldSeparator = FIELD_SEPARATOR;

        String T_ETX = "03"; // Fix value "03h" ใช้สำหรับบ่งบอกจุดสิ้นสุดของชุดข้อมูล

        String M_Data = messageData.trim();

        String H_Data = H_STX + " " + getLengthData(M_Data) + " " + H_Reserve + " " + H_FormatVer + " " + H_ReqRespIndcstor + " " + H_TransCode + " " + H_RespCode + " " + H_MoreDataIndicator + " " + H_FieldSeparator;

        // ไม่มี Field Data (เช่น Settlement) ต้องไม่ใส่ช่องว่างซ้อน
        String HM_Data = M_Data.isEmpty() ? H_Data : H_Data + " " + M_Data;

        String T_Data = T_ETX;

        // LRC (Longitudinal Redundancy Character) = XOR ทุก byte ตั้งแต่ STX ถึง ETX (รวมทั้ง STX และ ETX)
        // หลักฐาน: "POS INTERFACE MESSAGE SPECIFICATIONS V1.00_รับชำระ.pdf" (Template 1.00, 05-04-2018)
        //   หัวข้อ "ตัวอย่างข้อมูลที่ส่ง" Sale 200 บาท ตารางแจกแจง binary แถวสุดท้ายระบุ "Xor STX-ETX" = 13 ซึ่งตรงกับสูตรนี้
        //   (ไฟล์ PDF อยู่ใน git history: เพิ่มใน commit 106ac42, ลบออกใน f50c5c0)
        // ข้อความ "โดยไม่รวม ETX" ในเอกสาร (รวมถึงฉบับรักษาพยาบาล V1.10-V2.13) และบรรทัด hex "... 1C 03 11"
        // ในตัวอย่างเดียวกันไม่ถูกต้อง
        // ใช้งานจริงกับเครื่อง EDC ได้ด้วยสูตรนี้
        String T_XorStxEtx = lrc(HM_Data + " " + T_Data);

        String text = HM_Data + " " + T_Data + " " + T_XorStxEtx;
        return text;
    }

    public static LinkedHashMap<String, Object> getMessageObject(String messagePOS) {
        LinkedHashMap<String, Object> lhm = new LinkedHashMap<String, Object>();
        try {
            String txtMsgPOS = messagePOS.trim().replace(" ", "");

            String H_STX = txtMsgPOS.substring(0, 2);//"02";
            lhm.put("STX", H_STX);

            String H_Length = txtMsgPOS.substring(2, 6);//"00 35";
            lhm.put("Length", H_Length);

            String H_Reserve = txtMsgPOS.substring(6, 26);//"30 30 30 30 30 30 30 30 30 30";
            lhm.put("Reserve", H_Reserve);
            lhm.put("Reserve_Value", HexConverter.hexToASCII(H_Reserve));

            String H_FormatVer = txtMsgPOS.substring(26, 28);//"31";
            lhm.put("FormatVer", H_FormatVer);
            lhm.put("FormatVer_Value", HexConverter.hexToASCII(H_FormatVer));

            String H_ReqRespIndcstor = txtMsgPOS.substring(28, 30);//"30";
            lhm.put("ReqRespIndcstor", H_ReqRespIndcstor);
            lhm.put("ReqRespIndcstor_Value", HexConverter.hexToASCII(H_ReqRespIndcstor));

            String H_TransCode = txtMsgPOS.substring(30, 34);//"32 30";
            lhm.put("TransCode", H_TransCode);
            lhm.put("TransCode_Value", HexConverter.hexToASCII(H_TransCode));

            String H_RespCode = txtMsgPOS.substring(34, 38);//"30 30";
            lhm.put("ResponseCode", H_RespCode);
            lhm.put("ResponseCode_Value", HexConverter.hexToASCII(H_RespCode));

            String H_MoreDataIndicator = txtMsgPOS.substring(38, 40);//"30";
            lhm.put("MoreDataIndicator", H_MoreDataIndicator);
            lhm.put("MoreDataIndicator_Value", HexConverter.hexToASCII(H_MoreDataIndicator));

            String H_FieldSeparator = txtMsgPOS.substring(40, 42);//"1C";
            lhm.put("FieldSeparator", H_FieldSeparator);

            String F_Data = txtMsgPOS.substring(42, (txtMsgPOS.length() - 46) + 42);
            String[] F_Datas = F_Data.toLowerCase().split("1c");
            List<LinkedHashMap<String, String>> fieldDatas = new ArrayList<LinkedHashMap<String, String>>();
            for (String fieldData : F_Datas) {
                LinkedHashMap<String, String> lhm1 = new LinkedHashMap<String, String>();
                try {
                    lhm1.put("FieldData", fieldData);
                    lhm1.put("FieldType", fieldData.substring(0, 4));
                    lhm1.put("FieldType_Value", HexConverter.hexToASCII(fieldData.substring(0, 4)));
                    lhm1.put("Length", fieldData.substring(4, 8));
                    lhm1.put("Data", fieldData.substring(8, (Integer.parseInt(fieldData.substring(4, 8)) * 2) + 8));
                    lhm1.put("Data_Value", HexConverter.hexToASCII(fieldData.substring(8, (Integer.parseInt(fieldData.substring(4, 8)) * 2) + 8)));
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    lhm1.put("MSG_Err", "Index and length must refer to a location within the string");
                }
                fieldDatas.add(lhm1);
            }
            lhm.put("FieldDatas", fieldDatas);
            String F_ETX = txtMsgPOS.substring(42 + (txtMsgPOS.length() - 46), 42 + (txtMsgPOS.length() - 46) + 2);
            lhm.put("ETX", F_ETX);
            String F_XOR = txtMsgPOS.substring(44 + (txtMsgPOS.length() - 46), 44 + (txtMsgPOS.length() - 46) + 2);
            lhm.put("XOR", F_XOR);
            lhm.put("XOR_Checked", lrc(messagePOS.substring(0, messagePOS.length() - 3)));

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return lhm;
    }

    /**
     *
     * @param messagePOS
     * @return { error: "if have error", reponse_code: "00 - for completed",
     * datas: [ {type: "", data: ""} ] * }
     */
    public static LinkedHashMap<String, Object> getReponseMessageObject(String messagePOS) {
        LinkedHashMap<String, Object> lhm = new LinkedHashMap<String, Object>();
        try {
            String txtMsgPOS = messagePOS.trim().replace(" ", "");
            // latest version (24/05/2024) of edc at ladyao hospital send message start with 06
            if (txtMsgPOS.startsWith("06")) {
                txtMsgPOS = txtMsgPOS.substring(2);
            }

            String H_ReqRespIndcstor = HexConverter.hexToASCII(txtMsgPOS.substring(28, 30));
            if (!"1".equals(H_ReqRespIndcstor)) {
                lhm.put("error", "Not response message");
                return lhm;
            }

            String H_RespCode = txtMsgPOS.substring(34, 38);
            lhm.put("reponse_code", HexConverter.hexToASCII(H_RespCode));

            String F_Data = txtMsgPOS.substring(42, (txtMsgPOS.length() - 46) + 42);
            String[] F_Datas = F_Data.toLowerCase().split("1c");
            List<LinkedHashMap<String, String>> fieldDatas = new ArrayList<LinkedHashMap<String, String>>();
            for (String fdText : F_Datas) {
                LinkedHashMap<String, String> lhm1 = new LinkedHashMap<String, String>();
                try {
                    lhm1.put("type", HexConverter.hexToASCII(fdText.substring(0, 4)));
                    lhm1.put("data", HexConverter.hexToASCII(fdText.substring(8, (Integer.parseInt(fdText.substring(4, 8)) * 2) + 8)));
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    lhm1.put("error", "Index and length must refer to a location within the string");
                }
                fieldDatas.add(lhm1);
            }
            lhm.put("datas", fieldDatas);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return lhm;
    }

    public static String getFieldData(List<LinkedHashMap<String, String>> fieldDatas, String fieldType) {
        for (LinkedHashMap<String, String> fieldData : fieldDatas) {
            if (fieldData.get("error") == null
                    && fieldData.get("type").equals(fieldType)) {
                return fieldData.get("data");
            }
        }
        return null;
    }

    /**
     * ข้อมูลที่ยาวเกิน Length ที่ spec กำหนดจะทำให้ Length ของ field ไม่ตรงกับข้อมูลจริง
     * จึงไม่ส่งรายการ (ไม่ตัดทิ้ง เพราะเลขบัตร/VN/invoice ที่ถูกตัดจะกลายเป็นข้อมูลผิด)
     */
    private static boolean isTooLong(String fieldName, String value, int maxLength) {
        if (value != null && value.length() > maxLength) {
            // ไม่ log ค่าจริง เพราะอาจเป็นเลขบัตรประชาชน
            LOG.warning(fieldName + " length " + value.length() + " exceeds max " + maxLength);
            return true;
        }
        return false;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    // ทศนิยม 2 หลักแบบปัดทิ้ง แล้วตัดจุดออก เช่น 20.50 -> "2050"
    private static String formatAmount(double amount) {
        DecimalFormat formatter = new DecimalFormat("#.00");
        formatter.setRoundingMode(RoundingMode.DOWN);
        return formatter.format(amount).replace(".", "");
    }

    /**
     * Field Type (2) + Length (2, BCD) + Data (เติม '0' ด้านหน้าให้ครบ Length) +
     * Field Separator (1Ch)
     */
    private static String buildField(String fieldType, int length, String data) {
        return " " + HexConverter.asciiToHexWithSpace(fieldType).toUpperCase()
                + " " + toBcdLength(length)
                + " " + HexConverter.asciiToHexWithSpace(padLeft(length, "0", data))
                + " " + FIELD_SEPARATOR;
    }

    // ความยาว 2 bytes แบบ BCD เช่น 256 -> "02 56"
    private static String toBcdLength(int length) {
        String lenData = padLeft(4, "0", String.valueOf(length));
        return lenData.substring(0, 2) + " " + lenData.substring(2);
    }

    private static String padLeft(int number, String character, String text) {
        StringBuilder sb = new StringBuilder();

        for (int i = number - text.length(); i > 0; i--) {
            sb.append(character);
        }

        sb.append(text);
        return sb.toString();
    }

    private static String getLengthData(String text) {
        text = text.trim().replace(" ", "");
        // 18 = Reserve (10) + Presentation Header (8)
        return toBcdLength((text.length() / 2) + 18);
    }

    /**
     * XOR ทุก byte ของ hex string (คั่นด้วยช่องว่างหรือไม่ก็ได้) คืนค่าเป็น hex 2
     * หลักตัวพิมพ์ใหญ่
     */
    private static String lrc(String hexString) {
        String hex = hexString.trim().replace(" ", "");
        int lrc = 0;
        for (int i = 0; i < hex.length(); i += 2) {
            lrc ^= Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return String.format("%02X", lrc);
    }

}
