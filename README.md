


Instructions for using the Java RMI:
First, start that RMI registry:

    rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false

I have implemented small example in the Client/AnalyticServer to have the analytic server  call using RMI on remote Client object.

To run the client server:

    java -classpath /home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.codebase=file:/home/wouter/Development/communitysmartgrid/bin/objects/ -jar client.jar

To run the code for AnalyticServer:

    java -classpath /home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.codebase=file:/home/wouter/Development/communitysmartgrid/bin/objects/ -jar server.analytic.jar


Is nog niet ideaal, maar we zouden dit kunnen doen ipv de Fake dingen. Dan hebben we iig drie technologien: RMI, Sockets en Message Queue.


