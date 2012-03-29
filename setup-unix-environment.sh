#!/bin/bash

#set -x 

ROOT=$PWD
EXTERNALS_DIR=$PWD/externals

SDK_VERSION=17

#NDK_VERSION=5 #Fails to build STLPort
#NDK_VERSION=6b
#NDK_VERSION=6
#NDK_VERSION=7 #Fails to build STLPort
NDK_VERSION=7b

API_VERSION=10

OS=`uname`

if [ "$OS" = "Linux" ]; then
    OS="linux"
    SDK_FILE="android-sdk_r$SDK_VERSION-linux.tgz"
    NDK_FILE="android-ndk-r$NDK_VERSION-linux-x86.tar.bz2"
elif [ "$OS" = "Darwin" ]; then
    OS="macosx"
    SDK_FILE="android-sdk_r$SDK_VERSION-macosx.zip"
    NDK_FILE="android-ndk-r$NDK_VERSION-darwin-x86.tar.bz2"
else
    echo "Unsupported OS, exiting"
    exit 1
fi



get_package_index(){
    echo "$SDKS_BUFFER" | grep "$1" | awk '{ print substr($1, 0, length($1)-1)}' 
}

echo "Trying to detect an already installed android sdk..."
SDK_ROOT=$(readlink -f $(dirname `which android` 2> /dev/null)/.. 2> /dev/null)
if [ "$SDK_ROOT" != "/" ]; then
    echo "  SDK found at $SDK_ROOT"
else
    SDK_ROOT=$EXTERNALS_DIR/android-sdk-$OS
    echo "  SDK not found, will be installed at $SDK_ROOT"
fi

echo "Trying to detect an already installed android ndk..."
NDK_ROOT=$(dirname `which ndk-build` 2> /dev/null)
if [ ! -z "$NDK_ROOT" ]; then
    echo "  NDK found at $NDK_ROOT"
else
    NDK_ROOT=$EXTERNALS_DIR/android-ndk-r$NDK_VERSION
    echo "  NDK not found, will be installed at $NDK_ROOT"
fi


set -e

echo "Ok, let's set this up..."
echo ''

mkdir -p externals/downloads

cd externals/

if [ -e $SDK_ROOT ]; then
    echo "Skipping SDK download as it's already installed."
    echo "If you want to reinstall it, please, delete $SDK_ROOT"
    echo "And re-run this script."
else
    if [ ! -e downloads/$SDK_FILE ]; then
        echo "Downloading android SDK..."
        wget http://dl.google.com/android/$SDK_FILE -O downloads/$SDK_FILE
    else
        echo "Skipping SDK download, already downlodaded."
        echo "If you want to redownload it, please delete $PWD/downloads/$SDK_FILE"
        echo "And re-run this script."
    fi
fi

if [ -e $NDK_ROOT ]; then
    echo "Skipping NDK download as it's already installed."
    echo "If you want to reinstall it, please, delete $NDK_ROOT"
    echo "And re-run this script."
else
    if [ ! -e downloads/$NDK_FILE ]; then
        echo "Downloading android NDK..."
        wget http://dl.google.com/android/ndk/$NDK_FILE -O downloads/$NDK_FILE
    else
        echo "Skipping NDK download, already downlodaded."
        echo "If you want to redownload it, please delete $PWD/downloads/$NDK_FILE"
        echo "And re-run this script."
    fi
fi


if [ ! -e $SDK_ROOT ]; then
    echo "Installing SDK..."
    if [ $OS = "macosx" ]; then
        unzip downloads/$SDK_FILE
    else
        tar xapf downloads/$SDK_FILE
    fi
    echo "  Done."
fi

if [ ! -e $NDK_ROOT ]; then
    echo "Installing NDK..."
    tar xapf downloads/$NDK_FILE
    echo "  Done."
fi


export PATH=$PATH:$SDK_ROOT/tools:$SDK_ROOT/platform-tools:$NDK_ROOT:$PWD/externals/android-scripting/tools/agcc

echo "Installing android platforms and tools..."
if [ ! -e $SDK_ROOT/platforms/android-$API_VERSION ]; then
    #cd android-sdk-linux/tools

    SDKS_BUFFER=`android list sdk`

    PLATFORM=`get_package_index "SDK Platform Android .*, API $API_VERSION"`
    PLATFORM_TOOLS=`get_package_index "Android SDK Platform-tools"`

    android update sdk --no-ui --filter $PLATFORM,$PLATFORM_TOOLS,tool,platform-tool 

    android update adb

    adb kill-server
fi


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

#We aren't using this ATM
#if [ ! -e android-scripting ]; then
#    echo ''
#    echo "Cloning android scripting repo."
#    hg clone https://code.google.com/p/android-scripting
#fi

cd $ROOT

#TODO: Detect if STLport has been already built or not.
#echo ''
#echo "Prebuilding STLport..."
##Use bash instead of sh as it contains bashisms...
#bash $NDK_ROOT/build/tools/build-stlport.sh
#echo ''

echo "Building JNI"
ndk-build -C $ROOT/jni

echo ''
echo "Copying libcom_googlecode_android_scripting_Exec.so..."
cp $ROOT/hack/lib/armeabi/libcom_googlecode_android_scripting_Exec.so $ROOT/libs/armeabi/libcom_googlecode_android_scripting_Exec.so


echo ''
echo "Setting up local project configuration..."
android update project -p .


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
