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


I. Installation
===============

1/ node.js, an event-driven I/O server-side JavaScript environment.

	Prerequisites
	-------------

	To build node.js from source, you need:
	- python - version 2.4 or higher. The build tools distributed with Node 
	run on python.
	- libssl-dev - If you plan to use SSL/TLS encryption in your networking,
	you'll need this. Libssl is the library used in the openssl tool. 
	On Linux and Unix systems it can usually be installed with your favorite
	package manager. The lib comes pre- installed on OS X.

    	Build instructions
	------------------

	To download and build node.js version 0.4.10, execute the following 
	commands:

	$ mkdir ~/node.js
	$ cd ~/node.js
	$ wget http://nodejs.org/dist/node-v0.4.10.tar.gz
	$ tar -xvzf node-v0.4.10.tar.gz
	$ rm node-v0.4.10.tar.gz
	$ cd node-v0.4.10
	$ ./configure --prefix=$HOME/node.js/node-v0.4.10
	$ make
	$ make install
	$ echo "export PATH=$PATH:$HOME/node.js/node-v0.4.10/bin" >> ~/.bashrc
	$ echo "export NODE_PATH=$HOME/node.js/node-v0.4.10:$HOME/\
	  node.js/node-v0.4.10/lib/node_modules" >> ~/.bashrc
	$ source ~/.bashrc


	Check that node.js is properly installed by trying the following
	command	that should return the installed version:
	$ node -v
	v0.4.10

2/ npm, the package manager for installing additional node.js' libraries
  
	If the curl command is not present, you should first install it:
	$ sudo apt-get install curl
	
	Then, install npm:
	$ curl http://npmjs.org/install.sh | sh

3/ node-amqp, an AMQP client for node.js

	Execute the following command:
	$ npm install -g amqp@0.1.0

4/ node-xmlrpc, an XMLRPC client for node.js

	Execute the following command:
	$ npm install -g xmlrpc


II. Execution
=============

  Setting IP addresses
  --------------------

  In the files resources/config.properties & resources/rabbitmq.properties,
  set the IP addresses that you use for:
  - the GameServer,
  - the NodeJsProxy,
  - the RabbitMQ broker.

  By default, the all the addresses are set to localhost.

  The script of the example application is 
  ../run_with_master_and_spectator_javascript_applications.sh, not ../run.sh.


  Using NodeJsProxy for several games
  -----------------------------------

  A single instance of the NodeJsProxy is required to handle several Master
  and Spectator applications for different games.

  To describe the html, css & javascript files used by several applications 
  for several games, use the games-specific-files configuration file, located 
  in the resources directory of the current folder.

  This file describes a set of keys and values, binding an element of an URL
  or of an HTML file to a local file designated by its path, required to upload
  all the required files for the Javascript applications.

  Please refer to the imports located in the file 
  ../MasterApplicationJavascript/master.html for a better understanding of the 
  games-specific-files configuration file.

  



