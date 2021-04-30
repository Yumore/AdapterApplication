adb shell cat /proc/net/dev
adb shell cat /proc/net/xt_qtaguid/stats
adb shell cat /proc/net/xt_qtaguid/stats | findstr 10084
adb shell cat /proc/net/xt_qtaguid/iface_stat_fmt
adb shell cat proc/21/net/dev
###
# app 断网实现
# 网关拦截
# 1. 获取网关
# 2. 修改网关