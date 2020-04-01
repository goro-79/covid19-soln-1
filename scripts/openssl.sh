#!/usr/bin/env bash
openssl pkcs12 -export -in certs/certificate.crt  -inkey certs/private.key \
-out src/main/resources/cert.p12 -name covid-19.goro.live
exit