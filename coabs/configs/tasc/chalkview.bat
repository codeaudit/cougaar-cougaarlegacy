CALL %ALP_INSTALL_PATH%\configs\aef-config\setlibpathAEF.bat
echo %LIBPATHS%

set MYPROPERTIES=%MYPROPERTIES% -Dorg.cougaar.config.path=%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData;%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData\DBSetup;

java -Dorg.cougaar.install.path=%ALP_INSTALL_PATH% -Duser.timezone=GMT %MYPROPERTIES% -classpath %LIBPATHS% org.cougaar.domain.Airforce.UI.TransportationMap.ChalkChartController
