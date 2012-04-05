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

MODULE_VERSION=1.0-SNAPSHOT
RABBITMQ_CLIENT_VERSION=2.7.0
CLASS=net.totem.integration.j2se.GameLogicApplication
RABBITMQPROPERTIESFILE=rabbitmq.properties
XMLRPCPROPERTIESFILE=xmlrpc.properties

if [[ -f ${HOME}/.m2/repository/net/totem/gamelogic-client/1.0-SNAPSHOT/gamelogic-client-1.0-SNAPSHOT.jar ]]
then
    export JARS=${HOME}/.m2/repository/net/totem/gamelogic-client/1.0-SNAPSHOT/gamelogic-client-1.0-SNAPSHOT.jar
else
    echo Running maven install on Java-client...
    (cd ../../src/Java-client; mvn install)
    export JARS=${HOME}/.m2/repository/net/totem/gamelogic-client/1.0-SNAPSHOT/gamelogic-client-1.0-SNAPSHOT.jar
fi

if [[ -f ./target/gamelogic-integration-application-${MODULE_VERSION}.jar ]]
then
    export JARS=./target/gamelogic-integration-application-${MODULE_VERSION}.jar:${JARS}
else
    echo Running maven install on Java-application...
    mvn install
    export JARS=./target/gamelogic-integration-application-${MODULE_VERSION}.jar:${JARS}
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
# argv[2] = role
# argv[3] = game name
# argv[4] = instance name
# argv[5] = observation key for the application instance
exec java -Drabbitmq.config.file=${RABBITMQPROPERTIESFILE} -Dxmlrpc.config.file=${XMLRPCPROPERTIESFILE} -cp ${JARS} ${CLASS} $1 $2 $3 $4 $5 $6 &
