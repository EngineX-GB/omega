SET /A ARGS_COUNT=0    
FOR %%A in (%*) DO SET /A ARGS_COUNT+=1 

@echo off
call setenv.bat
java -Dlibrary.path=%INSTALL_DIR%/library -Dtemp.path=%INSTALL_DIR%/temp -Dffmpeg.path=%INSTALL_DIR%/ffmpeg-master-latest-win64-gpl/bin -jar omega-1.1-SNAPSHOT-jar-with-dependencies.jar -b %1
@echo on