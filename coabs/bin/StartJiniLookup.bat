echo off

REM
REM Batch file for starting Jini lookup service
REM


call SetLiaisonVars.bat
set REGGIE_JAR=%JINI_LIB%\reggie.jar
set CODEBASE_URL=http://%LIAISON_JINI_HOST%:%LIAISON_JINI_HTTP_PORT%/reggie-dl.jar
set JINI_DISCOVERY=jini://%LIAISON_JINI_HOST%:%LIAISON_DISCOVERY_PORT%/




echo Attempting to clean old logs...
rmdir %LIAISON_LOOKUP_LOG% /s

echo Starting Jini lookup service...
echo java -jar %REGGIE_JAR% %CODEBASE_URL% %LIAISON_LOOKUP_POLICY% %LIAISON_LOOKUP_LOG% %LIAISON_JINI_GROUPS% %JINI_DISCOVERY% -Djava.security.policy=%LIAISON_JAVA_POLICY%
java -jar %REGGIE_JAR% %CODEBASE_URL% %LIAISON_LOOKUP_POLICY% %LIAISON_LOOKUP_LOG% %LIAISON_JINI_GROUPS% %JINI_DISCOVERY% -Djava.security.policy=%LIAISON_JAVA_POLICY%
echo Done.