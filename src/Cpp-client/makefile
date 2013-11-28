
RM := rm -rf

MAKECONFIG_FILE=makeconfig_linux

-include $(MAKECONFIG_FILE)

# All of the sources participating in the build are defined here
-include xmlrpcpp/subdir.mk
-include src/subdir.mk
-include rabbitmq-c/subdir.mk
-include amqpcpp/subdir.mk

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: $(LIBNAME)

# Tool invocations
$(LIBNAME): $(OBJS) $(USER_OBJS)
	@echo 'Building library: $@'
	@echo 'Invoking: GCC C++ Linker'
	ar  -rsc "$(LIBNAME)" $(OBJS) $(USER_OBJS)
	@echo ' '
	@echo 'Finished building library: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(OBJS)$(C++_DEPS)$(C_DEPS)$(CC_DEPS)$(CPP_DEPS)$(EXECUTABLES)$(CXX_DEPS)$(C_UPPER_DEPS) $(LIBNAME)
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

