@echo off
rem --------------------------------------------------------------------------------------
rem --------------------------------------------------------------------------------------
rem -                                                                                 ----
rem -  SetLiasonVars  this script sets all of the environmental variables used to     ----
rem		      run the other scripts that launch JINI services. It is generally ---
rem		      assumed that you are running this script in %LIAISON_HOME%\scripts --
rem  -------------------------------------------------------------------------------------
echo Setting all variables for Liaison project...

rem  --------------------------------------------------------------------------------
rem  --  These are all of the of "foreign" variables used by other systems such as 
rem  --  ALP and COABS which may have some impact on what we need to do. 
rem  ---------------------------------------------------------------------------------

rem Modify the following to represent the parent directory to the Liaison home directory.
rem It is currently the only variable we are sharing with ALP. 

echo Setting relevant variables from other projects...

set ALP_INSTALL_PATH=d:\alp\dev
set AEF_INSTALL_PATH=%ALP_INSTALL_PATH%\aef
set EXT_INSTALL_PATH=%ALP_INSTALL_PATH%\ext

rem The following variables are required by either by the LIAISON project use of
rem COABS or by the COABS Grid itself. 

set COABS_HOME=%EXT_INSTALL_PATH%\coabs
set MACHINE_NAME=hopper
set COABS_PATH=%COABS_HOME%
set JINI_CLASSPATH=%COABS_PATH%\lib\jini-core.jar;%COABS_PATH%\lib\jini-ext.jar;%COABS_PATH%\lib\sun-util.jar;%COABS_PATH%\lib\reggie.jar
set COABS_CLASSPATH=%COABS_PATH%\lib
set XML_CLASSPATH=%COABS_PATH%\lib\xerces.jar

rem The following variables are required for RETSINA agent use

set RETSINA_HOME=%EXT_INSTALL_PATH%\retsina
set RETSINA_LIB=%RETSINA_HOME%\lib
set RETSINA_CLASSPATH=%RETSINA_LIB%\interop.jar;%RETSINA_LIB%\Infoagent.jar;%RETSINA_LIB%\ANS_v2.1.jar;%RETSINA_LIB%\Communicator_99Jun17.jar;%RETSINA_LIB%\KQML_99Jun17.jar;%RETSINA_LIB%\Util_99Jun17.jar
set RETSINA_ANS_NAME=ANSHOST
set RETSINA_ANS_HOST=hopper
set RETSINA_ANS_PORT=6677
set RETSINA_LOG_DIR=%RETSINA_HOME%\logs

rem The following variables are related to the FIPA-OS

set FIPAOS_HOME=%EXT_INSTALL_PATH%\fipa-os
set FIPAOS_PATH=%FIPAOS_HOME%\classes\FIPA_OSv1_3_2.jar

rem -------------------------------------------------------------------------------------
rem -  Variable decalarations for JINI installation (independent of COABS) 
rem ----------------------------------------------------------------------------------

set JINI_HOME=D:\jini1_1b
set JINI_LIB=%JINI_HOME%\lib
set JINI_PATH=%JINI_LIB%\jini-core.jar;%JINI_LIB%\jini-ext.jar;%JINI_LIB%\sun-util.jar;%JINI_LIB%\space-examples.jar;%COABS_CLASSPATH%\reggie.jar
 
rem  -------------------------------------------------------------------------------------
rem  --  Variables for LIAISON  project file structure
rem  -------------------------------------------------------------------------------------

echo Setting the Liaison Project File Structure....
rem  set the following to reflect the LIAISON project directory structure 
set LIAISON_HOME=%ALP_INSTALL_PATH%\com\prc

rem  set the following to reflect the location of source files
set LIAISON_SRC=%LIAISON_HOME%\alp\liaison

rem  set to show the path to executable jar and path files (not necessarily downloadable)
set LIAISON_PATH=%LIAISON_HOME%\lib

rem  set to show the path to executable scripts or other executables are located
set LIAISON_BIN=%LIAISON_HOME%\bin

