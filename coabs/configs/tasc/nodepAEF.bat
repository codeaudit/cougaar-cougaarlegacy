
@echo OFF

REM calls setlibpath.bat which sets the path to the required jar files.
REM calls setarguments.bat which sets input parameters for system behavior
CALL %ALP_INSTALL_PATH%\configs\aef-config\setlibpathAEF.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

REM pass in "NodeName" to run a specific named Node
REM pass in "admin" to run SANode separately

set MYMEMORY=
set MYARGUMENTS= -c -n "%1"
set VMPARAMETERS= -Xms96M -Xmx256M

if "%1"=="admin" set VMPARAMETERS= -Xmx96M
if "%1"=="admin" set MYARGUMENTS= -n Administrator -c -r -p 8000
if "%1"=="admin" set MYMEMORY= -Djava.compiler=NONE

set MYPROPERTIES=%MYPROPERTIES% -Dorg.cougaar.config.path=%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData;%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData\DBSetup;

@ECHO ON

java.exe %VMPARAMETERS% -Dcreate_supply_tasks=true -Dorg.cougaar.core.cluster.persistence.enable=true -Xbootclasspath:%ALP_INSTALL_PATH%\lib\alpio.jar;%JDK_INSTALL_PATH%\jre\lib\rt.jar %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS% %2 %3

