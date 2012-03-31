#!/bin/bash

set -e

ROOT=$PWD
EXTERNALS_DIR=$PWD/externals

SDK_VERSION=17
NDK_VERSION=7b
#NDK_VERSION=6

OS=`uname`
if [ "$OS" = "Linux" ]; then
    OS="linux"
elif [ "$OS" = "Darwin" ]; then
    OS="macosx"
else
    echo "Unsupported OS, exiting"
    exit 1
fi

PYTHON_LIB_VERSION=r16

echo "Trying to detect an already installed android sdk..."
SDK_ROOT=$(readlink -f $(dirname `which android` 2> /dev/null)/.. 2> /dev/null)
if [ "$SDK_ROOT" != "/" ]; then
    echo "  SDK found at $SDK_ROOT"
else
    SDK_ROOT=$EXTERNALS_DIR/android-sdk-$OS
fi


echo "Trying to detect an already installed android ndk..."
NDK_ROOT=$(dirname `which ndk-build` 2> /dev/null)
if [ ! -z "$NDK_ROOT" ]; then
    echo "  NDK found at $NDK_ROOT"
else
    NDK_ROOT=$EXTERNALS_DIR/android-ndk-r$NDK_VERSION
fi


export PATH=$PATH:$NDK_ROOT

export ANDROID_SDK=$SDK_ROOT
#Build standalone toolchain
export ANDROID_NDK=$NDK_ROOT
export ANDROID_NDK_TOOLCHAIN_ROOT=$PWD/externals/android-toolchain
#$ANDROID_NDK/build/tools/make-standalone-toolchain.sh --platform=android-$SDK_VERSION --install-dir=$ANDROID_NDK_TOOLCHAIN_ROOT
$ANDROID_NDK/build/tools/make-standalone-toolchain.sh --install-dir=$ANDROID_NDK_TOOLCHAIN_ROOT


#hack so python-for-android build scripts find the toolchain binaries as they seem to have different names now...
cd $ANDROID_NDK_TOOLCHAIN_ROOT/bin/
#arm-linux-androideabi-strip to arm-eabi-strip
for FILE in arm-linux-* ; do
    ln -sfv $FILE `echo $FILE | sed 's/linux-android//'`
    #rename 's/linux-android//' *
done
cd $ROOT

cd externals
if [ ! -e python-for-android ]; then
    hg clone https://code.google.com/p/python-for-android/
fi

# for linux, install build deps with apt-get
if [ "$OS" = "linux" ]; then

    echo "Installing build dependencies"
    sudo apt-get build-dep python2.6
    sudo apt-get install lib32z1-dev lib32z1 swig
    # 64 bit systems may be missing ld-linux.so.2
    sudo apt-get install libc6-i386 ia32-libs
fi

# for OS X, set build vars (do we need this if using pre-built libs?)
if [ "$OS" = "macosx" ]; then
    export MACOSX_DEPLOYMENT_TARGET=`sw_vers -productVersion | grep -Po '[0-9]+\.[0-9]*'`
fi

#export LDFLAGS="-L /usr/lib/i386-linux-gnu/"
export LDFLAGS="-L /usr/lib32  -L  $ROOT/externals/android-ndk-r$NDK_VERSION/platforms/android-5/arch-arm/usr/lib/"
export LD_LIBRARY_PATH="/usr/lib32:$ROOT/externals/android-ndk-r$NDK_VERSION/platforms/android-5/arch-arm/usr/lib/"
#/usr/lib/x86_64-linux-gnu/
cd python-for-android/python-build
if [ ! -e python/libs/armeabi/libbz.so ]; then
    #Python has not been built, let's do it.
    #If we don't delete this dir the build script will fail.
    #I think upstream commited it by accident
    rm -fR python-src
    ./build.sh
fi

export PATH=$ANDROID_NDK_TOOLCHAIN_ROOT/bin:$PATH
export CC=arm-linux-androideabi-gcc
export CFLAGS='-march=armv7-a -mfloat-abi=softfp'
export LDFLAGS='-Wl,--fix-cortex-a8'

#TODO:
#This is failing ATM If you dont modify the build.xml files to use android-14
cd $ROOT/externals/python-for-android/android/Utils
mkdir -p dist
ant
cd $ROOT/externals/python-for-android/android/Common
mkdir -p dist
ant
cd $ROOT/externals/python-for-android/android/InterpreterForAndroid
mkdir -p dist
ant


cd $ROOT/externals

#Get python-lib
PYTHON_LIB=python-lib_$PYTHON_LIB_VERSION.zip
if [ ! -e downloads/$PYTHON_LIB ]; then
    cd downloads
    wget https://python-for-android.googlecode.com/files/$PYTHON_LIB
    cd ..
fi


cd $ROOT/externals/python-for-android/python-modules
if [ ! -e python-lib ]; then
    mkdir python-lib
    cd    python-lib
    unzip $ROOT/externals/downloads/$PYTHON_LIB
fi

cd $ROOT

if [ ! -e externals/m2crypto ]; then
    cd externals
    svn co http://svn.osafoundation.org/m2crypto/trunk m2crypto
    cd $ROOT
fi

cd externals/m2crypto
source ..//python-for-android/python-modules/python-lib/setup.sh
#export LDFLAGS="$LD_FLAGS -I$PY4A_INC/python2.6 -L$ROOT/externals/python-for-android/python-build/python/libs/armeabi/ -L$ROOT/externals/python-for-android/python-modules/python-lib/lib"
#export CFLAGS="$CFLAGS -I$PY4A_INC/python2.6 -m32"
#export PY4A_INC=$PY4A_INC/python2.6
#export PYTHONPATH=$PYTHONPATH:$ROOT/externals/python-for-android/python-modules/python-lib/python

# TODO add setuptools to externals/python-for-android/python-build/host
# ie execute http://peak.telecommunity.com/dist/ez_setup.py

# TODO patch m2crypt setup files per http://code.google.com/p/python-for-android/wiki/BuildingModules
../python-for-android/python-build/host/bin/python2.6 setup.py bdist_egg 
