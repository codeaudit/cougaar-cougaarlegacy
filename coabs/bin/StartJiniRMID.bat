echo off
rem
rem  All variable defintions are in SetliaisonVars.bat
rem
call SetLiaisonVars.bat
echo Attempting to clean old RMID log at %LIAISON_RMID_LOG%
rmdir %LIAISON_RMID_LOG% /s
echo Starting RMI Daemon at port %liaison_RMID_PORT%
rem  modified to include both JAVA and sun.rmi.activation.execPolicies.  Currently 
rem  setting all policies to their most lenient settings. 
rmid -port %LIAISON_RMID_PORT% -log %LIAISON_RMID_LOG% -J-Djava.security.policy=%LIAISON_JAVA_POLICY% -J-Dsun.rmi.activation.execPolicy=%LIAISON_RMI_EXEC_POLICY%

