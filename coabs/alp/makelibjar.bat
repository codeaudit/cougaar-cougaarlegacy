rem Script to compile Liaison project

pushd %ALP_INSTALL_PATH%
set BASE=com\prc\alp\liaison
set FILES=%BASE%\*.class
set FILES=%FILES% %BASE%\acl\*.class
set FILES=%FILES% %BASE%\admin\*.class 
set FILES=%FILES% %BASE%\adminGUI\*.class
set FILES=%FILES% %BASE%\assets\*.class 
set FILES=%FILES% %BASE%\interact\*.class 
set FILES=%FILES% %BASE%\plugin\*.class
set FILES=%FILES% %BASE%\retsina\*.class
set FILES=%FILES% %BASE%\test\*.class
set FILES=%FILES% %BASE%\util\*.class
set FILES=%FILES% %BASE%\weather\*.class
jar cf liaison.jar %FILES%
popd
