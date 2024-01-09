package com.v2gclarity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;

import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartProcessing {
    private static Logger logger = LogManager.getLogger(StartProcessing.class.getSimpleName()); 

    public static void main(String[] args) {
        MiscUtils.loadProperties(GlobalValues.CONFIG_PROPERTIES_PATH.toString());
        final processmessage pm = new processmessage();
        
        //Process SECCDiscoverymessages
        String udpfilepath = "../csv/udpDump.csv";
        try {
            FileReader udpFileReader = new FileReader(udpfilepath);
            BufferedReader udpBufferReader = new BufferedReader(udpFileReader);

            String line = "";
            getLogger().info("Processing UDP packets..."");
            while ((line = udpBufferReader.readLine()) != null) {
                String[] scanningInput = line.split(",");
                getLogger().info("Frame#: " + scanningInput[0] + ", Time: " + scanningInput[1]);
                String incomingmessage = scanningInput[2];
                //for debug
                //System.out.println("Hex String Data: " + incomingmessage);
                pm.processSDPMessage(incomingmessage);
            }

            udpBufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().error("IOException occured while trying to fetch file from: " + udpfilepath);
        }

        //Process V2GCommunication messages
        String tlsfilepath = "../csv/tlsDump.csv";
        try {
            FileReader tlsFileReader = new FileReader(tlsfilepath);
            BufferedReader tlsBufferReader = new BufferedReader(tlsFileReader);

            String line = "";
            while ((line = tlsBufferReader.readLine()) != null) {
                String[] scanningInput = line.split(",");
                getLogger().info("Frame#: " + scanningInput[0] + ", Time: " + scanningInput[1]);
                String incomingmessage = scanningInput[2];
                //for debug
                //System.out.println("Hex String Data: " + incomingmessage);
                pm.processIncomingMessage(ByteUtils.toByteArrayFromHexString(incomingmessage));
            }

            tlsBufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().error("IOException occured while trying to fetch file from: " + tlsfilepath);
        }
    }

    public static Logger getLogger() {
		return logger;
	}
}