# File System Command Line Simulator
Main project of the course "Software Engineering 2"

## Description
Given by the professor a base UI and boilerplate code, we were required to develop a working File System.

### Requirements
- Java JDK 17
- Maven 3.x (for building)

### Execution
We recommend running the application via the **-jar-with-dependencies** file, as it includes all dependencies and runs "out of the box".

- Clone the repository
- Build the project with **Maven** (from the repository root)
```
mvn -f backend/pom.xml install
cd frontend
DISPLAY=:99 mvn $MAVEN_OPTS $MAVEN_CLI_OPTS -Dheadless clean package
cd ..
```
- Build without running tests (backend and frontend)
```
mvn -f backend/pom.xml install -DskipTests
cd frontend
mvn clean package -DskipTests
cd ..
```
- Run the .jar file
```
java -jar frontend/target/fscli-jar-with-dependencies.jar
```

### Usage
- Create a new FileSystem via **File → New** or load a saved JSON via **File → Open**
- Save to JSON with **File → Save** / **Save As** (you will be prompted if there are unsaved changes)
- Use the command input to run UNIX-like commands; the system is case-sensitive
- Supported commands: `pwd`, `touch`, `mkdir`, `cd`, `rm`, `rmdir`, `mv`, `ln [-s]`, `ls [-i]`, `clear`, `help`
- Paths can be absolute or relative; `.` `..` and `*` wildcard are supported
- **Help** shows the full command syntax, limits, and UI notes; language can be changed in **Edit → Preferences**

### Authors
- Niccolò Pasqualetti [niccolo.pasqualetti@student.supsi.ch]
- Sebastiano Piubellini [sebastiano.piubellini@student.supsi.ch]
- Daniele Cereghetti [daniele.cereghetti@student.supsi.ch]

This is a snapshot of the final, working version of the project.

### AGILE development
This project has been developed utilizing an AGILE approach, composed of four two weeks iterations.
