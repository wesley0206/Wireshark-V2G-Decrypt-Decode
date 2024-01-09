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
package com.v2gclarity.risev2g.shared.enumerations;

import java.util.Arrays;

import com.v2gclarity.risev2g.shared.utils.ByteUtils;


public enum GlobalValues {
	/* Properties files at the same level as the JAR. 
	 */
	CONFIG_PROPERTIES_PATH("./Config.properties"),

	// SECC Discovery Protocol (SDP) multicast address (see [V2G2-139]) and port (see table 15, page 36) 
	SDP_MULTICAST_ADDRESS("FF02::1"),
	V2G_UDP_SDP_SERVER_PORT((short) 15118, GlobalTypes.PORT),
	
	/*
	 * Maximum payload length allowed in bytes (0 ... 4294967295), see Table 9
	 * With Integer being a signed type in Java, we need to multiply the maximum value of Integer by 2
	 */
	V2GTP_HEADER_MAX_PAYLOAD_LENGTH((long) Integer.MAX_VALUE * 2, GlobalTypes.PAYLOAD_LENGTH),
	
	// Protocol version of V2GTP messages (1 = IS compliant), see Table 9
	V2GTP_VERSION_1_IS(ByteUtils.toByteFromHexString("01"), GlobalTypes.PROTOCOL_VERSION),
	
	// Schema information
	V2G_CI_AC_NAMESPACE("urn:iso:std:iso:15118:-20:CommonTypes"),
	V2G_CI_ACDP_NAMESPACE("urn:iso:std:iso:15118:-20:CommonTypes"), 
	V2G_CI_CommonMessages_NAMESPACE("urn:iso:std:iso:15118:-20:CommonTypes"),
	V2G_CI_CommonTypes_NAMESPACE("http://www.w3.org/2000/09/xmldsig#"), 
	V2G_CI_DC_NAMESPACE("urn:iso:std:iso:15118:-20:CommonTypes"), 
	V2G_CI_WPT_NAMESPACE("urn:iso:std:iso:15118:-20:CommonTypes"), 
	V2G_CI_XMLDSIG_NAMESPACE("http://www.w3.org/2000/09/xmldsig#"), 
	SCHEMA_PATH_APP_PROTOCOL("/schemas/V2G_CI_AppProtocol.xsd"),
	SCHEMA_PATH_AC("/schemas/iso15118_20/V2G_CI_AC.xsd"),
	SCHEMA_PATH_ACDP("/schemas/iso15118_20/V2G_CI_ACDP.xsd"),
	SCHEMA_PATH_CommonMessages("/schemas/iso15118_20/V2G_CI_CommonMessage.xsd"),
	SCHEMA_PATH_CommonTypes("/schemas/iso15118_20/V2G_CI_CommonTypes.xsd"),
	SCHEMA_PATH_DC("/schemas/iso15118_2/V2G_CI_DC.xsd"),
	SCHEMA_PATH_WPT("/schemas/iso15118_2/V2G_CI_WPT.xsd"),
	SCHEMA_PATH_XMLDSIG("/schemas/xmldsig-core-schema.xsd"),
	
	// Encoding for the requested security option (see [V2G2-623])
	V2G_SECURITY_WITH_TLS(ByteUtils.toByteFromHexString("00"), GlobalTypes.SECURITY),
	V2G_SECURITY_WITHOUT_TLS(ByteUtils.toByteFromHexString("10"), GlobalTypes.SECURITY),
	
	// Encoding for requested transport protocol (see [V2G2-623])
	V2G_TRANSPORT_PROTOCOL_TCP(ByteUtils.toByteFromHexString("00"), GlobalTypes.TRANSPORT_PROTOCOL),
	V2G_TRANSPORT_PROTOCOL_UDP(ByteUtils.toByteFromHexString("10"), GlobalTypes.TRANSPORT_PROTOCOL),
	
	// Payload types
	ISO_15118_2_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE(ByteUtils.toByteArrayFromHexString("8001"), GlobalTypes.PAYLOAD_TYPE),
	ISO_15118_20_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE(ByteUtils.toByteArrayFromHexString("8002"), GlobalTypes.PAYLOAD_TYPE),
	ISO_15118_20_AC_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE(ByteUtils.toByteArrayFromHexString("8003"), GlobalTypes.PAYLOAD_TYPE),
	ISO_15118_20_DC_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE(ByteUtils.toByteArrayFromHexString("8004"), GlobalTypes.PAYLOAD_TYPE),
	ISO_15118_20_ACDP_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE(ByteUtils.toByteArrayFromHexString("8005"), GlobalTypes.PAYLOAD_TYPE),
	ISO_15118_20_WPT_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE(ByteUtils.toByteArrayFromHexString("8006"), GlobalTypes.PAYLOAD_TYPE),
	V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE(ByteUtils.toByteArrayFromHexString("9000"), GlobalTypes.PAYLOAD_TYPE),
	V2GTP_PAYLOAD_TYPE_SDP_RESPONSE_MESSAGE(ByteUtils.toByteArrayFromHexString("9001"), GlobalTypes.PAYLOAD_TYPE);
	
	
	
