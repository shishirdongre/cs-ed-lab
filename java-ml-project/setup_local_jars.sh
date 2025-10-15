#!/bin/bash

# Local JARs Setup Script for Yelp Sentiment Analysis
# This script sets up the environment with local JAR dependencies

echo "=== Yelp Sentiment Analysis Local JARs Setup ==="
echo

# Check Java
echo "🔍 Checking Java..."
if command -v java &> /dev/null; then
    java -version
    echo "✅ Java is installed"
else
    echo "❌ Java is not installed. Please install Java 11 or higher."
    echo "   Download from: https://adoptium.net/"
    exit 1
fi

echo

# Check Maven (needed for downloading dependencies)
echo "🔍 Checking Maven..."
if command -v mvn &> /dev/null; then
    mvn -version
    echo "✅ Maven is installed"
else
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    echo "   Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

echo

# Check Python
echo "🔍 Checking Python..."
if command -v python3 &> /dev/null; then
    python3 --version
    echo "✅ Python 3 is installed"
    PYTHON_CMD="python3"
elif command -v python &> /dev/null; then
    python --version
    echo "✅ Python is installed"
    PYTHON_CMD="python"
else
    echo "❌ Python is not installed. Please install Python 3.7 or higher."
    echo "   Download from: https://www.python.org/downloads/"
    exit 1
fi

echo

# Download Maven dependencies
echo "📦 Downloading Maven dependencies..."
mvn dependency:copy-dependencies -DoutputDirectory=lib

if [ $? -eq 0 ]; then
    echo "✅ Dependencies downloaded successfully"
    
    # Remove problematic JAR if it exists
    if [ -f "lib/smile-data-3.0.1.jar" ]; then
        echo "   Removing problematic smile-data JAR..."
        rm lib/smile-data-3.0.1.jar
    fi
    
    echo "   JAR files in lib/:"
    ls -la lib/*.jar | wc -l | xargs echo "   Total JARs:"
else
    echo "❌ Failed to download dependencies"
    exit 1
fi

echo

# Create dataset
echo "📊 Creating dataset..."
$PYTHON_CMD create_simple_yelp_dataset_fallback.py

if [ $? -eq 0 ]; then
    echo "✅ Dataset created successfully"
else
    echo "❌ Failed to create dataset"
    exit 1
fi

echo

# Create target directory
echo "📁 Creating target directory..."
mkdir -p target/classes

if [ $? -eq 0 ]; then
    echo "✅ Target directory created"
else
    echo "❌ Failed to create target directory"
    exit 1
fi

echo

# Compile Java project
echo "🔨 Compiling Java project..."
javac -cp "lib/*" -d target/classes YelpSentimentAnalysisSmileML.java

if [ $? -eq 0 ]; then
    echo "✅ Java project compiled successfully"
else
    echo "❌ Failed to compile Java project"
    exit 1
fi

echo
echo "🎉 Setup completed successfully!"
echo
echo "To run the analysis:"
echo "  ./run_with_local_jars.sh"
echo
echo "Or manually:"
echo "  java -cp \".:target/classes:lib/*\" YelpSentimentAnalysisSmileML"
echo
echo "Dependencies included:"
echo "  - Smile ML (smile-core, smile-base)"
echo "  - OpenCSV (CSV handling)"
echo "  - Apache Commons (Lang, Math, Text, Collections)"
echo "  - JUnit 5 (testing framework)"