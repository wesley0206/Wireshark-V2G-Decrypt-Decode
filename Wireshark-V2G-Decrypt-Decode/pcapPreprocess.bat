@echo off

echo [33mINFO: Please install java first. (jdk-11.0.14 is recommended.)[0m
echo [33mINFO: Please install Wireshark at C:\Program Files\[0m
echo [33mINFO: This program will use "editcap" to decrypt a pcap file[0m
echo [33mINFO: and "tshark" to filter specific fields.[0m
echo [33mINFO: Please save evcc mac address at evccMACAddr.txt[0m
echo [33mINFO: Please save secc mac address at seccMACAddr.txt[0m
echo.

echo [41mWARNING: If the pcap file contains multi-sessions between one SECC and one EVCC,[0m
echo [41mWARNING: SessionStop must be finished in order to catch the SupportedAppProtocol in the next new round.[0m
echo [41mWARNING: Or save different sessions in separate pcap files.[0m
echo.

echo Input the undecrypted pcap file address...
set /p undecryptedPCAPfileAddr="Undecrypted pcap file address: "

echo Input the key log file address(.log or .txt)
set /p keyLogfileAddr="Key log file address: "
echo.
echo Undecrypted pcap file address: %undecryptedPCAPfileAddr%
echo Key log file address: %keyLogfileAddr%
echo.
echo [1mINFO: Decrypting...[0m
set PATH=%PATH%;C:\Program Files\Wireshark
editcap --inject-secrets tls,%keyLogfileAddr% %undecryptedPCAPfileAddr% out-dsb.pcapng
echo [1mINFO: Undecrypted pcap file is saved as out-dsb.pcapng[0m
echo.

set /p evccMAC=<evccMACAddr.txt
set /p seccMAC=<seccMACAddr.txt

echo [1mINFO: EVCC MAC Address: %evccMAC%[0m
echo [1mINFO: SECC MAC Address: %seccMAC%[0m

IF NOT EXIST csv (
echo [1mINFO: Creating directory csv...[0m
mkdir csv
)

IF NOT EXIST log (
echo [1mINFO: Creating directory log...[0m
mkdir log
)

echo [1mINFO: Dumping UDP packet from out-dsb.pcapng...[0m
tshark -r out-dsb.pcapng -Y "udp and data.data and ((eth.src == %evccMAC% and eth.dst == 33:33:00:00:00:01) or (eth.src == %seccMAC% and eth.dst == %evccMAC%))" -T fields -e frame.number -e frame.time_relative -e data.data -E header=n -E separator=, -E quote=n > csv/udpDump.csv

echo [1mINFO: Dumping TLS packet from out-dsb.pcapng...[0m
tshark -r out-dsb.pcapng -Y "tls and data.data and (eth.addr == %seccMAC% and eth.addr == %evccMAC%)" -T fields -e frame.number -e frame.time_relative -e data.data -E header=n -E separator=, -E quote=n > csv/tlsDump.csv

echo [1mINFO: Converting dumped data to XML format...[0m
cd StartProcessing\demo\
java -jar demo.jar
cd ..
cd ..

echo [43mINFO: Finish[0m
echo [43mINFO: Decode log is saved at log\WiresharkDecodeLog.log[0m