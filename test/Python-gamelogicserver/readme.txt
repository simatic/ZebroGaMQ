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

This is a PyDev project used to customize RabbitMQ Python library
for a specific game. More information are available in 
../../doc/tutorial.html.

Prerequisites:
- RabbitMQ Server version 2.7.1: http://www.rabbitmq.com/server.html
- RabbitMQ Client for Python named Pika version 0.9.5 http://pika.github.com

Run the demonstration with PyDev
================================

PyDev is an Eclipse plugin for Python development. Its installation procedure 
is described here: http://pydev.org/download.html

Python-gamelogicserver and Python-server are PyDev projects. Here are the required 
steps to import, configure and run/debug those projects within PyDev.

1/ Make sure that you have set at least one Python interpreter:
   Windows > Preferences > PyDev > Interpreter - Python.
   Otherwise, add a new one, specifying the installation directory of your
   Python interpreter. 

2/ Add pika to the libraries.
   In the same window, in the "Libraries", select "New Folder", and add the 
   installation directory of pika. Then click on "Apply", and "OK".

3/ Import Python-gamelogicserver.
   In the PyDev Package Explorer, right-click > import > General > Existing
   Projects into Workspace. 
   Once you have specify the location of Python-gamelogicserver, you may have 
   to change the interpreter, if it is not yours.

4/ Import ../../src/Python-server, the same way you have just done with 
   Python-gamelogicserver.

6/ Start the RabbitMQ broker, with the shell script start-broker located in the 
   test folder.

7/ Now, you can run/debug Python-server/net/totem/gameservergameserver.py.
   Right-click on gameserver.py > Run as / Debug as ... Python run
