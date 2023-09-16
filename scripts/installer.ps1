
$JAVA_HOME = Read-Host("Enter the path to JDK_HOME")
Write-Output($JAVA_HOME)
$INSTALL_DIR = Read-Host("Enter installation path")
Write-Output($INSTALL_DIR)


Write-Output("Extracting files")
Expand-Archive -Path "omega-cmd.zip" -DestinationPath $INSTALL_DIR\omega-cmd -Force

$INSTALL_DIR = $INSTALL_DIR + "\omega-cmd"

$CONTENT = "SET JAVA_HOME=" + $JAVA_HOME +
            "`n" +
            "SET INSTALL_DIR=" + $INSTALL_DIR +
            "`n" +
            "SET PATH=%PATH%;%JAVA_HOME%\bin;"

# unzip the contents in the INSTALL_DIR

# create a setenv.bat file and add it to the INSTALL_DIR path
Set-Content -Path $INSTALL_DIR\setenv.bat -Value $CONTENT