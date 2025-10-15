@echo off
REM Yelp Sentiment Analysis Runner Script for Windows
REM This script compiles and runs the Yelp sentiment analysis

echo === Yelp Sentiment Analysis Runner ===
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 11 or higher.
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed. Please install Maven 3.6 or higher.
    pause
    exit /b 1
)

REM Check if dataset exists
if not exist "simple_yelp_reviews.csv" (
    echo 📊 Dataset not found. Creating dataset...
    python create_simple_yelp_dataset.py
    if %errorlevel% neq 0 (
        echo ❌ Failed to create dataset. Please check Python installation.
        pause
        exit /b 1
    )
)

echo 🔨 Compiling project...
mvn clean compile -q

if %errorlevel% neq 0 (
    echo ❌ Compilation failed. Please check the errors above.
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

echo 🚀 Running sentiment analysis...
echo ==================================

REM Get classpath
for /f "delims=" %%i in ('mvn dependency:build-classpath -q -Dmdep.outputFile=^>NUL 2^>^&1') do set CLASSPATH=.;target/classes;%%i

REM Run the analysis
java -cp "%CLASSPATH%" YelpSentimentAnalysisSmileML

echo.
echo ==================================
echo ✅ Analysis completed!
pause