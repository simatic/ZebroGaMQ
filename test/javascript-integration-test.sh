#!/bin/sh

# TCM: TOTEM Communication Middleware
# Copyright: Copyright (C) 2009-2012
# Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
# USA
#
# Developer(s): Denis Conan, Gabriel Adgeg

PYTHONPATH=$PYTHONPATH:$PWD/Python-gamelogicserver/:$PWD/../src/Python-server/
CONFIGURATION_FILES_DIRECTORY="../../test/resources/"
JAVASCRIPT_CLIENT_LIBRARY_DIRECTORY="../JavaScript-client/"

# stop and re-launch the RabbitMQ broker
rabbitmqctl stop
sleep 2
rabbitmq-server -detached
sleep 2
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app

# launch the Game Server
(cd ../src/Python-server; python zebrogamq/gameserver/gameserver.py $CONFIGURATION_FILES_DIRECTORY) &
GAMESERVER_PID=$!
echo $GAMESERVER_PID >> resources/gameserver_temp_pid.txt
sleep 2

echo ""

# launch the JavaScript proxy
(cd ../src/JavaScript-proxy; node proxy.js $CONFIGURATION_FILES_DIRECTORY $JAVASCRIPT_CLIENT_LIBRARY_DIRECTORY) &
JAVASCRIPT_PROXY_PID=$!
echo $JAVASCRIPT_PROXY_PID >> resources/javascript_proxy_temp_pid.txt
sleep 1

echo ""
echo "Instructions for the sequel of the demonstration:"
echo "1/ Start a first JavaScript application on your Web browser, and create an instance"
echo "2/ Start a second JavaScript application on your Web browser, and join the instance"
echo "3/ (Optional) Start the Android-application on an Android Device,"
echo "    press the menu button, and click on \"Join Instance\"."
echo ""
echo "Finally, to properly stop the demonstration,"
echo "execute the termination.sh script located in the current directory."
echo ""
