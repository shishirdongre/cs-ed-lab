#!/bin/bash

echo "=== Setting up Apache Spark 4.0.1 for Java ML Project ==="
echo ""

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21 first."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "⚠️  Warning: Java $JAVA_VERSION detected. This project is optimized for Java 21."
    echo "   Consider upgrading to Java 21 for best compatibility."
fi

echo "✅ Java $JAVA_VERSION detected"
echo ""

# Create lib directory
echo "📁 Creating lib directory..."
mkdir -p lib

# Download Spark 4.0.1
echo "⬇️  Downloading Apache Spark 4.0.1..."
SPARK_URL="https://dlcdn.apache.org/spark/spark-4.0.1/spark-4.0.1-bin-hadoop3.tgz"
SPARK_TAR="spark-4.0.1-bin-hadoop3.tgz"

if [ -f "$SPARK_TAR" ]; then
    echo "📦 Spark archive already exists, skipping download"
else
    wget -q --show-progress "$SPARK_URL" -O "$SPARK_TAR"
    if [ $? -ne 0 ]; then
        echo "❌ Failed to download Spark. Please check your internet connection."
        exit 1
    fi
fi

# Extract Spark
echo "📂 Extracting Spark..."
tar -xzf "$SPARK_TAR"
if [ $? -ne 0 ]; then
    echo "❌ Failed to extract Spark archive."
    exit 1
fi

# Copy ALL JAR files to lib directory
echo "📋 Copying all JAR files to lib/..."
SPARK_LIB_DIR="spark-4.0.1-bin-hadoop3/jars"

# Copy all JAR files from Spark distribution
cp "$SPARK_LIB_DIR"/*.jar lib/

# Clean up
echo "🧹 Cleaning up temporary files..."
rm -rf spark-4.0.1-bin-hadoop3/
rm -f spark-4.0.1-bin-hadoop3.tgz

# Count JAR files
JAR_COUNT=$(find lib -name "*.jar" | wc -l)
echo ""
echo "✅ Setup complete!"
echo "📊 Installed $JAR_COUNT JAR files in lib/ directory"
echo ""
echo "🚀 You can now run the sentiment analysis:"
echo "   ./run_naive_bayes.sh"
echo ""
echo "📚 For more information, see README_NAIVE_BAYES.md"