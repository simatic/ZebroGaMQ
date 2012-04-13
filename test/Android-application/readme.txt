ZebroGaMQ: Communication Middleware for Mobile Gaming
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


This is an Eclipse Android project of the player application of the
integration example application.


Execution
=========

Here are the instructions for the sequel of the demonstration:
1/ Open a shell, go to the directory test/, start
   the demonstration typing:
   - $ ./android-integration-test.sh (on Unix-like operating systems)
   - $ android-integration-test.bat (on Windows operating systems)
2/ Adapt the addresses of the Game server and of the RabbitMQ broker to 
   your local configurations in the files:
   - res/raw/rabbitmq.properties
   - res/raw/xmlrpc.properties
3/ Start the Android-application on a first Android device (or emulator).
4/ Press the menu button, and click on "Create Instance".
5/ Start the Android-application on a second Android Device (or emulator).
6/ Press the menu button, and click on "Join Instance".
7/ Finally, execute the termination script to properly stop the demonstration.


TOTEM API
=========

The TOTEM API is located into the lib directory:
- gamelogic-client-1.0-SNAPSHOT.jar

However, all the jars located in the lib directory are needed by the 
communication middleware to use both XML-RPC and AMQP protocols.

Indeed, you must import this whole directory in your own Android projects 
to communicate with the middleware.
