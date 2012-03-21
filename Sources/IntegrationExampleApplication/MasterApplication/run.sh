#!/bin/bash

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

MODULE_VERSION=0.1-SNAPSHOT
RABBITMQ_CLIENT_VERSION=2.7.0
CLASS=net.totem.integration.master.j2se.MasterApplication
RABBITMQPROPERTIESFILE=rabbitmq.properties
XMLRPCPROPERTIESFILE=xmlrpc.properties

if [[ -f ./target/gamemaster-${MODULE_VERSION}.jar ]]
then
    export JARS=./target/gamemaster-${MODULE_VERSION}.jar
else
    echo Archive file ./target/gamemaster-${MODULE_VERSION}.jar missing
    echo Run maven install to generate it
fi

if [[ -f ${HOME}/.m2/repository/com/rabbitmq/amqp-client/${RABBITMQ_CLIENT_VERSION}/amqp-client-${RABBITMQ_CLIENT_VERSION}.jar ]]
then
    export JARS=${HOME}/.m2/repository/com/rabbitmq/amqp-client/${RABBITMQ_CLIENT_VERSION}/amqp-client-${RABBITMQ_CLIENT_VERSION}.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/com/rabbitmq/amqp-client/${RABBITMQ_CLIENT_VERSION}/amqp-client-${RABBITMQ_CLIENT_VERSION}.jar missing
    echo Run maven install to install it on your local maven repository
fi

if [[ -f ${HOME}/.m2/repository/org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.jar ]]
then
    export JARS=${HOME}/.m2/repository/org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.jar missing
    echo Run maven install to install it on your local maven repository
fi

if [[ -f ${HOME}/.m2/repository/net/totem/gamelogic/gamelogic-common/0.1-SNAPSHOT/gamelogic-common-0.1-SNAPSHOT.jar ]]
then
    export JARS=${HOME}/.m2/repository/net/totem/gamelogic/gamelogic-common/0.1-SNAPSHOT/gamelogic-common-0.1-SNAPSHOT.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/net/totem/gamelogic/gamelogic-common/0.1-SNAPSHOT/gamelogic-common-0.1-SNAPSHOT.jar missing
    echo Run maven install to install it on your local maven repository
fi

if [[ -f ${HOME}/.m2/repository/net/totem/gamelogic/gamemaster/0.1-SNAPSHOT/gamemaster-0.1-SNAPSHOT.jar ]]
then
    export JARS=${HOME}/.m2/repository/net/totem/gamelogic/gamemaster/0.1-SNAPSHOT/gamemaster-0.1-SNAPSHOT.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/net/totem/gamelogic/gamemaster/0.1-SNAPSHOT/gamemaster-0.1-SNAPSHOT.jar missing
    echo Run maven install to install it on your local maven repository
fi

if [[ -f ${HOME}/.m2/repository/xmlrpc/xmlrpc/2.0.1/xmlrpc-2.0.1.jar ]]
then
    export JARS=${HOME}/.m2/repository/xmlrpc/xmlrpc/2.0.1/xmlrpc-2.0.1.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/xmlrpc/xmlrpc/2.0.1/xmlrpc-2.0.1.jar missing
    echo Run maven install to install it on your local maven repository
fi

if [[ -f ${HOME}/.m2/repository/ws-commons-util/ws-commons-util/1.0.1/ws-commons-util-1.0.1.jar ]]
then
    export JARS=${HOME}/.m2/repository/ws-commons-util/ws-commons-util/1.0.1/ws-commons-util-1.0.1.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/ws-commons-util/ws-commons-util/1.0.1/ws-commons-util-1.0.1.jar missing
    echo Run maven install to install it on your local maven repository
fi

if [[ -f ${HOME}/.m2/repository/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar ]]
then
    export JARS=${HOME}/.m2/repository/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar missing
    echo Run maven install to install it on your local maven repository
fi

if [[ -f ${HOME}/.m2/repository/commons-codec/commons-codec/1.4/commons-codec-1.4.jar ]]
then
    export JARS=${HOME}/.m2/repository/commons-codec/commons-codec/1.4/commons-codec-1.4.jar:${JARS}
else
    echo Archive file ${HOME}/.m2/repository/commons-codec/commons-codec/1.4/commons-codec-1.4.jar missing
    echo Run maven install to install it on your local maven repository
fi

# argv[0] = login
# argv[1] = password
# argv[2] = game name
# argv[3] = instance name
exec java -Drabbitmq.config.file=${RABBITMQPROPERTIESFILE} -Dxmlrpc.config.file=${XMLRPCPROPERTIESFILE} -cp ${JARS} ${CLASS} "michel" "simatic" "Tidy-City" "Instance-1" &
