#!/usr/bin/env bash
set -e
echo "Building LockedIn..."
./mvnw clean package -q -DskipTests
JAR=$(ls target/lockedin-*.jar 2>/dev/null | grep -v sources | head -1)
echo "Launching $JAR"
java -jar "$JAR"
