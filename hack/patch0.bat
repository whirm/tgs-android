c:\Users\arno\AppData\Local\Android\android-sdk\platform-tools\aapt.exe a ..\bin\TriblerDroid-release-0.1.apk lib/armeabi/libcom_googlecode_android_scripting_Exec.so
"c:\Program Files\Java\jdk1.6.0_26\bin\jarsigner" -verbose -keystore c:\users\arno\.android\debug.keystore -storepass android -keypass android -digestalg SHA1 -sigalg MD5withRSA -sigfile CERT -signedjar HelloWorld2.apk ..\bin\TriblerDroid-release-0.1.apk androiddebugkey
copy HelloWorld2.apk ..\bin\TriblerDroid-release-0.1.apk
