@echo off
rem  This file starts a web server that serves up LIAISON JINI objects. 
rem  All environmental variables used in this script are defined in SetJiniVars.bat
rem==================================================================================
echo Importing Variable definitions...
call SetLiaisonVars.bat

echo Starting Jini web server on port %LIAISON_JINI_HTTP_PORT%...
java -jar %JINI_LIB%\tools.jar -port %LIAISON_JINI_HTTP_PORT% -dir %JINI_LIB% -verbose
