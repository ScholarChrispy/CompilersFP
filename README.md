# CompilersFP

## Group Members:
- Christopher Johnson (100870882)
- Jack Udeschini (100777605)
- Cameron Millar (100749823)

## Project Requirements:
This project requires `jdk-21` or earlier to build. Please be aware that it **will not build** with `jdk-22` or higher. Make sure that the `jdk-xx/bin` is added to your PATH and JAVA_HOME is set to the `jdk-xx` directory. This has only been tested with `jdk-21`, so there may be issues with earlier versions.

##  Steps for Running the Project:
### 1. Clone the Repository:
Use `git clone "https://github.com/ScholarChrispy/CompilersFP.git"` to clone the repository to your machine. Make sure to `cd` into the new `CompilersFP` directory before continuing with step 2.

### 2. Build the Project:
Run `./gradlew build` to build the project. This may take some time initially, as Gradle needs to download dependencies before the project can be built.

### 3. Run the Project:
Once the project has been built successfully, it can be run with `./gradle run`. This will run the included example file, showcasing the capabilities our our compiler.
