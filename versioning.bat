call C:\Users\rm_82\Documents\setenv.bat

rem Set the release version
call mvn build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion} versions:commit

rem Run a build
call mvn clean install -Dmaven.test.skip=true -P prod

call mvn help:evaluate -Dexpression=project.version -q -DforceStdout > target/version.txt

for /f %%a in (target/version.txt) do set output=%%a

rem Placeholder to run git tag
git tag -a "%output%" -m "Version %output%"
git push origin %output%

rem Set the new snapshot version
call mvn build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit

call mvn help:evaluate -Dexpression=project.version -q -DforceStdout > target/version.txt

for /f %%a in (target/version.txt) do set output=%%a

git add pom.xml
git commit -m "POM version %output%"
git push