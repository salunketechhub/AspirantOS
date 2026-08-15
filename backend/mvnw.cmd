@echo off
setlocal

set "MVN_DIST=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698\bin\mvn.cmd"

if exist "%MVN_DIST%" (
    "%MVN_DIST%" %*
    exit /b %ERRORLEVEL%
)

where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    mvn %*
    exit /b %ERRORLEVEL%
)

echo Error: Maven executable not found at %MVN_DIST% and not found on PATH.
exit /b 1
