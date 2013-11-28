
# Add inputs and outputs from these tool invocations to the build variables 
O_SRCS += 

CPP_SRCS += \
./src/Action.cpp \
./src/Log.cpp \
./src/ChannelsManager.cpp \
./src/ConsumeChannel.cpp \
./src/GameLogicProtocol.cpp \
./src/GameLogicState.cpp \
./src/HeartbeatChannel.cpp \
./src/JoinAction.cpp \
./src/LifeCycleAction.cpp \
./src/PresenceAction.cpp \
./src/Properties.cpp \
./src/PublishChannel.cpp \
./src/RabbitMQGameInstanceChannel.cpp \
./src/ZebroGamqUtil.cpp 

OBJS += \
./src/Action.o \
./src/Log.o \
./src/ChannelsManager.o \
./src/ConsumeChannel.o \
./src/GameLogicProtocol.o \
./src/GameLogicState.o \
./src/HeartbeatChannel.o \
./src/JoinAction.o \
./src/LifeCycleAction.o \
./src/PresenceAction.o \
./src/Properties.o \
./src/PublishChannel.o \
./src/RabbitMQGameInstanceChannel.o \
./src/ZebroGamqUtil.o 

CPP_DEPS += \
./src/Action.d \
./src/Log.d \
./src/ChannelsManager.d \
./src/ConsumeChannel.d \
./src/GameLogicProtocol.d \
./src/GameLogicState.d \
./src/HeartbeatChannel.d \
./src/JoinAction.d \
./src/LifeCycleAction.d \
./src/PresenceAction.d \
./src/Properties.d \
./src/PublishChannel.d \
./src/RabbitMQGameInstanceChannel.d \
./src/ZebroGamqUtil.d 

# Each subdirectory must supply rules for building sources it contributes
src/%.o: ./src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	$(CCP) $(INCLUDES) $(CFLAGS) $(CPP11FLAGS) -MF"$(@:%.o=%.d)" -o "$@" -c "$<"
	@echo 'Finished building: $<'
	@echo ' '

