@echo OFF

call ..\bin\setliaisonvars

if "%ALP_INSTALL_PATH%"=="" goto AIP_ERROR
if "%1"=="" goto ARG_ERROR

set LIBPATHS=%ALP_INSTALL_PATH%\lib\core.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\alpine.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\xml4j_2_0_11.jar
set LIBPATHS=%LIBPATHS%;%JINI_PATH%;%COABS_HOME%\lib;%FIPAOS_PATH%;%RETSINA_CLASSPATH%
set LIBPATHS=%LIAISON_PATH%\liaison.jar;%LIBPATHS%
rem CLASSPATH=%JINI_CLASSPATH%;%COABS_CLASSPATH%

set DOMAIN=-Dalp.domain.alp=mil.darpa.log.alp.domain.ALPDomain
set ALP_PATH=-Dalp.system.path=%LIBPATHS%
set LIAISON_CODEBASE_PATH=-Djava.rmi.server.codebase=http://%MACHINE_NAME%:%LIAISON_APP_HTTP_PORT%/
set LIAISON_CONFIG_PATH=-Dalp.config.liaison.path=%LIAISON_CONFIG%
set LIAISON_POLICY=-Djava.security.policy=%LIAISON_JAVA_POLICY%
set LIAISON_LOCATOR=-Dcom.prc.alp.liaison.locator=jini://%LIAISON_JINI_HOST%:%LIAISON_DISCOVERY_PORT%/
set LIAISON_GROUPS=-Dcom.sun.jini.lookup.groups=public,coabs
set DISABLE_BOOTSTRAPPER=-Dalp.useBootstrapper=false
REM pass in "NodeName" to run a specific named Node

set DEBUG_PROPS=-Xdebug -Xbootclasspath:D:\jdk1.2.2\jre\lib\rt.jar;D:\jdk1.2.2\lib\tools.jar  -Djava.compiler=NONE
set MYPROPERTIES= %DOMAIN% %DISABLE_BOOTSTRAPPER% %LIAISON_CONFIG_PATH% %LIAISON_POLICY% %LIAISON_CODEBASE_PATH% %LIAISON_LOCATOR%
set MYMEMORY=
set MYCLASSES=alp.society.Node
set MYARGUMENTS= -c -n "%1"

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -cp %LIBPATHS% %MYCLASSES% %MYARGUMENTS% %2 %3
rem java.exe %MYPROPERTIES% -cp %LIBPATHS% com.prc.alp.liaison.LiaisonPlugIn %1
goto QUIT

:AIP_ERROR
echo Please set ALP_INSTALL_PATH
goto QUIT

:ARG_ERROR
echo Run requires an argument  eg: Run ExerciseOneNode
goto QUIT

:QUIT
