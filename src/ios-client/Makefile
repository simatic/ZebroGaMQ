
RM := rm -rf

CONFIG_FILE=""

-include $(CONFIG_FILE)   

# All of the sources participating in the build are defined here
-include xmlrpcpp/subdir.mk
-include src/subdir.mk
-include rabbitmq-c/subdir.mk
-include amqpcpp/subdir.mk

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: $(LIBFILE)

# Tool invocations
# $(AR)  -rsc "$(LIBFILE)" $(OBJS) $(USER_OBJS)
$(LIBFILE): $(OBJS) $(USER_OBJS)
	@echo 'Building library: $@'
	@echo 'Invoking: GCC C++ Linker'
	$(LIBTOOL) -static $(ARCH_ONLY) $(SYSLIBROOT) -o "$(LIBFILE)" $(FILELIST) $(OBJS)
	@echo ' '
	@echo 'Finished building library: $@'
	@echo ' '
	@echo 'Create ./build folder and copy library file $(LIBFILE) into it:'
	mkdir -p build
	cp $(LIBFILE) build

# Other Targets
clean:
	-$(RM) $(OBJS)$(C++_DEPS)$(C_DEPS)$(CC_DEPS)$(CPP_DEPS)$(EXECUTABLES)$(CXX_DEPS)$(C_UPPER_DEPS) $(LIBFILE)
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

