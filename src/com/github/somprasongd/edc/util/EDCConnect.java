/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.somprasongd.edc.util;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sompr
 */
public class EDCConnect implements Observer {

    private static final Logger LOG = Logger.getLogger(EDCConnect.class.getName());

    private final OReaderAgency oReaderAgency = new OReaderAgency();
    private final ONotifyAgency oNotifyAgency = new ONotifyAgency();

    public EDCConnect() {
        oReaderAgency.addObserver(this);
    }

    private SerialPort serialPort;

    public void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            throw new Exception("Error: Port is currently in use.");
        }

        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

        if (!(commPort instanceof SerialPort)) {
            throw new Exception("Error: Use serial ports only.");
        }
        this.close();
        serialPort = (SerialPort) commPort;
        // Doc V 1.10 page 8
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        InputStream in = serialPort.getInputStream();

        serialPort.addEventListener(new SerialReader(in));
        serialPort.notifyOnDataAvailable(true);
    }

    public void close() {
        if (serialPort != null) {
            serialPort.close();
        }
    }

    /**
     *
     * @param hexString Example 02 00 29 30 30 30 30 30 30 30 30 30 30 31 30 32
     * 36 30 30 30 1C 36 35 00 06 31 30 30 30 39 38 1C 03 18
     */
    public void sendData(String hexString) {
        try {
            OutputStream out = serialPort.getOutputStream();
            String[] split = hexString.split(" ");

            byte[] bs = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                bs[i] = Byte.parseByte(split[i], 16);
            }
            out.write(bs);
            out.flush();
            out.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void addNotifyObserver(Observer observer) {
        oNotifyAgency.addObserver(observer);
    }

    @Override
    public void update(Observable o, Object edcMsg) {
        String hexString = (String) edcMsg;
        LOG.log(Level.INFO, "Retrieved: {0}", hexString);
        // Send Acknowledge (06 Hex) back to EDC
        sendData("06");
        LinkedHashMap<String, Object> reponseMessageObject = POSMessageGenerator.getReponseMessageObject(hexString);
        oNotifyAgency.sendEDCMessage(reponseMessageObject);
    }

    public void sendSaleMsg(String txCode, double amount,
            String ownerCardNo, String childCardNo, String foreignerCardNo) {
        String saleText = POSMessageGenerator.getSaleText(txCode, amount, ownerCardNo, childCardNo, foreignerCardNo);
        this.sendData(saleText);
    }

    public void sendVoidMsg(String invoiceNumber) {
        String saleText = POSMessageGenerator.getVoidText(invoiceNumber);
        this.sendData(saleText);
    }

    public void sendRePrintMsg(String invoiceNumber) {
        String saleText = POSMessageGenerator.getRePrintText(invoiceNumber);
        this.sendData(saleText);
    }

    public void sendSettlementMsg() {
        String saleText = POSMessageGenerator.getSettlementText();
        this.sendData(saleText);
    }

    /**
     * Handles the input coming from the serial port. A new line character is
     * treated as the end of a block in this example.
     */
    private class SerialReader implements SerialPortEventListener {

        private final InputStream in;
        private final byte[] buffer = new byte[1024];

        public SerialReader(InputStream in) {
            this.in = in;
        }

        @Override
        public void serialEvent(SerialPortEvent arg0) {
            int data;
            try {
                int len = 0;
                while ((data = in.read()) > -1) {
                    if (data == '\n') {
                        break;
                    }
                    buffer[len++] = (byte) data;
                }
                String hex = HexConverter.asciiToHexWithSpace(new String(buffer, 0, len));
                if (!hex.equals("06")) {
                    oReaderAgency.setRetrieveEDCMessage(hex);
                }

            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }

    }

    public static List<String> listSerialPorts() {
        return listPorts(CommPortIdentifier.PORT_SERIAL);
    }

    public static List<String> listPorts() {
        return listPorts(-1);
    }

    public static List<String> listPorts(int portType) {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        List<String> ports = new ArrayList<String>();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            LOG.log(Level.INFO, "{0} - {1}", new Object[]{portIdentifier.getName(), getPortTypeName(portIdentifier.getPortType())});
            if (portType == -1 || portIdentifier.getPortType() == portType) {
                ports.add(portIdentifier.getName());
            }
        }
        return ports;
    }

    private static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }

    private class OReaderAgency extends Observable {

        public void setRetrieveEDCMessage(String edcMsg) {
            setChanged();
            notifyObservers(edcMsg);
        }
    }

    private class ONotifyAgency extends Observable {

        public void sendEDCMessage(LinkedHashMap<String, Object> reponseMessageObject) {
            setChanged();
            notifyObservers(reponseMessageObject);
        }
    }
}
