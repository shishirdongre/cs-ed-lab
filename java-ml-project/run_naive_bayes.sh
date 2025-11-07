#!/bin/bash

echo "=== Yelp Sentiment Analysis with LinearSVC ==="
echo "Using Apache Spark 4.0.1 with Java 21"
echo ""

# Check if Spark is set up
if [ ! -d "lib" ] || [ ! -f "lib/spark-core_2.13-4.0.1.jar" ]; then
    echo "❌ Spark 4.0.1 is not set up yet."
    echo ""
    echo "Please run the setup script first:"
    echo "   ./setup_spark.sh"
    echo ""
    echo "This will download and configure Apache Spark 4.0.1 with all required dependencies."
    exit 1
fi

    # Compile the Java file with Spark dependencies
    echo "Compiling..."
    javac -Xlint:-options -cp "lib/*" YelpSentimentAnalysisSpark.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "Running LinearSVC sentiment analysis..."
    echo "=================================================="
    
    # Run the program with local JARs (suppress Spark logs)
    if [ -f "log4j.properties" ]; then
        java -Dlog4j.configuration=file:log4j.properties -Dspark.ui.showConsoleProgress=false -cp ".:lib/*" YelpSentimentAnalysisSpark
    else
        # Fallback: suppress logs via JVM properties
        java -Dlog4j.configuration=org.apache.log4j.ConsoleAppender -Dlog4j.logger.org.apache.spark=WARN -Dspark.ui.showConsoleProgress=false -cp ".:lib/*" YelpSentimentAnalysisSpark
    fi
    
    echo ""
    echo "=================================================="
    echo "Analysis completed!"
else
    echo "Compilation failed!"
    exit 1
fi