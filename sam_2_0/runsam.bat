@echo off
echo "run sam"

set ALP=c:\alp_7.0
set SAMPATH=c:\sam_2.0

# Add sam to the classpath
set CLASSPATH=%SAMPATH%\classes

# Add alpine
set CLASSPATH=%CLASSPATH%;%ALP%\lib\core.jar
set CLASSPATH=%CLASSPATH%;%ALP%\lib\glm.jar
set CLASSPATH=%CLASSPATH%;%ALP%\lib\toolkit.jar
set CLASSPATH=%CLASSPATH%;%ALP%\lib\contract.jar
set CLASSPATH=%CLASSPATH%;%ALP%\lib\xml4j_2_0_11.jar

# Add our 3rd party libraries
set CLASSPATH=%CLASSPATH%;%SAMPATH%\xerces.jar
set CLASSPATH=%CLASSPATH%;%SAMPATH%\diva.jar

echo java -cp %CLASSPATH% sam.main_program 
java -cp %CLASSPATH% sam.main_program 

