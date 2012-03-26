TCM: TOTEM Communication Middleware
Copyright: Copyright (C) 2009-2012
Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA

Developer(s): Denis Conan, Gabriel Adgeg

This directory structure contains the code written for using RabbitMQ in the
TOTEM project:
- Java:
  Maven projects for the API: common parts, and the different roles (game
  master, player, and spectator)
- Python:
  Some code for developing game instance logic.
- JavaScript:
  Libraries required by Master and Spectator applications in JavaScript
  to communicate with the NodeJsProxy, and hence with the Game Server and the 
  RabbitMQ broker, during the loggin phase and the in-game phase. 
