adb shell cat /proc/net/dev
adb shell cat /proc/net/xt_qtaguid/stats
adb shell cat /proc/net/xt_qtaguid/stats | findstr 10084
adb shell cat /proc/net/xt_qtaguid/iface_stat_fmt
adb shell cat proc/21/net/dev