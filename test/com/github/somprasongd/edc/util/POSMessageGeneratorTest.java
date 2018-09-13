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
        String expResult = "02 00 89 30 30 30 30 30 30 30 30 30 30 31 30 31 31 30 30 30 1C 34 30 00 12 30 30 30 30 30 30 30 30 34 30 30 30 1C 37 31 00 13 31 31 31 31 31 31 31 31 31 31 31 31 31 1C 37 32 00 13 42 32 32 32 32 32 32 32 32 32 32 32 32 1C 37 33 00 13 33 33 33 33 33 33 33 33 33 33 33 33 33 1C 03 E3";
        String result = POSMessageGenerator.getSaleText(txCode, amount, ownerCardNo, childCardNo, foreignerCardNo);
        System.out.println(result);
        assertEquals(expResult, result);
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
