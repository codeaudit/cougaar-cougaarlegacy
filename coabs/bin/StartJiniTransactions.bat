echo off

REM
REM Batch file for starting Jini transaction service
REM
REM  revised to use common set of environmental variables in SetLiaisonVars.bat
REM
REM  Note:  If we want, we can give a name to the TransactionManager using an optional 
REM         parameter. 


call SetLiaisonVars.bat





set MAHALO_JAR=%JINI_LIB%\mahalo.jar
set CODEBASE_URL=http://%LIAISON_JINI_HOST%:%LIAISON_JINI_HTTP_PORT%/mahalo-dl.jar
set LOCATOR=jini://%LIAISON_JINI_HOST%:%LIAISON_DISCOVERY_PORT%/

echo Attempting to clean old logs...
rmdir %LIAISON_TXN_LOG% /s

echo Starting Jini transaction service...
java -Djava.security.policy=%LIAISON_JAVA_POLICY% -jar %MAHALO_JAR% %CODEBASE_URL% %LIAISON_LOOKUP_POLICY% %LIAISON_TXN_LOG% %LIAISON_JINI_GROUPS% %LOCATOR%
echo Done.