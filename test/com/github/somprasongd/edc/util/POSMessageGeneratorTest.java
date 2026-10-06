/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.somprasongd.edc.util;

import java.util.LinkedHashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sompr
 */
public class POSMessageGeneratorTest {

    public POSMessageGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSaleText method, of class POSMessageGenerator.
     */
    @Test
    public void testGetSaleText() {
        System.out.println("getSaleText");
        String txCode = "11";
        double amount = 20.50;
        String ownerCardNo = "3333333333333";
        String childCardNo = "1111111111111";
        String foreignerCardNo = "B222222222222";
        // LENGTH 01 07 = 18 (header) + 17 (field 40) + 18 x 4 (field 71, 72, 73, 74)
        // field 40 = 000000002050 (20.50), field 74 = ownerCardNo
        String expResult = "02 01 07 30 30 30 30 30 30 30 30 30 30 31 30 31 31 30 30 30 1C 34 30 00 12 30 30 30 30 30 30 30 30 32 30 35 30 1C 37 31 00 13 31 31 31 31 31 31 31 31 31 31 31 31 31 1C 37 32 00 13 42 32 32 32 32 32 32 32 32 32 32 32 32 1C 37 33 00 13 33 33 33 33 33 33 33 33 33 33 33 33 33 1C 37 34 00 13 33 33 33 33 33 33 33 33 33 33 33 33 33 1C 03 50";
        String result = POSMessageGenerator.getSaleText(txCode, amount, ownerCardNo, childCardNo, foreignerCardNo);
        System.out.println(result);
        assertEquals(expResult, result);
        assertEquals(xorStxToEtx(result), lastByte(result));
    }

