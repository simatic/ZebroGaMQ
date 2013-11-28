ZebroGamQ library in C++

+ Three external libraries are used:
    - rabbitmq-cpp: C implementation of RabbitMQ (https://github.com/alanxz/rabbitmq-c)
    - amqpcpp: C++ wrapper of rabbitmq-cpp (https://github.com/akalend/amqpcpp)
    - xmlrpcpp: C++ implementation of XMLRPC (http://sourceforge.net/projects/xmlrpcpp/)
Code files (including *.c/*.cpp files and *.h files) are put in equivalent folders (amqpcpp, rabbitmq-c, xmlrpcpp).

+ Compile: 
    - iOS: Run bash script build_ios_framework.sh. The framework zebrogamq-ios.framework is generated in ios-client/build/Framework.

+ Use: 
    - iOS: Add framework ios-client/build/Framework/zebrogamq-ios.framework into the project that you want to use it.
    
+ Differences compared with cpp-client: Code of cpp-client and ios-client is exactly the same but the execution is little bit different. For ios-client, all output message is written in file meanwhile for cpp-client we just use cout for print out output messages.
	
