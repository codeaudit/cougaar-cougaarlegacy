echo off

REM
REM Batch file for starting a RETSINA Grid Interoperator


call SetLiaisonVars.bat

echo Starting RETSINA Grid Interoperator service

echo set CLASSPATH=%RETSINA_CLASSPATH%;%COABS_HOME%\lib
set CLASSPATH=%RETSINA_CLASSPATH%;%JINI_PATH%;%COABS_HOME%\lib
set POLICY=-Djava.security.policy=%LIAISON_JAVA_POLICY%

pushd %LIAISON_CONFIG%\retsina

java %POLICY% EDU.cmu.CoABS.Grid.Proxy.InterOp -n WCNForecastInterop -h %RETSINA_ANS_HOST% -p %RETSINA_ANS_PORT% -a WCNForecastWeatherAgent -d "The Weather Channel Agent providing forecasts (canned data)" -o "weather"

popd