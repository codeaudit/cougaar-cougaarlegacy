CALL %ALP_INSTALL_PATH%\configs\aef-config\setlibpathAEF.bat
echo %LIBPATHS%

java -Dorg.cougaar.install.path=%ALP_INSTALL_PATH% -classpath %LIBPATHS% com.tasc.Airforce.UI.LRC.LRC_Frame
