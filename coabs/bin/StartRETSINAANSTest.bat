echo off

REM
REM Batch file for starting a RETSINA Agent Name Service (ANS)


call SetLiaisonVars.bat
echo Starting RETSINA ANS service
java -cp %RETSINA_CLASSPATH% EDU.cmu.softagents.misc.ANS.testANS %RETSINA_ANS_HOST% %RETSINA_ANS_PORT%
