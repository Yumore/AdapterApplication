mv ../build/outputs/apk/p9sdkservice-release-unsigned.apk ./
java -jar signapk.jar platform.x509.pem platform.pk8 p9sdkservice-release-unsigned.apk p9sdkservice-release-sign.apk
adb install -r p9sdkservice-release-sign.apk