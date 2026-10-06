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

    public static String getSaleText(String txCode, double amount,
            String ownerCardNo, String childCardNo, String foreignerCardNo) {
        if (!isValidSaleTxCode(txCode)) {
            LOG.warning("invalid transaction code: " + txCode);
            return null;
        }

        String messageData = "";

        DecimalFormat formatter = new DecimalFormat("#.00");
        formatter.setRoundingMode(RoundingMode.DOWN);
        String amountString = formatter.format(amount);

        if (isTooLong("amount", amountString.replace(".", ""), 12)
                || isTooLong("ownerCardNo", ownerCardNo, 13)
                || isTooLong("childCardNo", childCardNo, 13)
                || isTooLong("foreignerCardNo", foreignerCardNo, 13)) {
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
        if (amountString != null && !amountString.isEmpty()) {
            String M_FieldType = "34 30";
            String M_LenFieldData = "00 12";
            String textB_40 = padLeft(12, "0", amountString.replace(".", ""));
            String M_AmtData = HexConverter.asciiToHexWithSpace(textB_40);
            String M_AmtSeparator = "1C";
            messageData += " " + M_FieldType + " " + M_LenFieldData + " " + M_AmtData + " " + M_AmtSeparator;
        }

        if (childCardNo != null && !childCardNo.isEmpty()) {
            String M_FieldType_Sale71 = "37 31";
            String M_LenFieldData_Sale71 = "00 13";
            String textB_Sale71 = padLeft(13, "0", childCardNo);
            String M_Sale71Data = HexConverter.asciiToHexWithSpace(textB_Sale71);
            String M_AmtSeparator_Sale71 = "1C";
            messageData += " " + M_FieldType_Sale71 + " " + M_LenFieldData_Sale71 + " " + M_Sale71Data + " " + M_AmtSeparator_Sale71;
        }

        if (foreignerCardNo != null && !foreignerCardNo.isEmpty()) {
            String M_FieldType_Sale72 = "37 32";
            String M_LenFieldData_Sale72 = "00 13";
            String textB_Sale72 = padLeft(13, "0", foreignerCardNo);
            String M_Sale72Data = HexConverter.asciiToHexWithSpace(textB_Sale72);
            String M_AmtSeparator_Sale72 = "1C";
            messageData += " " + M_FieldType_Sale72 + " " + M_LenFieldData_Sale72 + " " + M_Sale72Data + " " + M_AmtSeparator_Sale72;
        }

        if (ownerCardNo != null && !ownerCardNo.isEmpty()) {
            // Field 73
            String M_FieldType_Sale73 = "37 33";
            String M_LenFieldData_Sale73 = "00 13";
            String textB_Sale73 = padLeft(13, "0", ownerCardNo);
            String M_Sale73Data = HexConverter.asciiToHexWithSpace(textB_Sale73);
            String M_AmtSeparator_Sale73 = "1C";
            messageData += " " + M_FieldType_Sale73 + " " + M_LenFieldData_Sale73 + " " + M_Sale73Data + " " + M_AmtSeparator_Sale73;

            // Field 74 หาก POS ส่ง Message Type 74 มา EDC จะเอาเลขบัตรจากการอ่าน Chip เทียบกับ Message Type 74 ให้ Reject รายการ
            if (!ownerCardNo.equals("0000000000000")) {
                String M_FieldType_Sale74 = "37 34";
                String M_LenFieldData_Sale74 = "00 13";
                String textB_Sale74 = padLeft(13, "0", ownerCardNo);
                String M_Sale74Data = HexConverter.asciiToHexWithSpace(textB_Sale74);
                String M_AmtSeparator_Sale74 = "1C";
                messageData += " " + M_FieldType_Sale74 + " " + M_LenFieldData_Sale74 + " " + M_Sale74Data + " " + M_AmtSeparator_Sale74;
            }
        }

        return genText(txCode, messageData);
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

        String messageData = "";

        DecimalFormat formatter = new DecimalFormat("#.00");
        formatter.setRoundingMode(RoundingMode.DOWN);

        String totalAmountString = formatter.format(totalAmount);
        String privilegeAmountString = formatter.format(privilegeAmount);
        String paidAmountString = formatter.format(paidAmount);

        if (isTooLong("totalAmount", totalAmountString.replace(".", ""), 12)
                || isTooLong("privilegeAmount", privilegeAmountString.replace(".", ""), 12)
                || isTooLong("paidAmount", paidAmountString.replace(".", ""), 12)
                || isTooLong("ownerCardNo", ownerCardNo, 13)
                || isTooLong("visitNumber", visitNumber, 13)) {
            return null;
        }

        if (totalAmountString != null && !totalAmountString.isEmpty()) {
            // Field 43
            String M_FieldType = "34 33";
            String M_LenFieldData = "00 12";
            String textB_43 = padLeft(12, "0", totalAmountString.replace(".", ""));
            String M_AmtData = HexConverter.asciiToHexWithSpace(textB_43);
            String M_AmtSeparator = "1C";
            messageData += " " + M_FieldType + " " + M_LenFieldData + " " + M_AmtData + " " + M_AmtSeparator;
        }

        if (privilegeAmountString != null && !privilegeAmountString.isEmpty()) {
            // Field 44
            String M_FieldType = "34 34";
            String M_LenFieldData = "00 12";
            String textB_44 = padLeft(12, "0", privilegeAmountString.replace(".", ""));
            String M_AmtData = HexConverter.asciiToHexWithSpace(textB_44);
            String M_AmtSeparator = "1C";
            messageData += " " + M_FieldType + " " + M_LenFieldData + " " + M_AmtData + " " + M_AmtSeparator;
        }

        if (paidAmountString != null && !paidAmountString.isEmpty()) {
            // Field 45
            String M_FieldType = "34 35";
            String M_LenFieldData = "00 12";
            String textB_45 = padLeft(12, "0", paidAmountString.replace(".", ""));
            String M_AmtData = HexConverter.asciiToHexWithSpace(textB_45);
            String M_AmtSeparator = "1C";
            messageData += " " + M_FieldType + " " + M_LenFieldData + " " + M_AmtData + " " + M_AmtSeparator;
        }

        if (ownerCardNo != null && !ownerCardNo.isEmpty()) {
            // Field 74 หาก POS ส่ง Message Type 74 มา EDC จะเอาเลขบัตรจากการอ่าน Chip เทียบกับ Message Type 74 ให้ Reject รายการ
            String M_FieldType_Sale74 = "37 34";
            String M_LenFieldData_Sale74 = "00 13";
            String textB_Sale74 = padLeft(13, "0", ownerCardNo);
            String M_Sale74Data = HexConverter.asciiToHexWithSpace(textB_Sale74);
            String M_Separator_Sale74 = "1C";
            messageData += " " + M_FieldType_Sale74 + " " + M_LenFieldData_Sale74 + " " + M_Sale74Data + " " + M_Separator_Sale74;
        }

        if (visitNumber != null && !visitNumber.isEmpty()) {
            // Field VN
            String M_FieldType = "56 4E";
            String M_LenFieldData = "00 13";
            String textB_VN = padLeft(13, "0", visitNumber);
            String M_VNData = HexConverter.asciiToHexWithSpace(textB_VN);
            String M_Separator = "1C";
            messageData += " " + M_FieldType + " " + M_LenFieldData + " " + M_VNData + " " + M_Separator;
        }

        return genText(txCode, messageData);
    }

    private static boolean isValidSaleTxCodeUC(String code) {
        String[] saleTxCodes = new String[]{
            "60" // ใช้สิทธิบัตรทอง from spec v2.13
        };
        return Arrays.asList(saleTxCodes).contains(code);
    }

    public static String getVoidText(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            return null;
        }
        if (isTooLong("invoiceNumber", invoiceNumber, 6)) {
            return null;
        }
        String messageData = "";
        String M_FieldType_VoidTrace = "36 35";
        String M_LenFieldData_VoidTrace = "00 06";
        String textB_VoidTrace = padLeft(6, "0", invoiceNumber);
        String M_VoidTraceData = HexConverter.asciiToHexWithSpace(textB_VoidTrace);
        String M_AmtSeparator_VoidTrace = "1C";
        messageData += " " + M_FieldType_VoidTrace + " " + M_LenFieldData_VoidTrace + " " + M_VoidTraceData + " " + M_AmtSeparator_VoidTrace;

        // 26 = Void (รายการยกเลิก)
        return genText("26", messageData);
    }

    public static String getRePrintText(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            return null;
        }
        if (isTooLong("invoiceNumber", invoiceNumber, 6)) {
            return null;
        }
        String messageData = "";
        String M_FieldType_RePrintTrace = "36 35";
        String M_LenFieldData_RePrintTrace = "00 06";
        String textB_RePrintTrace = padLeft(6, "0", invoiceNumber);
        String M_RePrintTraceData = HexConverter.asciiToHexWithSpace(textB_RePrintTrace);
        String M_AmtSeparator_RePrintTrace = "1C";
        messageData += " " + M_FieldType_RePrintTrace + " " + M_LenFieldData_RePrintTrace + " " + M_RePrintTraceData + " " + M_AmtSeparator_RePrintTrace;

        // 92 = re print (รายการพิมพ์สลิปซำ้)
        return genText("92", messageData);
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
        String H_FieldSeparator = "1C"; // Fix value "1Ch" ใช้สำหรับคั่นข้อมูลระหว่าง Field

        String T_ETX = "03"; // Fix value "03h" ใช้สำหรับบ่งบอกจุดสิ้นสุดของชุดข้อมูล

        String M_Data = messageData.trim();

        String H_Data = H_STX + " " + getLengthData(M_Data) + " " + H_Reserve + " " + H_FormatVer + " " + H_ReqRespIndcstor + " " + H_TransCode + " " + H_RespCode + " " + H_MoreDataIndicator + " " + H_FieldSeparator;

        // ไม่มี Field Data (เช่น Settlement) ต้องไม่ใส่ช่องว่างซ้อน
        String HM_Data = M_Data.isEmpty() ? H_Data : H_Data + " " + M_Data;

        String T_Data = T_ETX;

        String XORCHK = GetXOR(HM_Data + " " + T_Data);

        // LRC (Longitudinal Redundancy Character) = XOR ทุก byte ตั้งแต่ STX ถึง ETX (รวมทั้ง STX และ ETX)
        // หลักฐาน: "POS INTERFACE MESSAGE SPECIFICATIONS V1.00_รับชำระ.pdf" (Template 1.00, 05-04-2018)
        //   หัวข้อ "ตัวอย่างข้อมูลที่ส่ง" Sale 200 บาท ตารางแจกแจง binary แถวสุดท้ายระบุ "Xor STX-ETX" = 13 ซึ่งตรงกับสูตรนี้
        //   (ไฟล์ PDF อยู่ใน git history: เพิ่มใน commit 106ac42, ลบออกใน f50c5c0)
        // ข้อความ "โดยไม่รวม ETX" ในเอกสาร (รวมถึงฉบับรักษาพยาบาล V1.10-V2.13) และบรรทัด hex "... 1C 03 11"
        // ในตัวอย่างเดียวกันไม่ถูกต้อง
        // ใช้งานจริงกับเครื่อง EDC ได้ด้วยสูตรนี้
        String T_XorStxEtx = HexConverter.binaryToHex(XORCHK.substring(0, 4)) + HexConverter.binaryToHex(XORCHK.substring(4, 8));

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
            String XORCHK = GetXOR(messagePOS.substring(0, messagePOS.length() - 3));

            lhm.put("XOR_Checked", HexConverter.binaryToHex(XORCHK.substring(0, 4)) + HexConverter.binaryToHex(XORCHK.substring(4, 8)));

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
        String lenData = padLeft(4, "0", String.valueOf((text.length() / 2) + 18));
        return lenData.substring(0, 2) + " " + lenData.substring(2);
    }

    private static String GetXOR(String hexString) {
        try {
            hexString = hexString.trim().replace(" ", "");
            hexString = hexString.substring(0, hexString.length());

            String bValue;

            String ckdigit1;
            String ckdigit2;
            String ckdigit3;
            String ckdigit4;
            String ckdigit5;
            String ckdigit6;
            String ckdigit7;
            String ckdigit8;

            String ckdigitOl1 = "0";
            String ckdigitOl2 = "0";
            String ckdigitOl3 = "0";
            String ckdigitOl4 = "0";
            String ckdigitOl5 = "0";
            String ckdigitOl6 = "0";
            String ckdigitOl7 = "0";
            String ckdigitOl8 = "0";

            for (int i = 0; i < hexString.length(); i += 2) {
                String hs = hexString.substring(i, i + 2);
                bValue = HexConverter.hexStringToBinary(hs);
                ckdigit1 = bValue.substring(0, 1);
                ckdigit2 = bValue.substring(1, 2);
                ckdigit3 = bValue.substring(2, 3);
                ckdigit4 = bValue.substring(3, 4);
                ckdigit5 = bValue.substring(4, 5);
                ckdigit6 = bValue.substring(5, 6);
                ckdigit7 = bValue.substring(6, 7);
                ckdigit8 = bValue.substring(7);

                if (ckdigitOl1.equals(ckdigit1)) {
                    ckdigitOl1 = "0";
                } else {
                    ckdigitOl1 = "1";
                }
                if (ckdigitOl2.equals(ckdigit2)) {
                    ckdigitOl2 = "0";
                } else {
                    ckdigitOl2 = "1";
                }
                if (ckdigitOl3.equals(ckdigit3)) {
                    ckdigitOl3 = "0";
                } else {
                    ckdigitOl3 = "1";
                }
                if (ckdigitOl4.equals(ckdigit4)) {
                    ckdigitOl4 = "0";
                } else {
                    ckdigitOl4 = "1";
                }
                if (ckdigitOl5.equals(ckdigit5)) {
                    ckdigitOl5 = "0";
                } else {
                    ckdigitOl5 = "1";
                }
                if (ckdigitOl6.equals(ckdigit6)) {
                    ckdigitOl6 = "0";
                } else {
                    ckdigitOl6 = "1";
                }
                if (ckdigitOl7.equals(ckdigit7)) {
                    ckdigitOl7 = "0";
                } else {
                    ckdigitOl7 = "1";
                }
                if (ckdigitOl8.equals(ckdigit8)) {
                    ckdigitOl8 = "0";
                } else {
                    ckdigitOl8 = "1";
                }
            }
            return ckdigitOl1 + ckdigitOl2 + ckdigitOl3 + ckdigitOl4 + ckdigitOl5 + ckdigitOl6 + ckdigitOl7 + ckdigitOl8;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "";
    }

}
