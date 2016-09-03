#!/usr/bin/env bash

set -e

cd "$(dirname "${BASH_SOURCE[0]}")"; cd ..

ROOT=`pwd`
DEVSERVER_ROOT="$ROOT/resources/public"
DEVSERVER_PORT=9977

pushd "$DEVSERVER_ROOT" > /dev/null

set +e
PYTHON_PATH=`which python`
set -e
if [ -z "$PYTHON_PATH" ]; then
  echo "Error: python does not seem to be installed on your PATH. We use python to start a simple HTTP server."
  exit 3
else
  echo "Starting HTTP server on port $DEVSERVER_PORT => http://localhost:$DEVSERVER_PORT"
fi

python -m SimpleHTTPServer "$DEVSERVER_PORT" 2> /dev/null \
  || echo "Error: failed to start 'python -m SimpleHTTPServer ...', do you have python properly installed?"

popd
