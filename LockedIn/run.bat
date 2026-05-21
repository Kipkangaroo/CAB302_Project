@echo off
echo Building LockedIn...
call mvnw.cmd clean package -q -DskipTests
for %%f in (target\lockedin-*.jar) do set JAR=%%f
java -jar %JAR%
