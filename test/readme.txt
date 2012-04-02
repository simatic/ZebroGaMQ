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

Demonstration
=============

Here is the scenario for a demonstration that includes:
- a JavaScript application
- an Android application


1/ Setting IP addresses
-----------------------

In the following, we explain how to set the IP addresses that you will use for:
- the Python-server,
- the JavaScript-proxy for Web applications,
- the Android-application.

You cannot use the default localhost address because of the Android application.

Here are the configuration files you need to update:
- resources/rabbitmq.properties & resources/xmlrpc.properties
- Android-application/res/raw/rabbitmq.properties & 
  Android-application/res/raw/xmlrpc.properties.

2/ Starting the demonstration
-----------------------------

2.1 Start the server side by launching the following command in a first shell:
    $ ./javascript-integration-test.sh

    This will start:
    - the RabbitMQ broker
    - the GameServer
    - the NodeJsProxy

    The following text will be displayed once the JavaScript-proxy would be started:
    
    "Instructions for the sequel of the demonstration:
    1/ Start a first JavaScript application on your Web browser, and create an instance
    2/ Start a second JavaScript application on your Web browser, and join the instance
    3/ (Optional) Start the Android-application on an Android Device,
    press the menu button, and click on "Join Instance".

    Finally, to properly stop the demonstration,
    echo "execute the termination.sh script located in the current directory."


2.2 Launch the Web application

    Open a Web browser, and go to the following URL:
    http:JAVASCRIPT_PROXY_HOST//:8001/JavaScript-application (where JAVASCRIPT is the address 
    that you have just set in resources/xmlrpc.properties).
    Then, click on the button "Create and join game instance".
    The application will log in, create a game instance, and join that game
    instance.

    Be careful! We do not allow for several Master applications in this
                demonstration.


2.3 Launch the Android application for the player

    Android-application is an Eclipse project. You need to start Eclipse,
    import this project, and run it (whether on an Android device or on an 
    emulator) as an Android Application.
    Press the menu button, and click on "Join Instance".

2.4 Messages exchanged

    The JavaScript and the Android application should have logged in and and 
    should have joined the game instance. They will periodically send 
    heartbeat messages and messages to ask the game logic server for the list 
    of participants.

3/ Terminating the demonstration
--------------------------------

Terminate the demonstration on both server and clients side by launching the 
following command in a second shell:
$ ./termination.sh
