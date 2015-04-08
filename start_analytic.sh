#!/usr/bin/env bash

WD=$(pwd)

cd ./bin/
java -classpath ${WD}/bin/objects/ -Djava.rmi.server.codebase=file:${WD}/bin/objects/ -jar server.analytic.jar
