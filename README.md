# Omega 1.0

A simple tool, and nothing more.

Prerequisites:
- Java 11 or higher
- ffmpeg
- Windows (and soon Linux)
- Fast Internet Connection

### Building the project

To build the project:

Run:

`mvn clean install` to build the project

`mvn clean compile assembly:single` To build the UBER jar in the target folder

### Execute the project

Use the following example:

`java -Dlibrary.path=C:/Users/<username>/Desktop/omega/library -Dtemp.path=C:/users/<username>/Desktop/omega/temp -Dffmpeg.path=C:/Users/<username>/Desktop/ffmpeg-master-latest-win64-gpl/bin -jar omega-1.0-SNAPSHOT-jar-with-dependencies.jar -i <URL> <OUTPUT_NAME>`