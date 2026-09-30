@echo off
rem Double-click this file to open the pronunciation review page.
rem It runs on whatever Python is on this computer; if you have the project's
rem virtual environment, use that instead by editing PYTHON below.
setlocal
set PYTHON=python

if exist "..\..\.venv\Scripts\python.exe" set PYTHON=..\..\.venv\Scripts\python.exe
if exist "..\..\..\.venv_tts\Scripts\python.exe" set PYTHON=..\..\..\.venv_tts\Scripts\python.exe

cd /d "%~dp0"
"%PYTHON%" serve.py
if errorlevel 1 pause
