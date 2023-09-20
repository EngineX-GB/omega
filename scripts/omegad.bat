SET /A ARGS_COUNT=0    
FOR %%A in (%*) DO SET /A ARGS_COUNT+=1 

@echo off
call setenv.bat
java -Dlibrary.path=%INSTALL_DIR%/library -Ddiscovery.service.adapter=chrome-testing -Dtemp.path=%INSTALL_DIR%/temp -Dffmpeg.path=%INSTALL_DIR%/tools/ffmpeg-master-latest-win64-gpl/bin -jar omega-@project.version-jar-with-dependencies.jar -d %1
@echo on