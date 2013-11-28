
# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
./amqpcpp/AMQP.cpp \
./amqpcpp/AMQPBase.cpp \
./amqpcpp/AMQPException.cpp \
./amqpcpp/AMQPExchange.cpp \
./amqpcpp/AMQPMessage.cpp \
./amqpcpp/AMQPQueue.cpp 

OBJS += \
./amqpcpp/AMQP.o \
./amqpcpp/AMQPBase.o \
./amqpcpp/AMQPException.o \
./amqpcpp/AMQPExchange.o \
./amqpcpp/AMQPMessage.o \
./amqpcpp/AMQPQueue.o 

CPP_DEPS += \
./amqpcpp/AMQP.d \
./amqpcpp/AMQPBase.d \
./amqpcpp/AMQPException.d \
./amqpcpp/AMQPExchange.d \
./amqpcpp/AMQPMessage.d \
./amqpcpp/AMQPQueue.d 

# Each subdirectory must supply rules for building sources it contributes
amqpcpp/%.o: ./amqpcpp/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	$(CCP) $(INCLUDES) $(CFLAGS) $(CPP11FLAGS) -MF"$(@:%.o=%.d)" -o "$@" -c "$<"
	@echo ' '
	@echo 'Finished building: $<'
	@echo ' '

