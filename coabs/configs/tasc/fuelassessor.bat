CALL %ALP_INSTALL_PATH%\configs\aef-config\setAlpPaths.bat
echo %LIBPATHS%

java -Dorg.cougaar.install.path=%ALP_INSTALL_PATH% -classpath %LIBPATHS% com.centurylogix.CTAssessorPlugin.PluginGUI
