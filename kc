#!/bin/bash
PROJECT_DIR="$HOME/Klang"

for arg in "$@"; do
    if [[ -f "$arg" ]]; then
        ARGS+=("$(realpath "$arg")")
    else
        ARGS+=("$arg")
    fi
done

cd "$PROJECT_DIR" || exit 1
./gradlew :cli:run --args="${ARGS[*]}"
