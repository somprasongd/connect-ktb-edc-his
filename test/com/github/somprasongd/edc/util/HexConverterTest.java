/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.somprasongd.edc.util;

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
public class HexConverterTest {
    
    public HexConverterTest() {
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
     * Test of asciiToHex method, of class HexConverter.
     */
    @Test
    public void testAsciiToHex() {
        System.out.println("asciiToHex");
        byte[] bs = new byte[]{(byte) 6};
        String value = new String(bs);
        String expResult = "06";
        String result = HexConverter.asciiToHex(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of bytesToHex method, of class HexConverter.
     */
    @Test
    public void testBytesToHex() {
        System.out.println("bytesToHex");
        byte[] buffers = new byte[]{(byte) 6};
        String expResult = "06";
        String result = HexConverter.bytesToHex(buffers);
        assertEquals(expResult, result);
    }

    /**
     * Test of hexToASCII method, of class HexConverter.
     */
    @Test
    public void testHexToASCII() {
        System.out.println("hexToASCII");
        String hexString = "54584e2043414e43454c202020202020202020202020202020202020202020202020202020202020";
        String expResult = "TXN CANCEL                              ";
        String result = HexConverter.hexToASCII(hexString);
        assertEquals(expResult, result);
    }

    @Test
    public void testHexToASCIIByteAbove7F() {
        System.out.println("hexToASCIIByteAbove7F");
        // byte 80-FF ต้องไม่โยน NumberFormatException
        String result = HexConverter.hexToASCII("31893132");
        assertTrue(result.startsWith("1"));
        assertTrue(result.endsWith("12"));
    }

    /**
     * Test of hexWithSpaceToBytes method, of class HexConverter. LENGTH แบบ
     * BCD 80-99 และ LRC อาจเป็น byte 80-FF ได้
     */
    @Test
    public void testHexWithSpaceToBytes() {
        System.out.println("hexWithSpaceToBytes");
        byte[] expResult = new byte[]{0x02, 0x00, (byte) 0x89, 0x1C, 0x03, (byte) 0xE3, (byte) 0xFF};
        byte[] result = HexConverter.hexWithSpaceToBytes("02 00 89 1C 03 E3 FF");
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testHexWithSpaceToBytesSingleByte() {
        System.out.println("hexWithSpaceToBytesSingleByte");
        // ACK ที่ส่งกลับ EDC
        assertArrayEquals(new byte[]{0x06}, HexConverter.hexWithSpaceToBytes("06"));
    }

   
    
}
