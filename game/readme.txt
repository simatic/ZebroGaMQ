aMazing
=======

aMazing is a geolocalized game for two players.

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
