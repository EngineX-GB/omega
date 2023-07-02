SET /A ARGS_COUNT=0    
FOR %%A in (%*) DO SET /A ARGS_COUNT+=1 

@echo off
call %USERPROFILE%\Documents\setenv.bat
java -Dlibrary.path=%USERPROFILE%/Desktop/omega/library -Dtemp.path=%USERPROFILE%/Desktop/omega/temp -Dffmpeg.path=%USERPROFILE%/Desktop/ffmpeg-master-latest-win64-gpl/bin -jar omega-1.0-SNAPSHOT-jar-with-dependencies.jar -i %1 %2
@echo on