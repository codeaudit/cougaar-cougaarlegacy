#!/bin/sh

ALPPATH=/shares/projects/alp/alpine_stuff/alp/lib
PACKAGES="sam sam.LoadIniFiles sam.LoadContractFiles sam.display sam.graphPlanner"

echo " ----------- Building javadocs ----------"
echo "Getting ALP libraries from " ${ALPPATH}

echo javadoc -d docs/javadoc -classpath lib/diva.jar:lib/xerces.jar:${ALPPATH}/core.jar:${ALPPATH}/xml4j_2_0_11.jar -sourcepath src -package ${PACKAGES}
javadoc -d docs/javadoc -classpath lib/diva.jar:lib/xerces.jar:${ALPPATH}/core.jar:${ALPPATH}/xml4j_2_0_11.jar -sourcepath src -package ${PACKAGES}
