@echo off
rem  This file starts a web server that serves up LIAISON JINI objects. 
rem  All environmental variables used in this script are defined in SetJiniVars.bat
rem==================================================================================
echo Importing Variable definitions...
call SetLiaisonVars.bat

echo java -jar %JINI_LIB%\tools.jar -port %LIAISON_APP_HTTP_PORT% -dir %LIAISON_CODEBASE_DIR% -verbose
java -jar %JINI_LIB%\tools.jar -port %LIAISON_APP_HTTP_PORT% -dir %LIAISON_CODEBASE_DIR% -verbose
