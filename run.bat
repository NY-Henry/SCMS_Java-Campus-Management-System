@echo off
REM ============================================
REM Smart Campus Management System - Windows
REM Compilation and Execution Script
REM ============================================

echo.
echo ========================================
echo   SCMS - Smart Campus Management System
echo   Ndejje University
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install JDK 8 or higher and add to PATH.
    pause
    exit /b 1
)

echo [Step 1/3] Cleaning previous build...
if exist bin rmdir /s /q bin
mkdir bin

echo.
echo [Step 2/3] Compiling Java files...
echo This may take a moment...
echo.

javac -d bin -cp "lib/*" src/Main.java src/database/*.java src/models/*.java src/services/*.java src/utils/*.java src/gui/*.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo [Step 3/3] Starting application...
echo.
echo ========================================
echo   Login Credentials:
echo   Admin: admin / admin123
echo   Lecturer: lec001 / password123
echo   Student: stu001 / password123
echo ========================================
echo.

java -cp "bin;lib/*" Main

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Application failed to start!
    echo Common issues:
    echo   - MySQL server not running
    echo   - Database 'scms_db' not created
    echo   - Wrong MySQL password in MySQLDatabase.java
    echo.
    pause
    exit /b 1
)

pause
