#!/usr/bin/env sh
BIN_DIR="$(dirname "$0")/build/install/compiler/bin"

INPUT="$1"
OUTPUT="$2"
ASM_FILE="${OUTPUT}.s"

$BIN_DIR/compiler "$INPUT" "$ASM_FILE"

gcc "$ASM_FILE" -o "$OUTPUT"