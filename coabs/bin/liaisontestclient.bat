set LIAISON_CODEBASE_PARAM=-Djava.rmi.server.codebase=http://%MACHINE_NAME%:%LIAISON_APP_HTTP_PORT%/
java -cp %LIAISON_HOME%\..\..;%JINI_PATH% %LIAISON_CODEBASE_PARAM% -Djava.security.policy=%LIAISON_JAVA_POLICY% com.prc.alp.liaison.test.LiaisonTestClient %1 %2 %3 %4
