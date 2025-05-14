#!/usr/bin/env sh
BIN_DIR="$(dirname "$0")/build/install/compiler/bin"
$BIN_DIR/compiler "$@"

gcc "$2" -o "${2%.s}"