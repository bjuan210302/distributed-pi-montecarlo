#!/bin/bash

./gradlew build
java -jar worker/build/libs/worker.jar
