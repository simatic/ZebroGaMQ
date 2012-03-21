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
publish and consume messages. A prototypical game involves:
- A Python server
- Android applications
- Java J2SE applications
- JavaScript applications

One of the most powerful features of the middleware is the disconnection 
management of Android devices: you just have to focus on publishing your 
messages, the middleware will handle for you the loss of network 
connection inherent to mobile networks.


Doc directory
-------------

The Doc directory includes:
- The document "Presentation of the Communication Middleware", which presents
  the communication middleware, from its use cases and its requirements to its 
  concepts, its architecture and even its installation procedure. A tutorial 
  is also presented in this document to ease the use of the middleware and of 
  its clients applications.
- The installation procedure of prerequisite software on Windows environments,
  since the "Presentation of the Communication Middleware" mostly focuses on 
  Unix-like operating systems.


Sources
-------

The Sources directory includes:
- The API used by the communication middleware for the Python server and for
  the Java Android and JavaScript client applications.
- All the applications using those API that compose the communication 
  middleware, from the server to the client applications.
- A set of tutorials, to test the installation of the prerequisite software used
  by the middleware and its client applications.
