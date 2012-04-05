@echo off

rem TCM: TOTEM Communication Middleware
rem Copyright: Copyright (C) 2009-2012
rem Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu
rem
rem This library is free software; you can redistribute it and/or
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
set PYTHONPATH=%PYTHONPATH%;%cd%\Python-gamelogicserver\;%cd%\..\src\Python-server\
set CONFIGURATION_FILES_DIRECTORY=..\..\test\resources\
set JAVASCRIPT_CLIENT_LIBRARY_DIRECTORY=..\JavaScript-client\

rem stop and re-launch the RabbitMQ broker
call rabbitmqctl stop
rem sleep 2 seconds
ping 127.0.0.1 -n 2 > NUL
call rabbitmq-server start -detached
rem sleep 5 seconds
ping 127.0.0.1 -n 4 > NUL
call rabbitmqctl stop_app
call rabbitmqctl reset
call rabbitmqctl start_app

rem working directory
set PWD=%~dp0

rem launch the Game Server
cd ..\src\Python-server
start python zebrogamq\gameserver\gameserver.py %CONFIGURATION_FILES_DIRECTORY%
rem sleep 1 second
ping 127.0.0.1 -n 1 > NUL

rem launch the JavaScript proxy
cd %PWD%../src/JavaScript-proxy
start node proxy.js %CONFIGURATION_FILES_DIRECTORY% %JAVASCRIPT_CLIENT_LIBRARY_DIRECTORY%
rem sleep 1 second
ping 127.0.0.1 -n 1 > NUL

echo Instructions for the sequel of the demonstration:
echo 1/ Start a first JavaScript application on your Web browser, and create an instance
echo 2/ Start a second JavaScript application on your Web browser, and join the instance
echo 3/ (Optional) Start the Android-application on an Android Device,
echo     press the menu button, and click on "Join Instance".
echo Finally, to properly stop the demonstration,
echo execute the termination.bat script located in the current directory,
echo and close both python.exe and node.exe shells.

endlocal