    /**
     * Test of getVoidText method, of class POSMessageGenerator.
     */
    @Test
    public void testGetVoidText() {
        System.out.println("getVoidText");
        String invoiceNumber = "100098";
        String expResult = "02 00 29 30 30 30 30 30 30 30 30 30 30 31 30 32 36 30 30 30 1C 36 35 00 06 31 30 30 30 39 38 1C 03 18";
        String result = POSMessageGenerator.getVoidText(invoiceNumber);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRePrintText method, of class POSMessageGenerator.
     */
    @Test
    public void testGetRePrintText() {
        System.out.println("getRePrintText");
        String invoiceNumber = "100098";
        String expResult = "02 00 29 30 30 30 30 30 30 30 30 30 30 31 30 39 32 30 30 30 1C 36 35 00 06 31 30 30 30 39 38 1C 03 17";
        String result = POSMessageGenerator.getRePrintText(invoiceNumber);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSettlementText method, of class POSMessageGenerator.
     */
    @Test
    public void testGetSettlementText() {
        System.out.println("getSettlementText");
        String expResult = "02 00 18 30 30 30 30 30 30 30 30 30 30 31 30 35 30 30 30 30 1C 03 31";
        String result = POSMessageGenerator.getSettlementText();
        assertEquals(expResult, result);
        assertEquals(xorStxToEtx(result), lastByte(result));
    }

    /**
     * LRC = XOR from STX through ETX, per the "Xor STX-ETX" row of the Sale
     * 200 baht example table in spec V1.00 (the "03 11" hex line and the
     * "ไม่รวม ETX" wording in the spec are wrong).
     */
    @Test
    public void testLrcRuleMatchesSpecExample() {
        System.out.println("lrcRuleMatchesSpecExample");
        String specSale200 = "02 00 35 30 30 30 30 30 30 30 30 30 30 31 30 32 30 30 30 30 1C 34 30 00 12 30 30 30 30 30 30 30 32 30 30 30 30 1C 03";
        assertEquals("13", xorStxToEtx(specSale200 + " 13"));
    }

    @Test
    public void testGeneratedLrcIsXorStxToEtx() {
        System.out.println("generatedLrcIsXorStxToEtx");
        String[] messages = {
            POSMessageGenerator.getVoidText("100098"),
            POSMessageGenerator.getRePrintText("100098"),
            POSMessageGenerator.getSettlementText(),
            POSMessageGenerator.getSaleTextUC("60", 500, 300, 200, "3333333333333", "6812345")
        };
        for (String message : messages) {
            assertEquals(message, xorStxToEtx(message), lastByte(message));
        }
    }

    @Test
    public void testGetSaleTextUC() {
        System.out.println("getSaleTextUC");
        String result = POSMessageGenerator.getSaleTextUC("60", 500, 300, 200, "3333333333333", "6812345");
        // field 74 = ownerCardNo, field VN = 0000006812345
        assertTrue(result.contains("37 34 00 13 33 33 33 33 33 33 33 33 33 33 33 33 33 1C"));
        assertTrue(result.contains("56 4E 00 13 30 30 30 30 30 30 36 38 31 32 33 34 35 1C"));
        assertEquals(xorStxToEtx(result), lastByte(result));
    }

    @Test
    public void testGetSaleTextUCWithoutOwnerCardStillSendsVN() {
        System.out.println("getSaleTextUCWithoutOwnerCardStillSendsVN");
        String result = POSMessageGenerator.getSaleTextUC("60", 500, 300, 200, null, "6812345");
        assertFalse(result.contains("37 34 00 13"));
        assertTrue(result.contains("56 4E 00 13 30 30 30 30 30 30 36 38 31 32 33 34 35 1C"));
    }

    @Test
    public void testGetSaleTextUCWithoutVisitNumber() {
        System.out.println("getSaleTextUCWithoutVisitNumber");
        String result = POSMessageGenerator.getSaleTextUC("60", 500, 300, 200, "3333333333333", null);
        assertTrue(result.contains("37 34 00 13"));
        assertFalse(result.contains("56 4E"));
    }

    @Test
    public void testAcceptsValuesAtMaxLength() {
        System.out.println("acceptsValuesAtMaxLength");
        assertNotNull(POSMessageGenerator.getVoidText("123456"));
        assertNotNull(POSMessageGenerator.getRePrintText("123456"));
        assertNotNull(POSMessageGenerator.getSaleText("11", 9999999999.99, "3333333333333", null, null));
        assertNotNull(POSMessageGenerator.getSaleTextUC("60", 500, 300, 200, "3333333333333", "1234567890123"));
    }

    @Test
    public void testRejectsValuesLongerThanSpec() {
        System.out.println("rejectsValuesLongerThanSpec");
        // invoice 6, card/VN 13, amount 12 digits (ทศนิยม 2 หลัก)
        assertNull(POSMessageGenerator.getVoidText("1234567"));
        assertNull(POSMessageGenerator.getRePrintText("1234567"));
        assertNull(POSMessageGenerator.getSaleText("11", 10000000000.00, "3333333333333", null, null));
        assertNull(POSMessageGenerator.getSaleText("11", 100, "33333333333334", null, null));
        assertNull(POSMessageGenerator.getSaleText("12", 100, "3333333333333", "11111111111112", null));
        assertNull(POSMessageGenerator.getSaleText("13", 100, "3333333333333", null, "B2222222222223"));
        assertNull(POSMessageGenerator.getSaleTextUC("60", 10000000000.00, 300, 200, "3333333333333", "6812345"));
        assertNull(POSMessageGenerator.getSaleTextUC("60", 500, 300, 200, "33333333333334", "6812345"));
        assertNull(POSMessageGenerator.getSaleTextUC("60", 500, 300, 200, "3333333333333", "12345678901234"));
    }

    // XOR of every byte from STX through ETX (excludes the trailing LRC byte)
    private static String xorStxToEtx(String hexMessage) {
        String[] bytes = hexMessage.trim().split(" ");
        int lrc = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            lrc ^= Integer.parseInt(bytes[i], 16);
        }
        return String.format("%02X", lrc);
    }

    private static String lastByte(String hexMessage) {
        String[] bytes = hexMessage.trim().split(" ");
        return bytes[bytes.length - 1];
    }

    /**
     * Test of getMessageObject method, of class POSMessageGenerator.
     */
    @Test
    public void testgetMessageObject() {
        System.out.println("getMessageObject");
        String messagePOS = "02 00 89 30 30 30 30 30 30 30 30 30 30 31 30 31 31 30 30 30 1C 34 30 00 12 30 30 30 30 30 30 30 30 34 30 30 30 1C 37 31 00 13 31 31 31 31 31 31 31 31 31 31 31 31 31 1C 37 32 00 13 42 32 32 32 32 32 32 32 32 32 32 32 32 1C 37 33 00 13 33 33 33 33 33 33 33 33 33 33 33 33 33 1C 03 E3";
        String expResult = null;
        LinkedHashMap<String, Object> result = POSMessageGenerator.getMessageObject(messagePOS);
        
        assertNotEquals(expResult, result);
        
        for (String key : result.keySet()) {
            Object value = result.get(key);
            if (value instanceof String) {
                System.out.println(key + ": " + value);
            } else if (value instanceof List){
                System.out.println("-------------------------------------------");
                List<LinkedHashMap<String, String>> fieldDatas = (List<LinkedHashMap<String, String>>) value;
                for (LinkedHashMap<String, String> fieldData : fieldDatas) {                    
                    for (String string : fieldData.keySet()) {                        
                        System.out.println(string + ": " + fieldData.get(string));
                    }
                    System.out.println("-------------------------------------------");
                }
            }

        }
    }

}
