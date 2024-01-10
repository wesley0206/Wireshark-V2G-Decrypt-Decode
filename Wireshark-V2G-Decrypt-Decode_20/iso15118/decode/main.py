import csv
import os
import logging

from iso15118.shared.messages.v2gtp import V2GTPMessage
from iso15118.shared.comm_session import EXI
from iso15118.shared.messages.sdp import SDPRequest, SDPResponse
from iso15118.shared.messages.enums import Namespace, Protocol, ISOV20PayloadTypes, ISOV2PayloadTypes

logger = logging.getLogger(__name__)

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    csv_file_path = os.path.join(script_dir, 'udpDump.csv')
    print("-------- Processing UDP packets --------")
    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.reader(csv_file)
        
        for row in (csv_reader):
            logger.info("Frame#: " + row[0] + ", Time: " + row[1])
            hex_msg = row[2]
            payload_type = "0x" + hex_msg[4:8]
            byte_array_msg = bytes.fromhex(row[2])
            
            if payload_type == hex(ISOV20PayloadTypes.SDP_REQUEST.value):
                SDPReq = SDPRequest.from_payload(byte_array_msg[-2:])
                print("SECCDiscoveryReq Settings : " + str(SDPReq))
            
            elif payload_type == hex(ISOV20PayloadTypes.SDP_RESPONSE.value):
                SDPRes = SDPResponse.from_payload(byte_array_msg[-20:])
                print("SECCDiscoveryRes Settings : " + str(SDPRes))
                
            else :
                raise ValueError("not a valid SDP message")
            
    print("-------- End of UDP packets --------")
            
    script_dir = os.path.dirname(os.path.abspath(__file__))
    csv_file_path = os.path.join(script_dir, 'tlsDump.csv')
    print("-------- Processing TLS packets --------")
    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.reader(csv_file)

        for index, row in enumerate(csv_reader):
            # Processing SupportedAppProtocolRes/Req.
            if index == 0 or index == 1:
                logger.info("Frame#: " + row[0] + ", Time: " + row[1])
                hex_msg = row[2]
                if hex_msg[0:4] == "01fe" :
                    payload_type = "0x" + hex_msg[4:8]
                    byte_array_msg = bytes.fromhex(row[2])
                    print("V2GTP message :")
                    
                    if payload_type == hex(ISOV20PayloadTypes.SAP.value):
                        payload_type = hex(ISOV20PayloadTypes.SAP.value)
                        namespace = Namespace.SAP.value
                        protocol = Protocol.ISO_15118_20_COMMON_MESSAGES
                        
                    else :
                        raise ValueError("not a valid V2GTP message")
                else :
                        raise ValueError("not a valid V2GTP message")
                
            # Processing other V2GTP messages.
            else:
                logger.info("Frame#: " + row[0] + ", Time: " + row[1])
                hex_msg = row[2]
                if hex_msg[0:4] == "01fe" :
                    payload_type = "0x" + hex_msg[4:8]
                    byte_array_msg = bytes.fromhex(row[2])
                    print("V2GTP message :")
                    
                    if payload_type == hex(ISOV2PayloadTypes.EXI_ENCODED.value):
                        namespace = Namespace.ISO_V20_COMMON_MSG.value
                        protocol = Protocol.ISO_15118_20_COMMON_MESSAGES
                        
                        
                    elif payload_type == hex(ISOV20PayloadTypes.MAINSTREAM.value):
                        namespace = Namespace.ISO_V20_COMMON_MSG.value
                        protocol = Protocol.ISO_15118_20_COMMON_MESSAGES
                        
                    elif payload_type == hex(ISOV20PayloadTypes.AC_MAINSTREAM.value):
                        namespace = Namespace.ISO_V20_AC.value
                        protocol = Protocol.ISO_15118_20_AC
                        
                    else :
                        raise ValueError("not a valid V2GTP message")
                else :
                        raise ValueError("not a valid V2GTP message")
               
            v2gmsg = V2GTPMessage.from_bytes(protocol, byte_array_msg)
            print(v2gmsg)
            
            # Decoding
            Decode_tls = EXI()
            Decode_tls.from_exi(v2gmsg.payload, namespace)
            
    print("-------- End of TLS packets --------")
    
if __name__ == "__main__":
    main()
