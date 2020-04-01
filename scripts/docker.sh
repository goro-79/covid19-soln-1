#!/usr/bin/env bash
docker build . -t covid-19:3.7
docker run -p 83:83 -t covid-19:3.7