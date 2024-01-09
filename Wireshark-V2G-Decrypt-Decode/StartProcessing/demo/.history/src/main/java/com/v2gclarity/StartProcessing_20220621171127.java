/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
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
        String udpfilepath = "/Users/jacobhsieh/Desktop/WiresharkDecrypt/csv/udpDump.csv";
        try {
            FileReader udpFileReader = new FileReader(udpfilepath);
            BufferedReader udpBufferReader = new BufferedReader(udpFileReader);

            String line = "";
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
            System.out.println("IOException occured while trying to fetch file from: " + udpfilepath);
        }

        //Process V2GCommunication messages
        String tlsfilepath = "/Users/jacobhsieh/Desktop/WiresharkDecrypt/csv/tlsDump.csv";
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
            System.out.println("IOException occured while trying to fetch file from: " + tlsfilepath);
        }
    }

    public static Logger getLogger() {
		return logger;
	}
}