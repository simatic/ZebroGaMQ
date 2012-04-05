@echo off

rem ZebroGaMQ: Communication Middleware for Mobile Gaming
rem Copyright: Copyright (C) 2009-2012
rem Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu
rem
rem This library is free software; you can redistribute it and or
rem modify it under the terms of the GNU Lesser General Public
rem License as published by the Free Software Foundation; either
rem version 3 of the License, or any later version.
rem
rem This library is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
rem Lesser General Public License for more details.
rem
rem You should have received a copy of the GNU Lesser General Public
rem License along with this library; if not, write to the Free Software
rem Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
rem USA
rem
rem Developer(s): Denis Conan, Gabriel Adgeg

setlocal
set MODULE_VERSION=1.0-SNAPSHOT
set RABBITMQ_CLIENT_VERSION=2.7.0
set CLASS=zebrogamq.integration.j2se.GameLogicApplication
set RABBITMQPROPERTIESFILE=rabbitmq.properties
set XMLRPCPROPERTIESFILE=xmlrpc.properties
set PWD=%~dp0


IF EXIST "%HOMEPATH%\.m2\repository\zebrogamq\zebrogamq-gamelogic-client\1.0-SNAPSHOT\zebrogamq-gamelogic-client-1.0-SNAPSHOT.jar" (
    set JARS="%HOMEPATH%\.m2\repository\zebrogamq\zebrogamq-gamelogic-client\1.0-SNAPSHOT\zebrogamq-gamelogic-client-1.0-SNAPSHOT.jar"
) ELSE (
    echo Running maven install on Java-client...
    cd ..\src\Java-client
    call mvn install
    set JARS="%HOMEPATH%\.m2\repository\zebrogamq\zebrogamq-gamelogic-client\1.0-SNAPSHOT\zebrogamq-gamelogic-client-1.0-SNAPSHOT.jar"
)

IF EXIST "%PWD%target\zebrogamq-gamelogic-integration-application-%MODULE_VERSION%.jar" (
	set JARS="%PWD%target\zebrogamq-gamelogic-integration-application-%MODULE_VERSION%.jar";%JARS%
) ELSE (
    echo Running maven install on Java-application...
    cd %PWD%
    call mvn install
    set JARS="%PWD%target\zebrogamq-gamelogic-integration-application-%MODULE_VERSION%.jar";%JARS%
)

set JARS="%HOMEPATH%\.m2\repository\com\rabbitmq\amqp-client\%RABBITMQ_CLIENT_VERSION%\amqp-client-%RABBITMQ_CLIENT_VERSION%.jar";%JARS%
set JARS="%HOMEPATH%\.m2\repository\org\apache\commons\commons-io\1.3.2\commons-io-1.3.2.jar";%JARS%
set JARS="%HOMEPATH%\.m2\repository\xmlrpc\xmlrpc\2.0.1\xmlrpc-2.0.1.jar";%JARS%
set JARS="%HOMEPATH%\.m2\repository\ws-commons-util\ws-commons-util\1.0.1\ws-commons-util-1.0.1.jar";%JARS%
set JARS="%HOMEPATH%\.m2\repository\xml-apis\xml-apis\1.0.b2\xml-apis-1.0.b2.jar";%JARS%
set JARS="%HOMEPATH%\.m2\repository\commons-codec\commons-codec\1.4\commons-codec-1.4.jar";%JARS%

rem argv[0] = login
rem argv[1] = password
rem argv[2] = role
rem argv[3] = game name
rem argv[4] = instance name
rem argv[5] = observation key for the application instance
start java -Drabbitmq.config.file=%RABBITMQPROPERTIESFILE% -Dxmlrpc.config.file=%XMLRPCPROPERTIESFILE% -cp %JARS% %CLASS% %1 %2 %3 %4 %5 %6

endlocal