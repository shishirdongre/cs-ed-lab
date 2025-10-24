#!/bin/bash

# Yelp Sentiment Analysis Runner with Local JARs
# This script compiles and runs the Yelp sentiment analysis using local JAR files

echo "=== Yelp Sentiment Analysis Runner (Local JARs) ==="
echo

# Set Java path to use Java 21 (matches JAR file versions)
export PATH="/usr/lib/jvm/java-21-openjdk/bin:$PATH"
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21 or higher."
    exit 1
fi

# Change to the script's directory
cd "$(dirname "$0")"

# Check if dataset exists
if [ ! -f "simple_yelp_reviews.csv" ]; then
    echo "❌ Dataset 'simple_yelp_reviews.csv' not found!"
    echo "   Please run: python3 create_simple_yelp_dataset.py"
    echo "   to generate the dataset first."
    exit 1
fi

echo "🔨 Compiling project..."
javac -encoding UTF-8 -cp "lib/*" YelpSentimentAnalysisSmileML.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed. Please check the errors above."
    exit 1
fi

echo "✅ Compilation successful!"
echo

echo "🚀 Running sentiment analysis..."
echo "=================================="

# Run the analysis using local JARs
java -cp ".:lib/*" YelpSentimentAnalysisSmileML

echo
echo "=================================="
echo "✅ Analysis completed!"
