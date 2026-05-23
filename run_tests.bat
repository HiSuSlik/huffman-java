@echo off
setlocal

call build.bat
if errorlevel 1 exit /b 1

if not exist encoded mkdir encoded
if not exist decoded mkdir decoded

echo.
echo === Test 1: 10 identical characters ===
call encode.bat input\test1.txt encoded\test1.huff
if errorlevel 1 exit /b 1
call decode.bat encoded\test1.huff decoded\test1_decoded.txt
if errorlevel 1 exit /b 1
fc /b input\test1.txt decoded\test1_decoded.txt
if errorlevel 1 exit /b 1

echo.
echo === Test 2: 20-byte file with frequencies 10/5/5 ===
call encode.bat input\test2.txt encoded\test2.huff
if errorlevel 1 exit /b 1
call decode.bat encoded\test2.huff decoded\test2_decoded.txt
if errorlevel 1 exit /b 1
fc /b input\test2.txt decoded\test2_decoded.txt
if errorlevel 1 exit /b 1

echo.
echo === Test 3: binary Main.class file ===
call encode.bat out\Main.class encoded\MainClass.huff
if errorlevel 1 exit /b 1
call decode.bat encoded\MainClass.huff decoded\Main_decoded.class
if errorlevel 1 exit /b 1
fc /b out\Main.class decoded\Main_decoded.class
if errorlevel 1 exit /b 1

echo.
echo Control check completed successfully.
pause
endlocal
