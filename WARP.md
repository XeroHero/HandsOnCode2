# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a Java Maven project using Java 25. The actual project code is located in the nested `HandsOnCode2/` directory (the repository has a nested structure).

- **Group ID**: dev.xerohero
- **Artifact ID**: HandsOnCode2
- **Java Version**: 25

## Project Structure

- Source code: `HandsOnCode2/src/main/java/dev/xerohero/`
- Test code: `HandsOnCode2/src/test/java/` (currently empty)
- Build config: `HandsOnCode2/pom.xml`
- Main package: `dev.xerohero`

**Note**: The project has a nested directory structure - the actual Maven project is in `HandsOnCode2/HandsOnCode2/`, not the repository root.

## Common Commands

All Maven commands should be run from the `HandsOnCode2/` subdirectory:

### Build and Compile
```bash
cd HandsOnCode2
mvn compile
```

### Clean Build
```bash
cd HandsOnCode2
mvn clean compile
```

### Run Tests
```bash
cd HandsOnCode2
mvn test
```

### Package
```bash
cd HandsOnCode2
mvn package
```

### Clean
```bash
cd HandsOnCode2
mvn clean
```

## Development Environment

This project is set up for IntelliJ IDEA (`.idea/` configuration present). The IDE configuration includes:
- Maven integration
- Java 25 compiler settings
- UTF-8 encoding

## Code Conventions

- The project uses a custom `IO` class for console output (referenced in Main.java but not yet implemented in the codebase)
- Package structure follows `dev.xerohero.*` naming convention
