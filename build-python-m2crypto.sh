#!/bin/bash

set -e

ROOT=$PWD

SDK_VERSION=16
#NDK_VERSION=7b
NDK_VERSION=6


A_SDK=$ROOT/externals/android-sdk-linux
A_NDK=$ROOT/externals/android-ndk-r$NDK_VERSION

export PATH=$PATH:$A_NDK

export ANDROID_SDK=$A_SDK
#Build standalone toolchain
export ANDROID_NDK=$A_NDK
#export SYSROOT=$A_NDK/platforms/android-3/arch-arm
#export NDK=$A_NDK
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

echo "Installing build dependencies"
sudo apt-get build-dep python2.6
sudo apt-get install lib32z1-dev lib32z1 swig

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
ant
cd $ROOT/externals/python-for-android/android/Common
ant
cd $ROOT/externals/python-for-android/android/InterpreterForAndroid
ant


cd $ROOT/externals

#Get python-lib
PYTHON_LIB=python-lib_r16.zip
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


python2.6 setup.py bdist_egg 
