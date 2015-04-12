#!/usr/bin/env bash

if [ $# -lt 1 ]
  then
    echo "Please call like $0 <ip-address (e.g. 192.168.0.1)>"
    exit
fi

WD=$(pwd)
IP=$1

cd ./bin/
java -classpath ${WD}/bin/objects/ -Djava.rmi.server.codebase=file:${WD}/bin/objects/ -Djava.rmi.server.hostname=${IP} -jar client.jar ${IP}