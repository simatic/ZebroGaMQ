TCM: TOTEM Communication Middleware
Copyright: Copyright (C) 2009-2012
Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA

Developer(s): Denis Conan, Gabriel Adgeg


Prerequisites
=============

1/ Softwares
------------

– RabbitMQ Server version 2.7.1,
– Pika version ≥ 0.9.5,
– Node.js ≥ 0.4.10,
– Java ≥ 1.5,
– Python ≥ 2.6

2/ Installation procedure
-------------------------

Please refer to the "Presentation of the TOTEM Communication Infrastructure"
document located in the SVN repository at the following location:
Sandbox/TSP-MARGE-Communication/Writings/CommunicationMiddleware/document.pdf:
- The appendix A.1 describes the installation procedure for the RabbitMQ broker
  on desktop computers,
- The appendix A.3 describes the installation procedure for Pika, and
- The appendix A.4 describes the installation procedure for Node.js and its
  required libraries.

Demonstration
=============

Here is the scenario for a demonstration that includes:
- a master, using a Web application
- a spectator, using a Web application
- a player, using an Android application

0/ Example compilation execution
--------------------------------

An example of the messages that are displayed when executing this scenario
is provided in the file ./EXAMPLE_EXEC.txt.

1/ Setting IP addresses
-----------------------

In the following, we explain how to set the IP addresses that you will use for:
- the GameServer,
- the TerminationApplication,
- the NodeJsProxy for Web applications,
- the PlayerApplicationAndroid.

You cannot use the default localhost address because of the Android application.

Here are the configuration files you need to update:
- GameServer/rabbitmq.properties & GameServer/xmlrpc.properties
- TerminationApplication/xmlrpc.properties
- PlayerApplicationAndroid/res/raw/rabbitmq.properties & 
  PlayerApplicationAndroid/res/raw/xmlrpc.properties.
- NodeJsProxy/resources/config.properties &
  NodeJsProxy/resources/rabbitmq.properties

2/ Starting the demonstration
-----------------------------

2.1 Start the server side by launching the following command in a first shell:
    $ ./run_with_master_and_spectator_javascript_applications.sh

    This will start:
    - the RabbitMQ broker
    - the GameServer
    - the NodeJsProxy

    The following text will be displayed once the NodeJsProxy would be started:
    
    "Instructions for the sequel of the demonstration:
     1/ Start the Master application on your Web browser
     2/ Start the Spectator application on your Web browser
     3/ (Optional) Start the PlayerMasterApplication on an Android Device,"
         press the menu button, and click on \"Join Instance\"."
     
     Finally, to properly stop the demonstration,
     execute the termination.sh script located in the current directory."

     Note that the RabbitMQTOTEMLibrary directory is required by the 
     PYTHON_PATH, which is set in the script GameServer/run.sh. So you must 
     have also downloaded this directory before executing the script 
     run_with_master_and_spectator_javascript_applications.sh


2.2 Launch the Web application for the Master

    Open a Web browser, and go to the following URL:
    http:NODE_PROXY_HOST//:8001/Master (where NODE_PROXY_HOST is the address 
    that you have just set in NodeJsProxy/resources/config.properties).
    The master will log in, create a game instance, and join that game
    instance.

    Be careful! We do not allow for several Master applications in this
                demonstration.

2.3 Launch the Web application for the Spectator

    Open a Web browser, and go to the following URL:
    http:NODE_PROXY_HOST//:8001/Spectator (where NODE_PROXY_HOST is the address 
    that you have just set for the variable NodeJsProxy in 
    NodeJsProxy/proxy.js).
    Enter a name for your spectator and click on the "Join" button.
    The spectator will log in and join the game instance.

    NB: You can have as many spectator applications as you want, provided that
    	you give different spectator's names.

2.4 Launch the Android application for the player

    PlayerMasterAndroid is an Eclipse project. You need to start Eclipse,
    import this project, and run it (whether on an Android device or on an 
    emulator) as an Android Application.
    Press the menu button, and click on "Join Instance".

2.5 Messages exchanged

    The master, a spectator and a player should have logged in and and should
    have joined the game instance. Here are their behavior:
    - the master and the players periodically send heartbeat messages and
      messages to ask the game logic server for the list of participants.
      (The spectators are not considered as participants. So, they send neither
       the heartbeat messages nor the message to request the participants list.)
    - the spectators receive all the messages sent by the master, the players
      and the game logic server.

3/ Terminating the demonstration
--------------------------------

Terminate the demonstration on both server and clients side by launching the 
following command in a second shell:
$ ./termination.sh


Run the demonstration with PyDev
================================

PyDev is an Eclipse plugin for Python development. Its installation procedure 
is described here: http://pydev.org/download.html

GameServer and GameLogicServer are PyDev projects. Here are the required steps 
to import, configure and run/debug those projects within PyDev.

1/ Make sure that you have set at least one Python interpreter:
   Windows > Preferences > PyDev > Interpreter - Python.
   Otherwise, add a new one, specifying the installation directory of your
   Python interpreter. 

2/ Add pika to the libraries.
   In the same window, in the "Libraries", select "New Folder", and add the 
   installation directory of pika. Then click on "Apply", and "OK".

3/ Import GameLogicServer.
   In the PyDev Package Explorer, right-click > import > General > Existing
   Projects into Workspace. 
   Once you have specify the location of GameLogicServer, you may have to 
   change the interpreter, if it is not yours.

4/ Import GameServer, the same way you have just done with GameLogicServer.

5/ Add a reference to the TOTEM Library in GameServer.
   Right-click on your GameServer project > PyDev - PYTHONPATH > External 
   Libraries > Add source folder. Select the RabbitMQTOTEMLibrary/Python 
   directory.

6/ Start the RabbitMQ broker, with the shell script start-broker located in the 
   current folder.

7/ Now, you can run/debug GameServer/gameserver.py.
   Right-click on gameserver.py > Run as / Debug as ... Python run
