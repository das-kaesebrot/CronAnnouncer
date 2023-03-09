rm -v build/libs/cron-announcer-*.jar
rm -v mc-debug/plugins/cron-announcer-*.jar
./gradlew shadowJar
cp -v build/libs/cron-announcer-*.jar mc-debug/plugins/
docker restart mc-debug
