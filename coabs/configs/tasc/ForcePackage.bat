CALL %ALP_INSTALL_PATH%\configs\aef-config\setlibpathAEF.bat

set MYPROPERTIES=%MYPROPERTIES% -Dorg.cougaar.config.path=%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData;%ALP_INSTALL_PATH%\configs\aef-config\RuntimeData\DBSetup;

java  -classpath %LIBPATHS% %MYPROPERTIES% org.cougaar.domain.Airforce.UI.ForcePackageMix.ForcePackageMixResultsApplication
