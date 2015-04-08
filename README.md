


Instructions for using the Java RMI:
First, start that RMI registry:

    rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false

I now have the message server working in the sense that it sends the messages to a hardcoded client over RMI.

To run the client server (replace the local ip address):

    java -classpath /home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.codebase=file:/home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.hostname=192.168.1.107 -jar client.jar

To run the code for AnalyticServer:

    java -classpath /home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.codebase=file:/home/wouter/Development/communitysmartgrid/bin/objects/ -jar server.analytic.jar


Is nog niet ideaal, maar we zouden dit kunnen doen ipv de Fake dingen. Dan hebben we iig drie technologien: RMI, Sockets en Message Queue.


