rem Script to compile Liaison project

pushd %ALP_INSTALL_PATH%
set BASE=com\prc\alp\liaison
set FILES=%BASE%\*.java
set FILES=%FILES% %BASE%\acl\*.java
set FILES=%FILES% %BASE%\admin\*.java 
set FILES=%FILES% %BASE%\adminGUI\*.java
set FILES=%FILES% %BASE%\assets\*.java 
set FILES=%FILES% %BASE%\interact\*.java 
set FILES=%FILES% %BASE%\plugin\*.java
set FILES=%FILES% %BASE%\retsina\*.java
set FILES=%FILES% %BASE%\test\*.java
set FILES=%FILES% %BASE%\util\*.java
set FILES=%FILES% %BASE%\weather\*.java
jar cf liaisonsrc.jar %FILES%
popd
