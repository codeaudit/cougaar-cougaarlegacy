@echo off
:
set LIBPATHS=c:\java\jdk1.1.6\lib\classes.zip
set LIBPATHS=%LIBPATHS%;c:\jbuilder2\lib\vbjorb.jar
set LIBPATHS=%LIBPATHS%;c:\jbuilder2\lib\vbjapp.jar
set LIBPATHS=%LIBPATHS%;c:\jbuilder2\lib\vbjcosnm.jar
set LIBPATHS=%LIBPATHS%;c:\jbuilder2\lib\vbjcosev.jar
:

set NAMESERVICECLASS= com.visigenic.vbroker.services.CosNaming.ExtFactory
set ARGUMENTS= -DORBservices=CosNaming -DSVCnameroot=TASC -DJDKrenameBug
set ROOTCONTEXTNAME= TASC
set LOGNAME= namingLog
:
@echo on
java.exe -classpath %LIBPATHS% %ARGUMENTS% %NAMESERVICECLASS% %ROOTCONTEXTNAME% %LOGNAME%
@echo off

