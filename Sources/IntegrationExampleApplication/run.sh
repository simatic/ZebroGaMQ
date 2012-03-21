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


PYTHONPATH=$PYTHONPATH:$PWD/GameServer

# stop and re-launch the RabbitMQ broker
rabbitmqctl stop
sleep 2
rabbitmq-server -detached
sleep 2
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app

# launch the Game Server
(cd GameServer; ./run.sh)
sleep 2

# launch the Master Application
(cd MasterApplication; ./run.sh)
sleep 2

# launch the Spectator Applications
(cd SpectatorApplication; ./run.sh "denis" "conan" "Tidy-City" "Instance-1" "*.*.*.*")
(cd SpectatorApplication; ./run.sh "leif" "oppermann" "Tidy-City" "Instance-1" "*.*.*.*")
sleep 3

# launch the Player Applications
(cd PlayerApplication; ./run.sh "marius" "preda" "Tidy-City" "Instance-1")
(cd PlayerApplication; ./run.sh "lisa" "blum" "Tidy-City" "Instance-1")
sleep 3

# list users and users' permissions
rabbitmqctl list_users -p /Tidy-City/Instance-1
rabbitmqctl list_user_permissions -p /Tidy-City/Instance-1 michel
rabbitmqctl list_user_permissions -p /Tidy-City/Instance-1 denis
rabbitmqctl list_user_permissions -p /Tidy-City/Instance-1 marius
# list bindings to check the architecture
rabbitmqctl list_bindings -p /Tidy-City/Instance-1
# list queues to check reception and acknowledgments
rabbitmqctl list_queues -p /Tidy-City/Instance-1 messages messages_ready messages_unacknowledged

echo "''''''"
echo "''''''"
echo "''''''"
echo "''''''"
echo "''''''" When you are ready to terminate, press a key
echo "''''''"
echo "''''''"
echo "''''''"
echo "''''''"
read x

# terminate
(cd TerminationApplication; ./run.sh)
sleep 2

# kill game server
gameserver_pid=$(cat GameServer/temp_pid.txt)
kill $gameserver_pid

# remove temp file
rm GameServer/temp_pid.txt

# stop RabbitMQ broker => stop all the clients
echo " END OF EXAMPLE APPLICATION"
echo " GOING TO FORCE THE TERMINATION BY STOPPING BROKER"
rabbitmqctl stop_app
rabbitmqctl stop
