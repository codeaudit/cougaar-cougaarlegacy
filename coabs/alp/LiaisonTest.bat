@echo OFF

call ..\bin\setliaisonvars

if "%ALP_INSTALL_PATH%"=="" goto AIP_ERROR
call %ALP_INSTALL_PATH%\bin\setlibpath.bat
call %ALP_INSTALL_PATH%\bin\setarguments.bat
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\glm.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\planserver.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\xml4j_2_0_11.jar
set LIBPATHS=%LIBPATHS%;%JINI_PATH%;%COABS_HOME%\lib;%FIPAOS_PATH%;%RETSINA_CLASSPATH%
set LIBPATHS=%LIAISON_PATH%\liaison.jar;%LIBPATHS%


set LIAISON_CODEBASE_PATH=-Djava.rmi.server.codebase=http://%LIAISON_JINI_HOST%:%LIAISON_APP_HTTP_PORT%/
set LIAISON_CONFIG_PATH=-Dorg.cougaar.config.liaison.path=%LIAISON_CONFIG%
set LIAISON_POLICY=-Djava.security.policy=%LIAISON_JAVA_POLICY%
set LIAISON_LOCATOR=-Dcom.prc.alp.liaison.locator=jini://%LIAISON_JINI_HOST%:%LIAISON_DISCOVERY_PORT%/
set LIAISON_GROUPS=-Dcom.sun.jini.lookup.groups=public,coabs
set COABS_LOCATOR=-Dcom.prc.alp.liaison.coabs.locator=jini://%MACHINE_NAME%:4160/
set DISABLE_BOOTSTRAPPER=-Dorg.cougaar.useBootstrapper=false

set DEBUG_PROPS=-Xdebug -Xbootclasspath:D:\jdk1.2.2\jre\lib\rt.jar;D:\jdk1.2.2\lib\tools.jar  -Djava.compiler=NONE
set LPROPERTIES=%DISABLE_BOOTSTRAPPER% %LIAISON_CODEBASE_PATH% %LIAISON_CONFIG_PATH% %LIAISON_POLICY% %LIAISON_LOCATOR% %COABS_LOCATOR%
set VMPARAMETERS= -Xms96M -Xmx256M
set MYCLASSES=org.cougaar.core.society.Node
set MYARGUMENTS= -c -n "LiaisonTestNode"

@ECHO ON


java.exe %VMPARAMETERS% %MYPROPERTIES% %LPROPERTIES% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%


goto QUIT

:AIP_ERROR
echo Please set ALP_INSTALL_PATH

:QUIT
