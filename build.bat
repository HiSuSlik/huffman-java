@echo off
setlocal

if exist out rmdir /s /q out
mkdir out

dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
    echo.
    echo Compilation error.
    del sources.txt > nul 2> nul
    exit /b 1
)

jar --create --file huffman.jar --main-class Main -C out .
if errorlevel 1 (
    echo.
    echo JAR creation error.
    del sources.txt > nul 2> nul
    exit /b 1
)

del sources.txt > nul 2> nul

echo.
echo Build completed successfully.
echo Created: out and huffman.jar.
endlocal
