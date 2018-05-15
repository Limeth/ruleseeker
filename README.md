# ruleseeker
Distributed cellular automaton space miner.

## Compilation
You need maven to compile the project. Run the following command, which will
generate a `.jar` file in the `target` directory:

```
mvn clean compile assembly:single
```

Tests can be run using

```
mvn test
```

## Usage
To run the GUI simulator, just execute the JAR file with no arguments.

```
cd target
java -jar ruleseeker-*-jar-with-dependencies.jar
```

To display help for command line arguments, execute the JAR file with the
`--help` flag:

```
java -jar ruleseeker-*-jar-with-dependencies.jar --help
```
