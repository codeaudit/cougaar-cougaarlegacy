
echo off
call setliaisonvars.bat

rem java -cp %RETSINA_CLASSPATH%\infoagent.jar EDU.cmu.softagents.misc.KQMLParser.KQMLMessageSenderGUI
java -cp %RETSINA_CLASSPATH%\infoagent.jar EDU.cmu.softagents.misc.KQMLParser.KQMLMessageSenderGUI
