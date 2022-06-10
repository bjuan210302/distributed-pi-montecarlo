# Configuration

- Gradlew 6.6. Setting is `distributionUrl=https\://services.gradle.org/distributions/gradle-6.6-bin.zip` in `gradle/wrapper/gradle-wrapper.properties`.
- Java 1.8.

# Running and use

Running de `zrunmaster.sh` or `zrunworker.sh` builds and starts the Master and Worker projects. *The server must be running before launching the workers*.
App's entry point are at MasterServer.java and WorkerServer.java files.

Once the Server started, it asks for mode of executing, you can type `"auto"`
to execute the pre-configurated Experiments in `resoruces/default-experiments.csv`. Or you can type `gui` to trigger the GUI to configure an experiment. Setting an experiment requirest the exponent `n` of points you want to calculate (10^n) and the seed. The console, would show progress to make sure it is running. Once the experiment ends the table would be updated with
results, and you can run another one.
