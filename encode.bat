@echo off
setlocal

if "%~2"=="" (
    echo Usage:
    echo encode.bat input_file output_file
    exit /b 1
)

if not exist huffman.jar (
    call build.bat
    if errorlevel 1 exit /b 1
)

java -jar huffman.jar encode "%~1" "%~2"
endlocal
