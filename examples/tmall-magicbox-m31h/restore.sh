#!/bin/bash
# Return the box to the stock state: reinstall what was removed, lift the
# appops limits, hand Home back to the stock launcher.
#   TV=192.168.0.109:5555 ./restore.sh
set -u
TV=${TV:?set TV=host:port}
ADB_SH=${ADB_SH:-adb}
$ADB_SH connect "$TV" >/dev/null 2>&1 || true
for pkg in com.ott.minimonitor com.yunos.tvmgr; do
  echo "reinstall: $pkg"; $ADB_SH -s "$TV" shell "cmd package install-existing $pkg"
done
echo "reset appops: com.youku.taitan.tv"
$ADB_SH -s "$TV" shell "cmd appops reset com.youku.taitan.tv"
echo "default home -> stock launcher"
$ADB_SH -s "$TV" shell "cmd package set-home-activity com.youku.taitan.tv/com.youku.tv.home.activity.HomeActivity"
echo "restored. note: the stock launcher will now front every SCREEN_ON again."
