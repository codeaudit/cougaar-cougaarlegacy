rem Script to generate Liaison project javadocs

echo on

call ..\bin\setliaisonvars

rem compile the code
set LIBPATHS=%ALP_INSTALL_PATH%
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\core.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\planserver.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\glm.jar
set LIBPATHS=%LIBPATHS%;%JINI_PATH%;%COABS_HOME%\lib;%FIPAOS_PATH%;%RETSINA_CLASSPATH%
set DOCPATH=%LIAISON_HOME%\doc\javadoc
set TITLE=-windowtitle "ALP/Cougaar External Agent Liaison Documentation"
set OPTIONS=-version -overview %LIAISON_SRC%\overview.html
javadoc %OPTIONS% %TITLE% -d %DOCPATH% -classpath %LIBPATHS% -sourcepath %ALP_INSTALL_PATH% @%DOCPATH%\package-list
