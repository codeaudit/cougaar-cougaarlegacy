rem Script to compile Liaison project

echo on

call ..\bin\setliaisonvars

rem Regenerate and recompile all property/asset files
call makeassets

rem compile the code
set LIBPATHS=%ALP_INSTALL_PATH%
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\core.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\planserver.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\glm.jar
set LIBPATHS=%LIBPATHS%;%JINI_PATH%;%COABS_HOME%\lib;%FIPAOS_PATH%;%RETSINA_CLASSPATH%
javac -classpath %LIBPATHS% liaison\*.java
javac -classpath %LIBPATHS% liaison\acl\*.java
javac -classpath %LIBPATHS% liaison\assets\*.java
javac -classpath %LIBPATHS% liaison\admin\*.java
javac -classpath %LIBPATHS% liaison\interact\*.java
javac -classpath %LIBPATHS% liaison\plugin\*.java
javac -classpath %LIBPATHS% liaison\adminGUI\*.java
javac -classpath %LIBPATHS% liaison\retsina\*.java
javac -classpath %LIBPATHS% liaison\test\*.java
javac -classpath %LIBPATHS% liaison\util\*.java
javac -classpath %LIBPATHS% liaison\weather\*.java
rmic -classpath %LIBPATHS% -d %ALP_INSTALL_PATH% com.prc.alp.liaison.admin.LiaisonSpaceListener
jar cf %LIAISON_PATH%\liaison.jar -C %ALP_INSTALL_PATH% com\prc\alp\liaison
pushd %LIAISON_PATH%
jar xvf liaison.jar com\prc\alp\liaison\admin\LiaisonSpaceListener_Stub.class
popd