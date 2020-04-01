#!/usr/bin/env bash
#kill running process
kill -9 $(cat app.pid)
git fetch --all
git reset --hard origin/master
bash scripts/run-app.sh