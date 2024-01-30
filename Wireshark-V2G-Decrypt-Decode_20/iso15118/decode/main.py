import csv
import os
import logging
import datetime

from iso15118.shared.messages.v2gtp import V2GTPMessage
from iso15118.shared.comm_session import EXI
from iso15118.shared.messages.sdp import SDPRequest, SDPResponse
from iso15118.shared.messages.enums import Namespace, Protocol, ISOV20PayloadTypes, ISOV2PayloadTypes

logger = logging.getLogger(__name__)

def setup_logging():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    print(script_dir)
    current_time = datetime.datetime.now()
    timestamp = current_time.strftime("%Y%m%d_%H%M%S")
    log_file_path = os.path.join(script_dir, f'Decode_result_{timestamp}.txt')
    
    os.makedirs(os.path.dirname(log_file_path), exist_ok=True)

    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

    file_handler = logging.FileHandler(log_file_path)
    file_handler.setLevel(logging.INFO)
    file_handler.setFormatter(logging.Formatter('%(asctime)s - %(levelname)s - %(message)s'))
    logging.getLogger().addHandler(file_handler)

def process_udp_packets(csv_file_path):
    logger.info("-------- Processing UDP packets --------")
    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.reader(csv_file)
        for row in csv_reader:
            logger.info(f"Frame#: {row[0]}, Time: {row[1]}")
            hex_msg = row[2]
            payload_type = "0x" + hex_msg[4:8]
            byte_array_msg = bytes.fromhex(row[2])
            
            if payload_type == hex(ISOV20PayloadTypes.SDP_REQUEST.value):
                SDPReq = SDPRequest.from_payload(byte_array_msg[-2:])
                logger.info(f"SECCDiscoveryReq Settings : {SDPReq}")
            elif payload_type == hex(ISOV20PayloadTypes.SDP_RESPONSE.value):
                SDPRes = SDPResponse.from_payload(byte_array_msg[-20:])
                logger.info(f"SECCDiscoveryRes Settings : {SDPRes}")
            else:
                raise ValueError("not a valid SDP message")

def process_tls_packets(csv_file_path):
    logger.info("-------- Processing TLS packets --------")
    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.reader(csv_file)
        for index, row in enumerate(csv_reader):
            # Processing SupportedAppProtocolRes/Req.
            if index == 0 or index == 1:
                process_supported_protocol(row)
            else:
                process_other_v2gtp_messages(row)

def process_supported_protocol(row):
    logger.info(f"Frame#: {row[0]}, Time: {row[1]}")
    hex_msg = row[2]
    if hex_msg.startswith("01fe"):
        payload_type = "0x" + hex_msg[4:8]
        byte_array_msg = bytes.fromhex(row[2])
        logger.info("V2GTP message :")
        
        if payload_type == hex(ISOV20PayloadTypes.SAP.value):
            namespace = Namespace.SAP.value
            protocol = Protocol.ISO_15118_20_COMMON_MESSAGES
        else:
            raise ValueError("not a valid V2GTP message")
    else:
        raise ValueError("not a valid V2GTP message")

    v2gmsg = V2GTPMessage.from_bytes(protocol, byte_array_msg)
    print(v2gmsg)

    # Decoding
    Decode_tls = EXI()
    Decode_tls.from_exi(v2gmsg.payload, namespace)

def process_other_v2gtp_messages(row):
    logger.info(f"Frame#: {row[0]}, Time: {row[1]}")
    hex_msg = row[2]
    if hex_msg.startswith("01fe"):
        payload_type = "0x" + hex_msg[4:8]
        byte_array_msg = bytes.fromhex(row[2])
        logger.info("V2GTP message :")
        
        if payload_type == hex(ISOV2PayloadTypes.EXI_ENCODED.value):
            namespace = Namespace.ISO_V20_COMMON_MSG.value
            protocol = Protocol.ISO_15118_20_COMMON_MESSAGES
        elif payload_type == hex(ISOV20PayloadTypes.MAINSTREAM.value):
            namespace = Namespace.ISO_V20_COMMON_MSG.value
            protocol = Protocol.ISO_15118_20_COMMON_MESSAGES
        elif payload_type == hex(ISOV20PayloadTypes.AC_MAINSTREAM.value):
            namespace = Namespace.ISO_V20_AC.value
            protocol = Protocol.ISO_15118_20_AC
        else:
            raise ValueError("not a valid V2GTP message")
    else:
        raise ValueError("not a valid V2GTP message")

    v2gmsg = V2GTPMessage.from_bytes(protocol, byte_array_msg)
    print(v2gmsg)

    # Decoding
    Decode_tls = EXI()
    Decode_tls.from_exi(v2gmsg.payload, namespace)

def main():
    setup_logging()

    script_dir = os.path.dirname(os.path.abspath(__file__))

    udp_csv_file_path = os.path.join(script_dir, 'udpDump.csv')
    process_udp_packets(udp_csv_file_path)

    tls_csv_file_path = os.path.join(script_dir, 'tlsDump.csv')
    process_tls_packets(tls_csv_file_path)

    logger.info("-------- End of packets processing --------")

if __name__ == "__main__":
    main()
