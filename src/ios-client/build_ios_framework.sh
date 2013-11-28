# name and build location

: ${PROJECT_NAME:=zebrogamq-ios}
: ${PROJECT_DIR:=`pwd`}
: ${SRCROOT=`pwd`}

FRAMEWORK_NAME=${PROJECT_NAME}
FRAMEWORK_BUILD_PATH="${PROJECT_DIR}/build/Framework"
 
# these never change
FRAMEWORK_VERSION=A
FRAMEWORK_CURRENT_VERSION=1
FRAMEWORK_COMPATIBILITY_VERSION=1

# Generate library files for iPhone and Simulator
echo " "
echo "Framework: Run make files..."
make clean CONFIG_FILE='makeconfig_ios_sim';
make CONFIG_FILE='makeconfig_ios_sim';
make clean CONFIG_FILE='makeconfig_ios_dev';
make CONFIG_FILE='makeconfig_ios_dev';
echo " "

# Clean any existing framework that might be there
if [ -d "$FRAMEWORK_BUILD_PATH" ]
then
	echo "Framework: Cleaning framework..."
	rm -rf "$FRAMEWORK_BUILD_PATH"
fi
 
# Build the canonical Framework bundle directory structure
echo " "
echo "Framework: Setting up directories..."
FRAMEWORK_DIR=$FRAMEWORK_BUILD_PATH/$FRAMEWORK_NAME.framework
mkdir -p $FRAMEWORK_DIR
mkdir -p $FRAMEWORK_DIR/Versions
mkdir -p $FRAMEWORK_DIR/Versions/$FRAMEWORK_VERSION
mkdir -p $FRAMEWORK_DIR/Versions/$FRAMEWORK_VERSION/Resources
mkdir -p $FRAMEWORK_DIR/Versions/$FRAMEWORK_VERSION/Headers

echo " " 
echo "Framework: Creating symlinks..."
ln -s $FRAMEWORK_VERSION $FRAMEWORK_DIR/Versions/Current
ln -s Versions/Current/Headers $FRAMEWORK_DIR/Headers
ln -s Versions/Current/Resources $FRAMEWORK_DIR/Resources
ln -s Versions/Current/$FRAMEWORK_NAME $FRAMEWORK_DIR/$FRAMEWORK_NAME
 
# combine lib files for various platforms into one
echo " "
echo "Framework: Creating library..."
lipo -create "${PROJECT_DIR}/build/libzebrogamq-gamelogic-client-dev.a" "${PROJECT_DIR}/build/libzebrogamq-gamelogic-client-sim.a" -o "$FRAMEWORK_DIR/Versions/Current/$FRAMEWORK_NAME"
 
echo " "
echo "Framework: Copying assets into current version..."
cp ${SRCROOT}/amqpcpp/*.h $FRAMEWORK_DIR/Headers/
cp ${SRCROOT}/rabbitmq-c/*.h $FRAMEWORK_DIR/Headers/
cp ${SRCROOT}/src/*.h $FRAMEWORK_DIR/Headers/
cp ${SRCROOT}/xmlrpcpp/*.h $FRAMEWORK_DIR/Headers/

echo " " 
echo "Framework: Creating plist..."
cat > $FRAMEWORK_DIR/Resources/Info.plist <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>CFBundleDevelopmentRegion</key>
	<string>English</string>
	<key>CFBundleExecutable</key>
	<string>${FRAMEWORK_NAME}</string>
	<key>CFBundleIdentifier</key>
	<string>com.icebreak.${FRAMEWORK_NAME}</string>
	<key>CFBundleInfoDictionaryVersion</key>
	<string>6.0</string>
	<key>CFBundlePackageType</key>
	<string>FMWK</string>
	<key>CFBundleSignature</key>
	<string>????</string>
	<key>CFBundleVersion</key>
	<string>${FRAMEWORK_CURRENT_VERSION}</string>
</dict>
</plist>
EOF

echo " "
echo "Done."