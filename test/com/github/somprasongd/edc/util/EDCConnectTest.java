/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.somprasongd.edc.util;

import org.junit.Test;

/**
 *
 * @author sompr
 */
public class EDCConnectTest {

    public EDCConnectTest() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendDataNullMessage() {
        System.out.println("sendDataNullMessage");
        new EDCConnect().sendData(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendSaleMsgInvalidTxCode() {
        System.out.println("sendSaleMsgInvalidTxCode");
        // "60" ต้องใช้ sendSaleMsgUC
        new EDCConnect().sendSaleMsg("60", 100, "3333333333333", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendVoidMsgEmptyInvoice() {
        System.out.println("sendVoidMsgEmptyInvoice");
        new EDCConnect().sendVoidMsg("");
    }

    @Test(expected = IllegalStateException.class)
    public void testSendDataNotConnected() {
        System.out.println("sendDataNotConnected");
        new EDCConnect().sendData(POSMessageGenerator.getSettlementText());
    }
}
