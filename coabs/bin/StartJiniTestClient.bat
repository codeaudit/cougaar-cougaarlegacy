echo off

call SetLiaisonVars.bat

set CODEBASE_URL=http://%LIAISON_JINI_HOST%:%LIAISON_JINI_HTTP_PORT%/jini-examples-dl.jar


echo Starting Jini test client
java -cp %JINI_LIB%\jini-examples.jar -Djava.security.policy=%LIAISON_JAVA_POLICY% -Djava.rmi.server.codebase=%CODEBASE_URL% com.sun.jini.example.browser.Browser
