# Shared backup location for snapshot.sh and apply.sh.
# BACKUP_DIR, when set, is the directory that holds dated snapshot folders
# (each folder contains device.txt). The default is the repo-level backups/
# directory, which is gitignored. Do not run this file; source it.
# shellcheck shell=bash
_backup_here="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$_backup_here/../.." && pwd)"
BACKUP_ROOT="${BACKUP_DIR:-$REPO_ROOT/backups}"
unset _backup_here
