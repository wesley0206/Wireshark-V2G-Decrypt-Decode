# Wireshark-V2G-Decrypt-Decode
Decrypt a TLS encrypted V2G communication session captured by Wireshark in a pcap file.
Decode the messages to human-readble XML format.
Start decrypting and decoding by simply executing **pcapPreprocess.bat**.

## How to Run pcapPreprocess.bat?
* **Prerequisite**:
  * Save evcc mac address at **evccMACAddr.txt**, and secc mac address at **seccMACAddr.txt**. We need mac address to filter the captured traffic.
  * If the pcap file contains multiple sessions between one SECC and one EVCC, SessionStop must be finished (i.e., no exception occurred) in order to catch the SupportedAppProtocol in the next new round. Or you can save different sessions in separate pcap files.
  * This program uses java to decode the message. Please install java (jdk-11.0.14 is recommended).
  * Please install Wireshark at **C:\Program Files**. This program uses **editcap** to decrypt a pcap file, and **tshark** to filter specific fields from a pcap file by 
```
set PATH=%PATH%;C:\Program Files\Wireshark
``` 
* Double click on pcapPreprocess.bat or open in cmd.exe.

## Executing
* A message will be prompted asking for input of the undecrypted pcap file address. Drag the undecrypted pcap file into the executing window.
* Another message will be prompted asking for input of the key log file address. Drag the key log file (.log or .txt) into the executing window.
* Start decrypting. After decrypting, the decrypted pcap file will be saved as **out-dsb.pcapng** at the same level.
* Create directories **csv** and **log** if not existed.
* Start dumping packets from the decrypted pcap file.
  * UDP packets will be saved at **csv/udpDump.csv**.
  * TLS packets will be saved at **csv/tlsDump.csv**.
* Start coverting dumped data to human-readble XML format by executing **StartProcessing\demo\demo.jar**.
* The final result is saved at **log\WiresharkDecodeLog.log**.
