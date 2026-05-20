@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Maven2 Start Up Batch script
@REM Required ENV vars: JAVA_HOME - location of a JDK home dir
@REM optional ENV vars: MAVEN_BATCH_ECHO, MAVEN_BATCH_PAUSE, MAVEN_OPTS, MAVEN_DEBUG_OPTS

@echo off
setlocal enabledelayedexpansion

set "MAVEN_USER_HOME=%USERPROFILE%\.m2"

if not "%MAVEN_USER_HOME%"=="" goto validateHome
echo Error: MAVEN_USER_HOME is not set
exit /b 1

:validateHome
if exist "%MAVEN_USER_HOME%\wrapper\dists" goto init
echo.
echo Error: MAVEN_USER_HOME environment variable is not set correctly.
echo.
exit /b 1

:init
set "MAVEN_PROJECTBASEDIR=%~dp0"
set "DIST_DIR=%MAVEN_USER_HOME%\wrapper\dists"
set "MAVEN_VERSION=3.9.8"
set "MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_ZIP=%DIST_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip"

if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"

if exist "%MAVEN_ZIP%" goto checkSha

echo Downloading Maven %MAVEN_VERSION% from %MAVEN_URL% ...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%'}"

:checkSha
set "MAVEN_HOME=%DIST_DIR%\apache-maven-%MAVEN_VERSION%"
if exist "%MAVEN_HOME%\bin\mvn.cmd" goto run

echo Extracting Maven %MAVEN_VERSION% ...
powershell -Command "& {Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%DIST_DIR%' -Force}"

:run
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn"

if "%MAVEN_TERMINAL_CMD%"=="" set "MAVEN_TERMINAL_CMD=%MAVEN_CMD%"

%MAVEN_CMD% %MAVEN_OPTS% -f "%MAVEN_PROJECTBASEDIR%\pom.xml" %*

:end
endlocal
