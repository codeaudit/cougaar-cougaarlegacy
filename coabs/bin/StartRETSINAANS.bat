echo off

REM
REM Batch file for starting a RETSINA Agent Name Service (ANS)


call SetLiaisonVars.bat

echo Attempting to clean old ANS log
del permanentCache /s

echo Starting RETSINA ANS service

echo set CLASSPATH=%RETSINA_CLASSPATH%
set CLASSPATH=%RETSINA_CLASSPATH%

echo java EDU.cmu.softagents.misc.ANS.AgentNameServer :ANSname %RETSINA_ANS_NAME% :ANShost %RETSINA_ANS_HOST% :ANSport %RETSINA_ANS_PORT% :UseBackup %RETSINA_LOG_DIR%
java EDU.cmu.softagents.misc.ANS.AgentNameServer :ANSname %RETSINA_ANS_NAME% :ANShost %RETSINA_ANS_HOST% :ANSport %RETSINA_ANS_PORT% :UseBackup %RETSINA_LOG_DIR%

rem java -cp %RETSINA_CLASSPATH% EDU.cmu.softagents.misc.ANS.GUI.AgentNameServerGUI