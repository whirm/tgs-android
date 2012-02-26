#!/bin/bash

export PATH=$PATH:$PWD/externals/android-sdk-linux/platform-tools:$PWD/externals/android-sdk-linux/tools:$PWD/externals/android-ndk-r7b

ndk-build -C jni


ant
