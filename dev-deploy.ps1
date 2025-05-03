param([string]$sourceDir,[string]$targetDir)

# Ensure the target directory exists
if (-not (Test-Path -Path $targetDir)) {
    New-Item -Path $targetDir -ItemType Directory | Out-Null
}

# Get the first .zip file in the source directory
$zipFile = Get-ChildItem -Path $sourceDir -Filter "*.zip" | Select-Object -First 1

if ($zipFile) {
    Write-Host "Found ZIP file: $($zipFile.Name)"

    # Copy the zip file to the target directory
    $destinationZip = Join-Path -Path $targetDir -ChildPath $zipFile.Name
    Copy-Item -Path $zipFile.FullName -Destination $destinationZip

    Write-Host "Copied ZIP file to: $destinationZip"

    # Unzip the file into the target directory
    Write-Host "Extracting ZIP file..."
    Expand-Archive -Path $destinationZip -DestinationPath $targetDir -Force

    Write-Host "Extraction complete."

    # Delete the copied zip file from the target directory
    Write-Host "Deleting ZIP file..."
    Remove-Item -Path $destinationZip -Force

    Write-Host "ZIP file deleted."
} else {
    Write-Host "No ZIP file found in the source directory."
}