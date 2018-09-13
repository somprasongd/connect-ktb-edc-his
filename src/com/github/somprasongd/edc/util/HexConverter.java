/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.somprasongd.edc.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sompr
 */
public class HexConverter {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static String asciiToHex(String value) {
        if (value == null) {
            return "";
        }
        return bytesToHex(value.getBytes()).replace(" ", "");
    }

    public static String asciiToHexWithSpace(String value) {
        if (value == null) {
            return "";
        }
        return bytesToHex(value.getBytes());
    }

//    public static String bytesToHex(byte[] buffers) {
//        char[] chars = new char[2 * buffers.length];
//        for (int i = 0; i < buffers.length; ++i) {
//            chars[2 * i] = HEX_CHARS[(buffers[i] & 0xF0) >>> 4];
//            chars[2 * i + 1] = HEX_CHARS[buffers[i] & 0x0F];
//        }
//        return new String(chars);
//    }
    public static String bytesToHex(byte[] buffers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffers.length; ++i) {
            sb.append(HEX_CHARS[(buffers[i] & 0xF0) >>> 4]);
            sb.append(HEX_CHARS[buffers[i] & 0x0F]);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

//    public static String asciiToHex(String value) {
//        char[] chars = asciiText.toCharArray();
//        StringBuilder hex = new StringBuilder();
//        for (char ch : chars) {
//            hex.append(Integer.toHexString((int) ch));
//        }
//        return hex.toString();
//    }
    public static String hexToASCII(String hexString) {
        byte[] txtInByte = new byte[hexString.length() / 2];
        int j = 0;
        for (int i = 0; i < hexString.length(); i += 2) {
            txtInByte[j++] = Byte.parseByte(hexString.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }

//    public static String hexToAscii(String hexString) {
//        StringBuilder output = new StringBuilder("");
//        for (int i = 0; i < hexString.length(); i += 2) {
//            String str = hexString.substring(i, i + 2);
//            output.append((char) Integer.parseInt(str, 16));
//        }
//        return output.toString();
//    }
    public static String binaryToHex(String binaryString) {
        return Integer.toString(Integer.parseInt(binaryString.substring(0, 4), 2), 16).toUpperCase();
    }

    private final static Map<Character, String> HEX_CHAR_TO_BINARY = new HashMap<Character, String>() {
        {
            put('0', "0000");
            put('1', "0001");
            put('2', "0010");
            put('3', "0011");
            put('4', "0100");
            put('5', "0101");
            put('6', "0110");
            put('7', "0111");
            put('8', "1000");
            put('9', "1001");
            put('a', "1010");
            put('b', "1011");
            put('c', "1100");
            put('d', "1101");
            put('e', "1110");
            put('f', "1111");
        }
    };

    public static String hexStringToBinary(String hex) {
        StringBuilder result = new StringBuilder();
        for (char c : hex.toLowerCase().toCharArray()) {
            // This will crash for non-hex characters.
            result.append(HEX_CHAR_TO_BINARY.get(c));
        }
        return result.toString();
    }

}
