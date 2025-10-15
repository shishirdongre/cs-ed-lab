#!/bin/bash

# Yelp Sentiment Analysis Setup Script
# This script sets up the environment and dependencies

echo "=== Yelp Sentiment Analysis Setup ==="
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

# Check Maven
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

# Install Python dependencies
echo "📦 Installing Python dependencies..."
$PYTHON_CMD -m pip install -r requirements.txt

if [ $? -eq 0 ]; then
    echo "✅ Python dependencies installed"
else
    echo "❌ Failed to install Python dependencies"
    exit 1
fi

echo

# Create dataset
echo "📊 Creating dataset..."
$PYTHON_CMD create_simple_yelp_dataset.py

if [ $? -eq 0 ]; then
    echo "✅ Dataset created successfully"
else
    echo "❌ Failed to create dataset"
    exit 1
fi

echo

# Compile Java project
echo "🔨 Compiling Java project..."
mvn clean compile

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
echo "  ./run_analysis.sh"
echo
echo "Or manually:"
echo "  java -cp \".:target/classes:\$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)\" YelpSentimentAnalysisSmileML"