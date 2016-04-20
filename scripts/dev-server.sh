#!/usr/bin/env bash

set -e

cd "$(dirname "${BASH_SOURCE[0]}")"; cd ..

ROOT=`pwd`
DEVSERVER_ROOT="$ROOT/resources/public"
DEVSERVER_PORT=9977

pushd "$DEVSERVER_ROOT"

echo "starting HTTP server on port $DEVSERVER_PORT => http://localhost:$DEVSERVER_PORT"

python -m SimpleHTTPServer "$DEVSERVER_PORT"

popd