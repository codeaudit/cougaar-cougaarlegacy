@ECHO OFF

call %ALP_INSTALL_PATH%\bin\setlibpath.bat

set LIBPATHS=%ALP_INSTALL_PATH%\aef\lib;%LIBPATHS%
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\plugins\aef.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\lib\ants.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\plugins\jgl.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\plugins\jcchart.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\plugins\classes111.zip
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\plugins\djt.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\plugins\xml4j_2_0_11.jar
set LIBPATHS=%LIBPATHS%;%ALP_INSTALL_PATH%\plugins\mm.mysql-2.0.2-bin.jar

