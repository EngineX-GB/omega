$DIRECTORY = $PSScriptRoot + "\\scripts"
$TOOLS_DIRECTORY = $PSScriptRoot + "\\tools"
New-Item -ItemType Directory -Force -Path $DIRECTORY
New-Item -ItemType Directory -Force -Path $TOOLS_DIRECTORY

function download_python_runtime {
	New-Item -ItemType Directory -Force -Path $TOOLS_DIRECTORY
	Invoke-WebRequest -Uri "https://github.com/EngineX-GB/resources/releases/download/1.0.0/python-3.10.7-embed-amd64.zip" -OutFile $TOOLS_DIRECTORY\python-3.10.7-embed-amd64.zip
	Expand-Archive -Path $TOOLS_DIRECTORY\python-3.10.7-embed-amd64.zip -DestinationPath $TOOLS_DIRECTORY -Force
	Remove-Item -Path $TOOLS_DIRECTORY\python-3.10.7-embed-amd64.zip -Force
}



download_python_runtime
Invoke-WebRequest https://raw.githubusercontent.com/EngineX-GB/omega-io-utils/master/scripts/model1.py -OutFile $DIRECTORY\model1.py
Invoke-WebRequest https://raw.githubusercontent.com/EngineX-GB/omega-io-utils/master/scripts/model2.py -OutFile $DIRECTORY\model2.py
Invoke-WebRequest https://raw.githubusercontent.com/EngineX-GB/omega-io-utils/master/scripts/model3.py -OutFile $DIRECTORY\model3.py
Invoke-WebRequest https://raw.githubusercontent.com/EngineX-GB/omega-io-utils/master/scripts/model-runner.ps1 -OutFile $DIRECTORY\model-runner.ps1


