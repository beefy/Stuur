#/usr/bin/env bash

cd /home/nate/Work/repos/CrowdShout/web_API_tests/test_1/gs-rest-service/initial;
./gradlew build

gnome-terminal -x sh -c "sleep 3.5; google-chrome http://localhost:8080/send_msg --new-window;"
java -jar build/libs/gs-rest-service-0.1.0.jar

cd ../../..
