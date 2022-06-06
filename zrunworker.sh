#!/bin/bash

./gradlew build
java -Xmx8g -jar worker/build/libs/worker.jar
