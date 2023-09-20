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

`mvn clean package -P prod` To build the UBER jar and create the distributable package (in the 'release' folder)

### Execute the project:

Ensure that when you create the final package that the SystemMode enum in `ApplicationRunnerImpl.java` is set to `PRODUCTION`.

You can set the `SystemMode` enum to `DEV` to set up the properties locally on your machine without needing to set them in the run configuration on your IDE

### Running the tool

Use the following scripts:

### `omegai.bat <URL> <OUTPUT_FILENAME> <STRATEGY_MODE>`

Strategy mode can either be `SINGLE` or `MULTI_FILE`

### `omegad.bat <FILE_PATH_TO_INPUT_FILE>`

Input file will be a delimited file (by `|` character) in the following format:

`output_filename|strategymode|pageUrl_for_discovery_service

### `omegab.bat <FILE_PATH_TO_INPUT_FILE>`

Input file will be a delimited file (by `|` character) in the following format:

`output_filename|strategymode|linkurl`

## update the version

Run this maven command to update the version:

`mvn versions:set versions:commit -DnewVersion="1.1.1"`