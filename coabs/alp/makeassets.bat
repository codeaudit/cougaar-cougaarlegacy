rem Script to generate asset classes

set LIBPATHS=%ALP_INSTALL_PATH%\lib\core.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\build.jar
echo on

rem Regenerate and recompile all property/asset files
cd liaison\assets
java -classpath %LIBPATHS% org.cougaar.tools.build.PGWriter weather_properties.def
java -classpath %LIBPATHS% org.cougaar.tools.build.AssetWriter weather_properties.def -Pcom.prc.alp.liaison.assets weather_assets.def
cd ..\..
