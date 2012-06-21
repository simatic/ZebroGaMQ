#!/bin/sh

PYTHONPATH=$PYTHONPATH:$PWD/../../RabbitMQTOTEMLibrary/Python/:$PWD/../GameServer:$PWD/../LoggingServer:$PWD

#$1 is the first argument passed to the script 
python terminationinstance.py $1 &
