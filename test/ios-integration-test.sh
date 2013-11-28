#!/bin/sh

# ZebroGaMQ: Communication Middleware for Mobile Gaming
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

# Attention: This script should run on one (Linux) machine whereas the ios-application runs on another (a Mac machine). Make sure that all addresses are set correctly before running (in /test/ios-resources/ and ios-application/resources - they are the addresses of machine that the server runs).

PYTHONPATH=$PYTHONPATH:$PWD/Python-gamelogicserver/:$PWD/../src/Python-server/
CONFIGURATION_FILES_DIRECTORY="../../test/ios-resources/"

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
sleep 1

echo ""
echo "Instructions for the sequel of the demonstration:"
echo "1/ Start the iOS-application on a first iOS Device / Simulator"
echo "2/ Press the menu button, and click on \"Create Instance\"."
echo "3/ Start the iOS-application on a second iOS Device / Simulator"
echo "4/ Press the menu button, and click on \"Join Instance\"."
echo ""
echo "Finally, execute the termination script to stop the demonstration."
echo ""
