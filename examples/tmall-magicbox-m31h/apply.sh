#!/bin/bash
# Apply the verified neutralization list. Refuses to run without a snapshot:
# everything here is reversible ONLY through the records a snapshot contains.
#   TV=192.168.0.109:5555 ./apply.sh
set -u
TV=${TV:?set TV=host:port}
ADB_SH=${ADB_SH:-adb}
DIR="$(cd "$(dirname "$0")" && pwd)"
ls "$DIR"/../../backups/*/device.txt >/dev/null 2>&1 || { echo "no snapshot found in ../../backups — run snapshot.sh first"; exit 1; }
$ADB_SH connect "$TV" >/dev/null 2>&1 || true
grep -vE '^\s*(#|$)' "$DIR/debloat.txt" | while read -r action pkg extra; do
  case "$action" in
    uninstall)
      echo "uninstall --user 0 (data kept): $pkg"
      $ADB_SH -s "$TV" shell "cmd package uninstall -k --user 0 $pkg" ;;
    appops-deny)
      echo "appops deny $extra: $pkg"
      $ADB_SH -s "$TV" shell "cmd appops set $pkg $extra deny" ;;
    appops-ignore)
      echo "appops ignore $extra: $pkg"
      $ADB_SH -s "$TV" shell "cmd appops set $pkg $extra ignore" ;;
    keep) echo "keep (do nothing): $pkg — $extra" ;;
    *) echo "unknown action: $action" ;;
  esac
done
echo "done. restore with restore.sh"
