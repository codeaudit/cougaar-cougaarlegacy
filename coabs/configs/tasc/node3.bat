@echo off
if "%1"=="db" call DBSetup PROTOTYPES
if "%1"=="db" call DBSetup UTC
if "%1"=="db" call DBSetup AIRCRAFTASSETS
if "%1"=="db" call DBSetup ASSETS
if "%1"=="db" call DBSetup MAINTENANCE
if "%2"=="db" call DBSetup PROTOTYPES
if "%2"=="db" call DBSetup UTC
if "%2"=="db" call DBSetup AIRCRAFTASSETS
if "%2"=="db" call DBSetup ASSETS
if "%2"=="db" call DBSetup MAINTENANCE
%ALP_INSTALL_PATH%/configs/aef-config/nodeAEF AFNode3
