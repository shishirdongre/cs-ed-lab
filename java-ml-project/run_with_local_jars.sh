#!/bin/bash

# Yelp Sentiment Analysis Runner with Local JARs
# This script compiles and runs the Yelp sentiment analysis using local JAR files

echo "=== Yelp Sentiment Analysis Runner (Local JARs) ==="
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 11 or higher."
    exit 1
fi

# Check if dataset exists
if [ ! -f "simple_yelp_reviews.csv" ]; then
    echo "📊 Dataset not found. Creating dataset..."
    if command -v python3 &> /dev/null; then
        python3 create_simple_yelp_dataset_fallback.py
    elif command -v python &> /dev/null; then
        python create_simple_yelp_dataset_fallback.py
    else
        echo "❌ Python is not installed. Please install Python to create the dataset."
        exit 1
    fi
fi

echo "🔨 Compiling project..."
javac -cp "lib/*" -d target/classes YelpSentimentAnalysisSmileML.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed. Please check the errors above."
    exit 1
fi

echo "✅ Compilation successful!"
echo

echo "🚀 Running sentiment analysis..."
echo "=================================="

# Run the analysis using local JARs
java -cp ".:target/classes:lib/*" YelpSentimentAnalysisSmileML

echo
echo "=================================="
echo "✅ Analysis completed!"