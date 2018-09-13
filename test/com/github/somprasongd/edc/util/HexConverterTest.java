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

   
    
}
