echo off

REM
REM Batch file for starting a CoABS Grid
REM


call SetLiaisonVars.bat
echo Starting CoABS Grid
echo java -jar %COABS_HOME%/GridManager.jar
cd %COABS_HOME%
java -jar GridManager.jar
echo Done.