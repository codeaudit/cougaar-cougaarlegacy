@echo off
if "%1"=="db" call DBSetup PROTOTYPES
if "%1"=="db" call DBSetup AIRCRAFTASSETS
if "%1"=="db" call DBSetup MAINTENANCE
%ALP_INSTALL_PATH%/configs/aef-config/nodeAEF Marines
