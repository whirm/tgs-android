c:\Users\arno\AppData\Local\Android\android-sdk\platform-tools\aapt.exe a ..\bin\tgs-android.apk lib/armeabi/libcom_googlecode_android_scripting_Exec.so

REM Must be 1.6.0_24+, not 1.7, see
REM http://code.google.com/p/android/issues/detail?id=830
"c:\Program Files\Java\jdk1.6.0_26\bin\jarsigner" -verbose -keystore c:\users\arno\.android\debug.keystore -storepass android -keypass android -digestalg SHA1 -sigalg MD5withRSA -sigfile CERT -signedjar HelloWorld2.apk ..\bin\tgs-android.apk androiddebugkey
copy HelloWorld2.apk ..\bin\tgs-android.apk
