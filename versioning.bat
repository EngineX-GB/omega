call C:\Users\rm_82\Documents\setenv.bat

mvn help:evaluate -Dexpression=project.version -q -DforceStdout > target/version.txt

rem Set the release version
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit

rem Run a build
mvn clean install -Dmaven.test.skip=true -P prod

rem Placeholder to run git tag

rem Set the new snapshot version
mvn build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit
