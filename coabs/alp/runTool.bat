echo off
rem  This is a script to run sample app I have.  

call ..\bin\setLiaisonVars

rem Set the following to the location of your current jar file and mine. I guess they should 
rem be in lib.  Work on that in the future. 

rem set JOHN_JAR=%LIAISON_PATH%\johnJarFile.jar
rem set BRANDON_JAR=%LIAISON_PATH%\liaison.jar

set TOOL_CLASSPATH=%LIAISON_PATH%\liaison.jar;%JINI_PATH%;%COABS_CLASSPATH%
set SOCIETY_FLAG=-ALP=%LIAISON_ADMIN_SPACE%
set COABS_LOCATOR=-Dcom.prc.alp.liaison.coabs.locator=jini://%MACHINE_NAME%:4160/

echo java -cp %TOOL_CLASSPATH% %COABS_LOCATOR% -Djava.security.policy=%LIAISON_JAVA_POLICY% com.prc.alp.liaison.adminGUI.LiaisonAdminToolController %SOCIETY_FLAG%

java -cp %TOOL_CLASSPATH% %COABS_LOCATOR% -Djava.security.policy=%LIAISON_JAVA_POLICY% com.prc.alp.liaison.adminGUI.LiaisonAdminToolController %SOCIETY_FLAG%