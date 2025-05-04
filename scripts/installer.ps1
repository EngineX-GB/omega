param([string]$JAVA_HOME, [string] $INSTALL_DIR)

if (-not $JAVA_HOME) {
    $JAVA_HOME = Read-Host("Enter the path to JDK_HOME")
    Write-Output($JAVA_HOME)

    if (-not $JAVA_HOME) {
        Write-Output "Java Path must be defined"
        exit 1
    }

    if (-not(Test-Path -Path $JAVA_HOME -PathType Container)) {
        Write-Output "$JAVA_HOME is not a valid directory path"
        exit 1
    }

}
if (-not $INSTALL_DIR) {
    $INSTALL_DIR = Read-Host("Enter installation path")
    Write-Output($INSTALL_DIR)

    if (-not $INSTALL_DIR) {
        Write-Output "Installation Path must be defined"
        exit 1
    }

    if (-not(Test-Path -Path $INSTALL_DIR -PathType Container)) {
        Write-Output "$INSTALL_DIR is not a valid directory path"
        exit 1
    }

}


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