rem  set to show the path to where configuration files are stored.
set LIAISON_CONFIG=%LIAISON_HOME%\configs

rem  set to show the path to where the policy files are stored. 
set LIAISON_POLICY_DIR=%LIAISON_HOME%\data\policy

rem  set to show the directory where LIAISON logs are kept.
set LIAISON_LOG_DIR=%LIAISON_HOME%\data\logs

rem  set to show path to where downloadable code is stored 
set LIAISON_CODEBASE_DIR=%LIAISON_HOME%\lib

rem ----------------------------------------------------------------------------------------
rem --  Host (Machine) Names  -- Note that the COABS def is above...                      --
rem ----------------------------------------------------------------------------------------

echo Setting Liaison Host names....
rem machine that is hosting JINI if we are doing this independently of another project such
rem as COABS
set LIAISON_JINI_HOST=hopper

rem machine that hosts downloadable code produced for LIAISON
set LIAISON_APP_HOST=hopper

rem ----------------------------------------------------------------------------------------
rem --   Values for different Ports                					  --
rem --    NOTE:  THE COABS Lookup Server is currently hard-wired in its script to use     --
rem --    the default port 4160 for discovery; its HTTP server is hard-wired to use 8080. --
rem ----------------------------------------------------------------------------------------
 
echo Setting Liaison Ports....
rem  modify to reflect port of RMID daemon if run independently by LIAISON
rem  This usually defaults to 1098.  I'll set it there for now. 
set LIAISON_RMID_PORT=1098

rem  modify to reflect the port used LIAISON lookup discovery
set LIAISON_DISCOVERY_PORT=4160

rem  modify to reflect port of HTTP daemon the provides downloadable JINI code if run 
rem  independently by LIAISON
set LIAISON_JINI_HTTP_PORT=8080

rem  modify to reflect port of HTTP daemon that provides downloadable LIAISON code. 
set LIAISON_APP_HTTP_PORT=8082


rem ----------------------------------------------------------------------------------------
rem --------------  Values for different Policy file path names.                           -
rem ----------------------------------------------------------------------------------------

echo Setting Policy paths....

rem  point to the Java policy file
set LIAISON_JAVA_POLICY=%LIAISON_POLICY_DIR%\policy.all

rem  point to the lookup policy file
set LIAISON_LOOKUP_POLICY=%LIAISON_POLICY_DIR%\policy.all

rem point to the transaction policy file
set LIAISON_TXN_POLICY=%LIAISON_POLICY_DIR%\policy.all

rem point to the spaces policy file
set LIAISON_SPACES_POLICY=%LIAISON_POLICY_DIR%\policy.all

rem point to RMI Activation exec policy-- this is only used by the Sun implementation
rem of the RMI daemon
set LIAISON_RMI_EXEC_POLICY=none

rem ----------------------------------------------------------------------------------------
rem -  Values for different logging directories     ----------------------------------------
rem ----------------------------------------------------------------------------------------

echo Setting directory paths....

rem  point to the file where the lookup service keeps its log
set  LIAISON_LOOKUP_LOG=%LIAISON_LOG_DIR%\lookup_log

rem  point to the file where the RMID keeps its log.
set  LIAISON_RMID_LOG=%LIAISON_LOG_DIR%\rmid_log

rem point to the file where the transaction service keeps its log
set  LIAISON_TXN_LOG=%LIAISON_LOG_DIR%\transaction_log



rem ---------------------------------------------------------------------------------
rem --  Miscellaneous LIAISON Variables                                              -
rem ---------------------------------------------------------------------------------

echo Setting Miscellaneous variables....

rem  Modify to reflect JINI groups that LIAISON interacts with
set  LIAISON_JINI_GROUPS=public

rem  Modify to reflect the JavaSpace name used for liaison administration
set  LIAISON_ADMIN_SPACE=ALP_Society

rem --------------------------------------------------------------------------------
rem --------------------------------------------------------------------------------
echo Done Setting Liaison Variables. 








