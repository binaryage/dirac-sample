#!/usr/bin/env bash

set -e

cd "$(dirname "${BASH_SOURCE[0]}")"; cd ..

ROOT=`pwd`

pushd "$ROOT" > /dev/null

./scripts/reveal.cljs $@

popd
