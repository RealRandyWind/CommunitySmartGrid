**How to make the software**  
To compile the Java source code, simply execute:
    make dirs
    make
    make apps

The first command will make the needed directories. The second will compile the code used by all the servers. And the third command makes the jar’s for the actual servers.

**How to run the software**  
In order to run the software it is a prerequisite that both RabbitMQ and RMI are installed on the system. RMI often comes pre-installed on most systems, but RabbitMQ usually not.
Because the commands needed to start some of the servers, especially those hosting RMI objects, became quite lengthy, we have created a few bash scripts for this. 

Before starting anything, configuration of the server ip-addresses is necessary in /src/Config.java.

First, the RMI registry has to be started, then all of the servers and after that, the clients can start. In the current implementation (for demonstration purposes), a client is started with a single device.

    ~/communitysmartgrid $    ./start_rmi.sh
    ~/communitysmartgrid $    ./start_collection.sh
    ~/communitysmartgrid $    ./start_analytic.sh
    ~/communitysmartgrid $    ./start_message.sh

To start a client, some command line parameters are needed:
- The ip-address of the client
- The potential production of the device (this is the energy an energy source can produce, 0 if it is an energy sink)
- The potential usage of the device (this is the energy an energy sink can use, 0 if it is an energy source)
- The usage (this is the energy the device currently uses)


Examples of commands to start a client: 
A device that uses 100 watt (its maximum usage):

    ~/communitysmartgrid $    ./start_client.sh 192.168.1.197 0 100 100

A device that is producing 50 watt, but can produce 100. The smart grid will send it an action telling to produce more energy if this is needed:

    ~/communitysmartgrid $    ./start_client.sh 192.168.1.197 100 0 -50

A device that is using 50 energy, but can use 100. If there is an energy surplus in the network, it will be send an action to use more energy.

    ~/communitysmartgrid $    ./start_client.sh 192.168.1.197 0 100 50

