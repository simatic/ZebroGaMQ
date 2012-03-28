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

PYTHONPATH=$PYTHONPATH:$PWD/../src/Python-server/:$PWD/Termination-application

# terminate
(cd Termination-application; python termination.py&)
sleep 2

# kill game server
GAMESERVER_PID=$(cat conf/gameserver_temp_pid.txt 2> /dev/null)
kill $GAMESERVER_PID 2> /dev/null

# kill javascript proxy
JAVASCRIPT_PROXY_PID=$(cat conf/javascript_proxy_temp_pid.txt 2> /dev/null)
kill $JAVASCRIPT_PROXY_PID 2> /dev/null

# remove temp files
rm conf/gameserver_temp_pid.txt conf/javascript_proxy_temp_pid.txt 2> /dev/null

# stop RabbitMQ broker => stop all the clients
echo " End of demonstration"
echo " Stopping the borker..."
rabbitmqctl stop_app
rabbitmqctl stop
