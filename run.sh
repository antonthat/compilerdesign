#!/usr/bin/env sh
set -e

BIN_DIR="$(dirname "$0")/build/install/compiler/bin"

INPUT="$1"
OUTPUT="$2"
ASM_FILE="${OUTPUT}.s"

$BIN_DIR/compiler "$INPUT" "$ASM_FILE"
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
  exit $EXIT_CODE
fi

gcc "$ASM_FILE" -o "$OUTPUT"