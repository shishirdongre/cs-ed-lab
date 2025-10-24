#!/bin/bash

echo "=== Java Text Classification ML Project ==="
echo ""

# Create lib directory for dependencies
mkdir -p lib

echo "1. Downloading dependencies..."
# Download Smile ML libraries
wget -q -O lib/smile-core-3.0.1.jar https://repo1.maven.org/maven2/com/github/haifengl/smile-core/3.0.1/smile-core-3.0.1.jar
wget -q -O lib/smile-data-3.0.1.jar https://repo1.maven.org/maven2/com/github/haifengl/smile-data/3.0.1/smile-data-3.0.1.jar
wget -q -O lib/opencsv-5.7.1.jar https://repo1.maven.org/maven2/com/opencsv/opencsv/5.7.1/opencsv-5.7.1.jar

echo "   Dependencies downloaded successfully!"

echo ""
echo "2. Compiling Java source files with UTF-16..."
javac -encoding UTF-16 -cp "lib/*" -d target/classes src/main/java/com/example/ml/*.java

if [ $? -eq 0 ]; then
    echo "   Compilation successful!"
else
    echo "   Compilation failed!"
    exit 1
fi

echo ""
echo "3. Running the ML pipeline..."
java -encoding UTF-16 -cp "target/classes:lib/*" com.example.ml.TextClassificationMain

echo ""
echo "=== Pipeline completed! ==="