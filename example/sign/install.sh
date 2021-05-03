mv ../build/outputs/apk/app-debug.apk ./
java -jar signapk.jar platform.x509.pem platform.pk8 app-debug.apk app-sign.apk
adb install -r app-sign.apk