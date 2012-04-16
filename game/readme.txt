aMazing! Geolocalized multiplayer game for Android devices.
Conceived and realized within the course "Mixed Reality Games for 
Mobile Devices" at Fraunhofer FIT.

http://www.fit.fraunhofer.de/de/fb/cscw/mixed-reality.html
http://www.totem-games.org/?q=aMazing

Copyright (C) 2012  Alexander Hermans, Tianjiao Wang

Contact: 
alexander.hermans0@gmail.com, tianjiao.wang@rwth-aachen.de,
richard.wetzel@fit.fraunhofer.de, lisa.blum@fit.fraunhofer.de, 
denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Developer(s): Alexander Hermans, Tianjiao Wang
ZebroGaMQ:  Denis Conan, Gabriel Adgeg 

aMazing
=======

aMazing is a geolocalized game for two players.
Description and rules of the game can be found at 
http://www.totem-games.org/?q=aMazing

Configuration
-------------

- Replace the value of the API_KEY attribute in the file
  aMazing-application/res/value/strings.xml by the value of your
  Google Maps API key.

- In order to test the game with real Android devices, you need to update the
  values of the IP addresses used both on the server side and on the client
  side. Here are the configuration files you need to update:
  - resources/rabbitmq.properties & resources/xmlrpc.properties
  - Android-application/res/raw/rabbitmq.properties & 
    Android-application/res/raw/xmlrpc.properties.

- Even if you can launch the game on emulators, it is very difficult to 
  understand all its features without playing it for real with devices.
  You can play it anywhere, as long as you get a good GPS signal and a good
  mobile network connection.

- Start the server side executing the start_amazing_game script. Then,
  follow the instructions to start the game on the Android devices.

- To properly finish the game, execute the termination script.
