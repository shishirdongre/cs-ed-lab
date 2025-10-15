@echo off
REM Yelp Sentiment Analysis Runner with Local JARs for Windows
REM This script compiles and runs the Yelp sentiment analysis using local JAR files

echo === Yelp Sentiment Analysis Runner (Local JARs) ===
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 11 or higher.
    pause
    exit /b 1
)

REM Check if dataset exists
if not exist "simple_yelp_reviews.csv" (
    echo 📊 Dataset not found. Creating dataset...
    python create_simple_yelp_dataset_fallback.py
    if %errorlevel% neq 0 (
        echo ❌ Failed to create dataset. Please check Python installation.
        pause
        exit /b 1
    )
)

echo 🔨 Compiling project...
javac -cp "lib\*" -d target\classes YelpSentimentAnalysisSmileML.java

if %errorlevel% neq 0 (
    echo ❌ Compilation failed. Please check the errors above.
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

echo 🚀 Running sentiment analysis...
echo ==================================

REM Run the analysis using local JARs
java -cp ".;target\classes;lib\*" YelpSentimentAnalysisSmileML

echo.
echo ==================================
echo ✅ Analysis completed!
pause