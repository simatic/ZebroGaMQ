1. How to compile:
	+ Compile ios-client first by runnning ios-client/build_ios_framework.sh. The framework is generated in ios-client/build/Framework
	+ Open ios-application.xcodeproj by XCode, make sure that the framework zebrogamq-ios.framework is added into project.
	+ Choose target (Simulator, iPhone, iPad) and run application.

2. How to run:
	+ Make sure that the server is running in distance and its address is set correctly in property files (ios-application/resources/rabbitmq.properties, test/resources/rabbitmq.properties and test/resources/xmlrpc.properties).
    + Run the script ios-integration-test.sh to start server.
    + Set address for application (in property files ios-application/resources/xmlrpc.properties) and make sure that they are the same as the server ones (in step 1)
	+ Open application project by XCode, run it and enjoy (after pressing buttons Create Instance or Join Instance, exchange messages are shown on the Text View of application).

3. Change logs
	+ 22/11/2013: C++ error should come from the xmlrpc server checking (from line int n = write(fd, sp, nToWrite); of ios-client/xmlrpcpp/XmlRpcSocket.cpp) if the xmlrpc server is not running and it crashes application (we cannot handle it from ObjC code). Solution: Check the xmlrpc server by calling GameLogicApplication::checkXMLRPCServer() at the beginning, before login and init ChannelsManager.
