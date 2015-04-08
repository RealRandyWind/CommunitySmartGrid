


Instructions for using the Java RMI.
First, start that RMI registry:

    rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false

I now have the message server working in the sense that it sends the messages to a hardcoded client over RMI.

To run the client server (replace the local ip address):

    java -classpath /home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.codebase=file:/home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.hostname=192.168.1.107 -jar client.jar

To run the code for AnalyticServer:

    java -classpath /home/wouter/Development/communitysmartgrid/bin/objects/ -Djava.rmi.server.codebase=file:/home/wouter/Development/communitysmartgrid/bin/objects/ -jar server.analytic.jar


Is nog niet ideaal, maar we zouden dit kunnen doen ipv de Fake dingen. Dan hebben we iig drie technologien: RMI, Sockets en Message Queue.



**Edit**
Ik heb nu wat bash scriptjes voor het starten gemaakt:

1. Eerst de RMI-registry starten. Deze is alleen nodig als er Remote objecten gehost worden op deze host: ./start_rmi.sh
2. De client starten. Deze host een Client remote object: ./start_client.sh <ip-address>
3. De message server starten. Deze geeft de actions die via RabbitMQ worden ontvangen van de AnalyticServer door naar de Client via RMI. ./start_message.sh
4. De analytic server. Deze doet wat debug code om een dummy action in de Message Queue te plaatsen. ./start_analytic.sh

Last but not least: pas even de hardcoded TestClientHost in Config.java aan naar het ip-adres van de host waar de client op wordt gedraaid.