	private GlobalTypes type;
	private byte byteValue;
	private byte[] byteArrayValue;
	private short shortValue;
	private long longValue;
	private String stringValue;
	
	private GlobalValues(byte byteValue, GlobalTypes type) {
		this.byteValue = byteValue;
		this.type = type;
	}
	
	private GlobalValues(byte[] byteArrayValue, GlobalTypes type) {
		this.byteArrayValue = byteArrayValue;
		this.type = type;
	}
	
	private GlobalValues(short shortValue, GlobalTypes type) {
		this.shortValue = shortValue;
		this.type = type;
	}
	
	private GlobalValues(long longValue, GlobalTypes type) {
		this.longValue = longValue;
		this.type = type;
	}
	
	private GlobalValues(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public byte getByteValue() {
		return byteValue;
	}	
	
	public byte[] getByteArrayValue() {
		return byteArrayValue;
	}
	
	public long getLongValue() {
		return longValue;
	}
	
	public short getShortValue() {
		return shortValue;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case CONFIG_PROPERTIES_PATH:
			 return stringValue;
		case SDP_MULTICAST_ADDRESS:
			 return stringValue;
		case V2G_UDP_SDP_SERVER_PORT:
			 return stringValue;
		case V2GTP_HEADER_MAX_PAYLOAD_LENGTH:
			 return "4294967295 bytes";
		case V2GTP_VERSION_1_IS:
			 return "version 1 (IS compliant)";
		case V2G_CI_AC_NAMESPACE:
			 return stringValue;
		case V2G_CI_ACDP_NAMESPACE:
			 return stringValue;
		case V2G_CI_CommonMessages_NAMESPACE:
			 return stringValue;
		case V2G_CI_CommonTypes_NAMESPACE:
			 return stringValue;
		case V2G_CI_DC_NAMESPACE:
			 return stringValue;
		case V2G_CI_WPT_NAMESPACE:
			 return stringValue;
		case V2G_CI_XMLDSIG_NAMESPACE:
			 return stringValue;
		case SCHEMA_PATH_APP_PROTOCOL:
			 return stringValue;
		case SCHEMA_PATH_AC:
			 return stringValue;
		case SCHEMA_PATH_ACDP:
			 return stringValue;
		case SCHEMA_PATH_CommonMessages:
			 return stringValue;
		case SCHEMA_PATH_CommonTypes:
			 return stringValue;
		case SCHEMA_PATH_DC:
			 return stringValue;
		case SCHEMA_PATH_WPT:
			 return stringValue;
		case SCHEMA_PATH_XMLDSIG:
			 return stringValue;
		case V2G_SECURITY_WITH_TLS:
			 return "TLS enabled";
		case V2G_SECURITY_WITHOUT_TLS:
			 return "TLS disabled";
		case V2G_TRANSPORT_PROTOCOL_TCP:
			 return "transport protocol TCP";
		case V2G_TRANSPORT_PROTOCOL_UDP:
			 return "transport protocol UDP";
		case ISO_15118_2_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE:
			 return "ISO 15118 -2 EXI encoded Message";
		case ISO_15118_20_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE:
			 return "ISO 15118 -20 EXI encoded Message";
		case ISO_15118_20_AC_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE:
			 return "ISO 15118 -20 AC EXI encoded Message";
		case ISO_15118_20_DC_V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE:
			 return "ISO 15118 -20 DC EXI encoded Message";
		default: return "Invalid GlobalValue type";
		}
	}
	
	/*
	 * This method needs the second parameter from type GlobalTypes to distinguish between byte
	 * values which may be equal, e.g. TRANSPORT_PROTOCOL and SECURITY, which both may take values
	 * such as 0x00 or 0x01.
	 */
	public static String toString(byte[] globalValue, GlobalTypes globalType) {
		String globalFound = "";
		
		for (GlobalValues glValue : GlobalValues.values()) {
			if (glValue.type == globalType && Arrays.equals(glValue.byteArrayValue, globalValue)) {
				globalFound = glValue.toString();
				break;
			}
		}

		return globalFound;
	}
	
	/*
	 * This method needs the second parameter from type GlobalTypes to distinguish between byte array
	 * values which may be equal.
	 */
	public static String toString(byte globalValue, GlobalTypes globalType) {
		String globalFound = "";
		
		for (GlobalValues glValue : GlobalValues.values()) {
			if (glValue.type == globalType && glValue.byteValue == globalValue) {
				globalFound = glValue.toString();
				break;
			}
		}

		return globalFound;
	}

}
