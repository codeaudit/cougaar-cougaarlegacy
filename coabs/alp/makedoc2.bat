rem Script to generate Liaison project javadocs

echo on

call ..\bin\setliaisonvars

rem compile the code
set LIBPATHS=%RETSINA_CLASSPATH%
set DOCPATH=%LIAISON_HOME%\doc\javadoc\com\prc\alp\liaison\retsina
set SOURCEPATH=-sourcepath %ALP_INSTALL_PATH%\com\prc\alp\liaison\retsina
set CLASSES=CNNCurrentWeatherQueryFn CNNForecastWeatherQueryFn WCNForecastWeatherQueryFn
set OPTIONS=-version 
set TITLE=-windowtitle "ALP/Cougaar External Agent Liaison Documentation"

javadoc %TITLE% %OPTIONS% -d %DOCPATH% -classpath %LIBPATHS% %SOURCEPATH% %CLASSES%
