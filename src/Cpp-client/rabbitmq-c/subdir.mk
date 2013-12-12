
# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
./rabbitmq-c/amqp_api.c \
./rabbitmq-c/amqp_connection.c \
./rabbitmq-c/amqp_consumer.c \
./rabbitmq-c/amqp_framing.c \
./rabbitmq-c/amqp_mem.c \
./rabbitmq-c/amqp_socket.c \
./rabbitmq-c/amqp_table.c \
./rabbitmq-c/amqp_tcp_socket.c \
./rabbitmq-c/amqp_timer.c \
./rabbitmq-c/amqp_url.c 

OBJS += \
./rabbitmq-c/amqp_api.o \
./rabbitmq-c/amqp_connection.o \
./rabbitmq-c/amqp_consumer.o \
./rabbitmq-c/amqp_framing.o \
./rabbitmq-c/amqp_mem.o \
./rabbitmq-c/amqp_socket.o \
./rabbitmq-c/amqp_table.o \
./rabbitmq-c/amqp_tcp_socket.o \
./rabbitmq-c/amqp_timer.o \
./rabbitmq-c/amqp_url.o 

C_DEPS += \
./rabbitmq-c/amqp_api.d \
./rabbitmq-c/amqp_connection.d \
./rabbitmq-c/amqp_consumer.d \
./rabbitmq-c/amqp_framing.d \
./rabbitmq-c/amqp_mem.d \
./rabbitmq-c/amqp_socket.d \
./rabbitmq-c/amqp_table.d \
./rabbitmq-c/amqp_tcp_socket.d \
./rabbitmq-c/amqp_timer.d \
./rabbitmq-c/amqp_url.d 

# Each subdirectory must supply rules for building sources it contributes
rabbitmq-c/%.o: ./rabbitmq-c/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	$(CC) $(INCLUDES) $(CFLAGS) -MF"$(@:%.o=%.d)" -o "$@" -c "$<"
	@echo 'Finished building: $<'
	@echo ' '

