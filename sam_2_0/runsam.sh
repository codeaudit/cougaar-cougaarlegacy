#!/bin/csh
echo "Running Sam"

set ALPPATH = /shares/projects/alp/alpine_stuff/ALP_7.0
#set SAMPATH = sam_1.3
set SAMPATH = .

# Add sam to the class path
set CLASSPATH = ${SAMPATH}/classes

# Add alpine
set CLASSPATH = ${CLASSPATH}:${ALPPATH}/lib/core.jar
set CLASSPATH = ${CLASSPATH}:${ALPPATH}/lib/glm.jar
set CLASSPATH = ${CLASSPATH}:${ALPPATH}/lib/toolkit.jar
set CLASSPATH = ${CLASSPATH}:${ALPPATH}/lib/contract.jar
set CLASSPATH = ${CLASSPATH}:${ALPPATH}/lib/xml4j_2_0_11.jar

# Add our 3rd party libraries
set CLASSPATH = ${CLASSPATH}:${SAMPATH}/lib/xerces.jar
set CLASSPATH = ${CLASSPATH}:${SAMPATH}/lib/diva.jar


#echo /shares/packages/jdk1.2.2/bin/java -cp ${CLASSPATH} sam.main_program 
#/shares/packages/jdk1.2.2/bin/java -cp ${CLASSPATH} sam.main_program 

echo java -cp ${CLASSPATH} sam.main_program 
java -cp ${CLASSPATH} sam.main_program 

