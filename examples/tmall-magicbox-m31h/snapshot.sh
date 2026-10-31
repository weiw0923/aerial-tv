#!/bin/bash
# Snapshot a TV's state before surgery. Backups land in ./backups/ on the machine
# running this script (gitignored, never committed — the data is device-private).
#   TV=192.168.0.109:5555 ./snapshot.sh            # direct adb
#   ADB_SH="ssh root@relay adb" TV=1.2.3.4:5555 ./snapshot.sh   # via a relay host
set -u
TV=${TV:?set TV=host:port}
ADB_SH=${ADB_SH:-adb}
OUT="${BACKUP_DIR:-$(cd "$(dirname "$0")/../.." && pwd)/backups}/$(date +%F)-$(echo "$TV" | tr ':.' '--')"
mkdir -p "$OUT"
run() { echo "\$ $*" >&2; $ADB_SH -s "$TV" "$@" 2>&1; }
$ADB_SH connect "$TV" >/dev/null 2>&1 || true
{
  echo "model: $(run shell getprop ro.product.model)"
  echo "fingerprint: $(run shell getprop ro.build.fingerprint)"
  echo "sdk: $(run shell getprop ro.build.version.sdk)"
  echo "taken: $(date -u +%FT%TZ)"
} > "$OUT/device.txt"
run shell pm list packages -f        > "$OUT/packages-all.txt"
run shell pm list packages --user 0  > "$OUT/packages-user0.txt"
run shell pm list packages -d        > "$OUT/packages-disabled.txt"
run shell settings list system       > "$OUT/settings-system.txt"
run shell settings list secure       > "$OUT/settings-secure.txt"
run shell settings list global       > "$OUT/settings-global.txt"
run shell cmd package query-activities -a android.intent.action.MAIN -c android.intent.category.HOME > "$OUT/homes.txt"
run shell cmd appops get com.youku.taitan.tv > "$OUT/appops-youku.txt"
run shell cmd appops get app.orchard.tv      > "$OUT/appops-aerial.txt"
( cd "$OUT" && { command -v shasum >/dev/null && shasum -a 256 * || sha256sum *; } > SHA256SUMS )
echo "snapshot written to $OUT ($(ls "$OUT" | wc -l | tr -d ' ') files)"
