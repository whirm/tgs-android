
"c:\Program Files\Android\android-sdk\platform-tools\aapt.exe" a ..\bin\tgs-android-release.apk lib/armeabi/libcom_googlecode_android_scripting_Exec.so

REM Must be 1.6.0_24+, not 1.7, see
REM http://code.google.com/p/android/issues/detail?id=830
"c:\Program Files\Java\jdk1.6.0_31\bin\jarsigner" -verbose -keystore \build\android-keys\tribler-release-key.keystore -digestalg SHA1 -sigalg MD5withRSA -sigfile CERT -signedjar HelloWorld2.apk ..\bin\tgs-android-release.apk tribler
copy HelloWorld2.apk ..\bin\tgs-android-release.apk
