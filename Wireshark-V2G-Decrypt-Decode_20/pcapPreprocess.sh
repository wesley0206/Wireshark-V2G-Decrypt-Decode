#!/bin/bash

echo -e "\e[33mINFO: Please install Wireshark at /usr/bin/\e[0m"
echo -e "\e[33mINFO: This program will use 'editcap' to decrypt a pcap file\e[0m"
echo -e "\e[33mINFO: and 'tshark' to filter specific fields.\e[0m"
echo -e "\e[33mINFO: Please save evcc mac address at evccMACAddr.txt\e[0m"
echo -e "\e[33mINFO: Please save secc mac address at seccMACAddr.txt\e[0m"
echo

echo -e "\e[41mWARNING: If the pcap file contains multi-sessions between one SECC and one EVCC,\e[0m"
echo -e "\e[41mWARNING: SessionStop must be finished in order to catch the SupportedAppProtocol in the next new round.\e[0m"
echo -e "\e[41mWARNING: Or save different sessions in separate pcap files.\e[0m"
echo

read -p "Input the undecrypted pcap file address: " undecryptedPCAPfileAddr
read -p "Input the key log file address (.log or .txt): " keyLogfileAddr
cd iso15118/decode
echo
echo "Undecrypted pcap file address: $undecryptedPCAPfileAddr"
echo "Key log file address: $keyLogfileAddr"
echo
echo -e "\e[1mINFO: Decrypting...\e[0m"
export PATH=$PATH:/usr/bin
editcap --inject-secrets tls,"$keyLogfileAddr" "$undecryptedPCAPfileAddr" out-dsb.pcapng
echo -e "\e[1mINFO: Undecrypted pcap file is saved as out-dsb.pcapng\e[0m"
echo

evccMAC=$(cat evccMACAddr.txt)
seccMAC=$(cat seccMACAddr.txt)

echo -e "\e[1mINFO: EVCC MAC Address: $evccMAC\e[0m"
echo -e "\e[1mINFO: SECC MAC Address: $seccMAC\e[0m"

if [ ! -d csv ]; then
  echo -e "\e[1mINFO: Creating directory csv...\e[0m"
  mkdir csv
fi

if [ ! -d log ]; then
  echo -e "\e[1mINFO: Creating directory log...\e[0m"
  mkdir log
fi

echo -e "\e[1mINFO: Dumping UDP packet from out-dsb.pcapng...\e[0m"
tshark -r out-dsb.pcapng -Y "udp and data.data and ((eth.src == $evccMAC and eth.dst == 33:33:00:00:00:01) or (eth.src == $seccMAC and eth.dst == $evccMAC))" -T fields -e frame.number -e frame.time_relative -e data.data -E header=n -E separator=, -E quote=n > csv/udpDump.csv

echo -e "\e[1mINFO: Dumping TLS packet from out-dsb.pcapng...\e[0m"
tshark -r out-dsb.pcapng -Y "tls and data.data and (eth.addr == $seccMAC and eth.addr == $evccMAC)" -T fields -e frame.number -e frame.time_relative -e data.data -E header=n -E separator=, -E quote=n > csv/tlsDump.csv

echo -e "\e[1mINFO: Converting dumped data to JSON format...\e[0m"
cd ..
cd ..
make install-local
poetry shell
meke run-decode

echo -e "\e[43mINFO: Finish\e[0m"
echo -e "\e[43mINFO: Decode log is saved at log/WiresharkDecodeLog.log\e[0m"
