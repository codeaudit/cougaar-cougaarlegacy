: This is the batch file that is used for the installed version of the software.
: This batch is not intended for use during development.
@echo off

CALL %ALP_INSTALL_PATH%\configs\aef-config\setlibpathAEF.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat
set MYCLASSES=org.cougaar.domain.Airforce.DBSetup.SetupDatabase
set MYPROPERTIES=-Dorg.cougaar.install.path=%ALP_INSTALL_PATH% -Duser.timezone=GMT -Dorg.cougaar.config.path=%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData;%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData\DBSetup;

@echo on

java.exe %MYPROPERTIES% -classpath %LIBPATHS% %MYCLASSES% %1
