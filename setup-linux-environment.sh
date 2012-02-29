#!/bin/bash

#set -x 
set -e

ROOT=$PWD

SDK_FILE=android-sdk_r16-linux.tgz
NDK_FILE=android-ndk-r7b-linux-x86.tar.bz2

get_package_index(){
    echo "$SDKS_BUFFER" | grep "$1" | awk '{ print substr($1, 0, length($1)-1)}' 
}



echo "Ok, let's set this up..."
echo ''

mkdir -p externals/downloads

cd externals/

if [ ! -e downloads/$SDK_FILE ]; then
    wget http://dl.google.com/android/$SDK_FILE -O downloads/$SDK_FILE
fi

if [ ! -e downloads/$NDK_FILE ]; then
    wget http://dl.google.com/android/ndk/$NDK_FILE -O downloads/$NDK_FILE
fi

if [ ! -e android-sdk-linux ]; then
    tar xapf downloads/$SDK_FILE
fi

if [ ! -e android-ndk-r7b ]; then
    tar xapf downloads/$SDK_FILE
fi

if [ ! -e android-sdk-linux/platforms/android-14 ]; then
    cd android-sdk-linux/tools

    SDKS_BUFFER=`./android list sdk`

    PLATFORM_4=`get_package_index "SDK Platform Android 4.0, API 14"`
    PLATFORM_TOOLS=`get_package_index "Android SDK Platform-tools"`

    ./android update sdk --no-ui --filter $PLATFORM_4,$PLATFORM_TOOLS,tool,platform-tool 

    ./android update adb

    cd ..

    platform-tools/adb kill-server
fi

#cd $ROOT/externals

if [ ! -z "`uname -a | grep x86_64`" ]; then
    echo "64 bit system detected, installing ia32-libs"
    set +e
    dpkg -l ia32-libs &>/dev/null
    if [ $? == 1 ]; then
        set -e
        sudo apt-get install ia32-libs
    else
        echo "already installed, skipping"
    fi
    echo "This will fail in Debian, this shouldn't be a problem."
    set +e
    dpkg -l ia32-libs-multiarch &>/dev/null
    if [ $? == 1 ]; then
        sudo apt-get install ia32-libs-multiarch
    else
        echo ''
        echo "already installed, skipping"
    fi
    set -e
fi

cd $ROOT/externals

if [ ! -e android-scripting ]; then
    echo ''
    echo "Cloning android scripting repo."
    hg clone https://code.google.com/p/android-scripting
fi

#cd $ROOT

#export PATH=$PATH:$PWD/externals/android-ndk-r7b:$PWD/externals/android-scripting/tools/agcc
echo ''
echo "Building JNI"
$ROOT/externals/android-ndk-r7b/ndk-build -C $ROOT/jni

echo ''
echo "Copying libcom_googlecode_android_scripting_Exec.so..."
cp $ROOT/hack/lib/armeabi/libcom_googlecode_android_scripting_Exec.so $ROOT/libs/armeabi/libcom_googlecode_android_scripting_Exec.so


echo ''
echo "Setting up local project configuration..."
$ROOT/externals/android-sdk-linux/tools/android update project -p .


echo ''
echo "Looks like everything went OK."
echo ''
echo 'Now you need to import the project to Eclipse and "Run as Android app", to generate and install the APK to your phone or emulator.'
echo ''
echo '                                OR'
echo ''
echo "Execute \"ant debug\" from the root's project dir. The .apk file will end up in bin/"
echo ''
echo "Have fun!"
