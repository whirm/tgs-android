set JAVA_HOME=c:\Progra~1\Java\jdk1.7.0_04

cd jni
CALL win32-build-jni.bat
cd ..

CALL \build\apache-ant-1.8.3\bin\ant release

cd hack
CALL patch2.bat
cd ..
