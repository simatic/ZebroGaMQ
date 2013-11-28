
RM := rm -rf

CC=g++
CFLAGS = -D__GXX_EXPERIMENTAL_CXX0X__ -O0 -g3 -Wall -c -fmessage-length=0 -std=c++0x -lpthread -pthread -MMD -MP
THREADFLAGS = -lpthread -pthread
LIBS = $(shell pwd)/../../src/Cpp-client/zebrogamq-gamelogic-client.a
INCLUDES = -I"$(shell pwd)/src" -I"$(shell pwd)/../../src/Cpp-client/amqpcpp" -I"$(shell pwd)/../../src/Cpp-client/src" -I"$(shell pwd)/../../src/Cpp-client/xmlrpcpp" -I"$(shell pwd)/../../src/Cpp-client/rabbitmq-c"

EXECFILE = zebrogamq-gamelogic-app

CPP_SRCS += \
./src/GameLogicApplication.cpp \
./src/MySecondActionKind.cpp \
./src/MyGameLogicProtocol.cpp \
./src/MyListOfGameLogicActions.cpp \
./src/MyFirstActionKind.cpp \
./src/XMLRPCLogin.cpp \
./src/zebrogamq-gamelogic-app.cpp 

OBJS += \
./src/GameLogicApplication.o \
./src/MySecondActionKind.o \
./src/MyGameLogicProtocol.o \
./src/MyListOfGameLogicActions.o \
./src/MyFirstActionKind.o \
./src/XMLRPCLogin.o \
./src/zebrogamq-gamelogic-app.o 

CPP_DEPS += \
./src/GameLogicApplication.d \
./src/MySecondActionKind.d \
./src/MyGameLogicProtocol.d \
./src/MyListOfGameLogicActions.d \
./src/MyFirstActionKind.d \
./src/XMLRPCLogin.d \
./src/zebrogamq-gamelogic-app.d 

# Each subdirectory must supply rules for building sources it contributes
src/%.o: ./src/%.cpp
	@echo 'Building file: $<'
	$(CC) $(INCLUDES) $(CFLAGS) -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building object file: $<'
	@echo ' '

# All Target
all: $(EXECFILE)

# Tool invocations
$(EXECFILE): $(OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C++ Linker'
	$(CC) -o "$(EXECFILE)" $(OBJS) $(LIBS) $(THREADFLAGS)
	@echo ' '
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(OBJS) $(CPP_DEPS) $(EXECFILE)
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

