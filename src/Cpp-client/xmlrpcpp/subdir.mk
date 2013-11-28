
# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
./xmlrpcpp/XmlRpcClient.cpp \
./xmlrpcpp/XmlRpcDispatch.cpp \
./xmlrpcpp/XmlRpcServer.cpp \
./xmlrpcpp/XmlRpcServerConnection.cpp \
./xmlrpcpp/XmlRpcServerMethod.cpp \
./xmlrpcpp/XmlRpcSocket.cpp \
./xmlrpcpp/XmlRpcSource.cpp \
./xmlrpcpp/XmlRpcUtil.cpp \
./xmlrpcpp/XmlRpcValue.cpp 

OBJS += \
./xmlrpcpp/XmlRpcClient.o \
./xmlrpcpp/XmlRpcDispatch.o \
./xmlrpcpp/XmlRpcServer.o \
./xmlrpcpp/XmlRpcServerConnection.o \
./xmlrpcpp/XmlRpcServerMethod.o \
./xmlrpcpp/XmlRpcSocket.o \
./xmlrpcpp/XmlRpcSource.o \
./xmlrpcpp/XmlRpcUtil.o \
./xmlrpcpp/XmlRpcValue.o 

CPP_DEPS += \
./xmlrpcpp/XmlRpcClient.d \
./xmlrpcpp/XmlRpcDispatch.d \
./xmlrpcpp/XmlRpcServer.d \
./xmlrpcpp/XmlRpcServerConnection.d \
./xmlrpcpp/XmlRpcServerMethod.d \
./xmlrpcpp/XmlRpcSocket.d \
./xmlrpcpp/XmlRpcSource.d \
./xmlrpcpp/XmlRpcUtil.d \
./xmlrpcpp/XmlRpcValue.d 

# Each subdirectory must supply rules for building sources it contributes
xmlrpcpp/%.o: ./xmlrpcpp/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'	
	$(CCP) $(INCLUDES) $(CFLAGS) $(CPP11FLAGS) -MF"$(@:%.o=%.d)" -o "$@" -c "$<"
	@echo 'Finished building: $<'
	@echo ' '

