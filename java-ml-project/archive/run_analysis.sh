#!/bin/bash

# Yelp Sentiment Analysis Runner Script
# This script compiles and runs the Yelp sentiment analysis

echo "=== Yelp Sentiment Analysis Runner ==="
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 11 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

# Check if dataset exists
if [ ! -f "simple_yelp_reviews.csv" ]; then
    echo "📊 Dataset not found. Creating dataset..."
    if command -v python3 &> /dev/null; then
        python3 create_simple_yelp_dataset.py
    elif command -v python &> /dev/null; then
        python create_simple_yelp_dataset.py
    else
        echo "❌ Python is not installed. Please install Python to create the dataset."
        exit 1
    fi
fi

echo "🔨 Compiling project..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed. Please check the errors above."
    exit 1
fi

echo "✅ Compilation successful!"
echo

echo "🚀 Running sentiment analysis..."
echo "=================================="

# Get classpath
CLASSPATH=".:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)"

# Run the analysis
java -cp "$CLASSPATH" YelpSentimentAnalysisSmileML

echo
echo "=================================="
echo "✅ Analysis completed!"