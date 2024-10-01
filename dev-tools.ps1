function GetFFMPEG {
    $CURRENT_DIR = Get-Location
    if (Test-Path -Path $CURRENT_DIR\Desktop\ffmpeg-master-latest-win64-gpl -PathType Container) {
        Write-Host("FFMPEG binary already exists. Exiting")
        exit 1
    }
    Invoke-WebRequest "https://github.com/EngineX-GB/resources/releases/download/1.0.0/ffmpeg-master-latest-win64-gpl.zip" -OutFile $CURRENT_DIR\Desktop\ffmpeg-master-latest-win64-gpl.zip
    Expand-Archive -Path $CURRENT_DIR\Desktop\ffmpeg-master-latest-win64-gpl.zip -DestinationPath $CURRENT_DIR\Desktop -Force
    Remove-Item -Path $CURRENT_DIR\Desktop\ffmpeg-master-latest-win64-gpl.zip 
}

GetFFMPEG