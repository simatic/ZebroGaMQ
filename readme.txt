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


TOTEM Communication Middleware
==============================

Features
--------

The TOTEM Communication Middleware provides an easy and reliable way to 
publish and consume messages in the context of mobile multiplayer games. 
A prototypical game involves:
- A Python server
- Android applications
- Java J2SE applications
- JavaScript applications

One of the most powerful features of the middleware is the disconnection 
management of Android devices: you just have to focus on publishing your 
messages, the middleware will handle for you the loss of network 
connection inherent to mobile networks.


src directory
-------------

The API used by the communication middleware for the Python server and for
the Java Android and JavaScript client applications.

test directory
--------------

All the applications using those API that compose the communication 
middleware, from the server to the client applications.
Scripts are present to execute integration tests using those applications.
They can be launched on both Unix and Windows environments.