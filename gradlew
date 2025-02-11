#!/usr/bin/env sh
exec java -Xmx64m "-Dorg.gradle.appname=gradlew" -jar "$0" "$@"
