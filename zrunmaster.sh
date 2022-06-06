#!/bin/bash

./gradlew build
java -Xmx8g -jar master/build/libs/master.jar
