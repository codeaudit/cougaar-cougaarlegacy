
@echo OFF

REM calls setlibpath.bat which sets the path to the required jar files.
REM calls setarguments.bat which sets input parameters for system behavior
CALL %ALP_INSTALL_PATH%\configs\aef-config\setlibpathAEF.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

REM pass in "NodeName" to run a specific named Node
REM pass in "admin" to run SANode separately

set VMPARAMETERS= -Xms96M -Xmx256M
if "%1"=="SubsNode" set VMPARAMETERS= -Xms32M -Xmx256M
set MYPROPERTIES=%MYPROPERTIES% -Dorg.cougaar.config.path=%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData;%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData\DBSetup;

@ECHO ON

java.exe %VMPARAMETERS% %MYPROPERTIES% -classpath %LIBPATHS% %MYCLASSES% -c -n "%1"

