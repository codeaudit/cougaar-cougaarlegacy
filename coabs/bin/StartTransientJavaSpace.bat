echo off

REM
REM Batch file for starting transient JavaSpace service
REM

call SetLiaisonVars.bat

set OUTRIGGER_JAR=%JINI_LIB%\transient-outrigger.jar
set CODEBASE_URL=http://%LIAISON_JINI_HOST%:%LIAISON_JINI_HTTP_PORT%/outrigger-dl.jar
set LOCATOR=jini://%LIAISON_JINI_HOST%:%LIAISON_DISCOVERY_PORT%/


echo Starting transient JavaSpace service...
java -Djava.security.policy=%LIAISON_LOOKUP_POLICY% -Djava.rmi.server.codebase=%CODEBASE_URL% -Dcom.sun.jini.outrigger.spaceName=%LIAISON_ADMIN_SPACE% -jar %OUTRIGGER_JAR% %LIAISON_JINI_GROUPS% %LOCATOR%
echo Done.								  