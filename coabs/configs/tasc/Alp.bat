: This is the batch file that is used for the installed version of the software.
: This batch is not intended for use during development.
@echo off
call setAlpPaths

REM Set parameters for system behavior.

set MYDOMAINS=-Dalp.domain.alp=org.cougaar.domain.glm.ALPDomain
set MYCLASSES=org.cougaar.core.society.Node
set MYPROPERTIES=%MYDOMAINS% -Dorg.cougaar.install.path=%ALP_INSTALL_PATH% -Duser.timezone=GMT

set MYARGUMENTS= -c -n "AFNode"
set VMPARAMETERS= -Xms100M -Xmx300M

echo Starting the ALP  Node.
echo You must manually terminate (Ctrl-C) the ALP Node after ending the ALP demo.
sleep 5
start /MIN java.exe %VMPARAMETERS% %MYPROPERTIES% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%

