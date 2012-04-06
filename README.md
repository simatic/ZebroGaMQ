[ZebroGaMQ: Communication Middleware for Mobile Gaming](http://www.totem-games.org/?q=Communication%20Middleware)
================================

ZebroGaMQ provides an easy and reliable way to 
publish and consume messages in the context of mobile multiplayer games. 
A prototypical game involves:

* A Python server
* Android applications
* JavaScript applications
* Java J2SE applications

One of the most powerful features of the middleware is the disconnection 
management of Android devices: you just have to focus on publishing your 
messages, the middleware will handle for you the loss of network 
connection inherent to mobile networks.

Installation and tests
----------------------

### For the server side (mandatory)
* Erlang ≥ R13B03,
* RabbitMQ Server ≥ 2.7.1,
* Python ≥ 2.6,
* Pika version ≥ 0.9.5

### For Java applications
* Java ≥ 1.5,
* Maven ≥ 2.2.1
##### To run the tests on Unix/Mac:

    cd test
    ./java-integration-test.sh

##### To run the tests on Windows:

    cd test
    java-integration-test.bat


### For Android applications
* ADT plugin for Eclipse ≥ 12.0.0,
* Android SDK API level ≥ 7
##### To run the tests on Unix/Mac:

    cd test
    ./android-integration-test.sh

##### To run the tests on Windows:

    cd test
    android-integration-test.bat


### For JavaScript applications
* Node.js ≥ 0.4.10,
* NPM ≥ 1.0.106,
* AMQP library (to install with NPM) = 0.1.0,
* XMLRPC library (to install with NPM) ≥ 0.8.1
##### To run the tests on Unix/Mac:

    cd test
    ./javascript-integration-test.sh

##### To run the testson Windows:

    cd test
    javascript-integration-test.bat