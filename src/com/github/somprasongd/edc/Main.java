/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.somprasongd.edc;

import com.github.somprasongd.edc.util.EDCConnect;
import static com.github.somprasongd.edc.util.EDCConnect.listSerialPorts;
import com.github.somprasongd.edc.util.POSMessageGenerator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sompr
 */
public class Main {

    public static void main(String[] args) {
        List<String> listPorts = listSerialPorts();
        System.out.println(listPorts.size());

        if (listPorts.isEmpty()) {
            return;
        }

        String port = listPorts.get(0);

        EDCConnect edcc = new EDCConnect();
        try {
            edcc.connect(port);
            String msg = POSMessageGenerator.getSaleText("11", 20.50, "1234567890123", null, null);
            edcc.sendData(msg);
        } catch (Exception ex) {
            Logger.getLogger(EDCConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
