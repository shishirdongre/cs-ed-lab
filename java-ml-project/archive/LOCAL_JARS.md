# Local JAR Dependencies Setup

This guide explains how to use the Yelp Sentiment Analysis project with locally downloaded JAR files instead of Maven.

## Why Use Local JARs?

- **No Maven required**: Run without Maven installation
- **Faster builds**: No dependency resolution during compilation
- **Offline capable**: Works without internet connection
- **Simpler setup**: Just Java and the JAR files needed
- **Portable**: Easy to distribute with all dependencies included

## Quick Start

### 1. Prerequisites

- Java 11 or higher
- Python 3.7+ (for dataset creation)

### 2. Download Dependencies

```bash
# Download all Maven dependencies as JAR files
mvn dependency:copy-dependencies -DoutputDirectory=lib
```

### 3. Run the Analysis

**Linux/Mac:**
```bash
./run_with_local_jars.sh
```

**Windows:**
```cmd
run_with_local_jars.bat
```

**Manual:**
```bash
# Compile
javac -cp "lib/*" -d target/classes YelpSentimentAnalysisSmileML.java

# Run
java -cp ".:target/classes:lib/*" YelpSentimentAnalysisSmileML
```

## Included Dependencies

The `lib/` directory contains all necessary JAR files:

### Core Libraries
- **smile-core-3.0.1.jar** - Machine learning algorithms
- **smile-base-3.0.1.jar** - Base utilities for Smile ML
- **opencsv-5.7.1.jar** - CSV file handling
- **commons-lang3-3.12.0.jar** - String utilities
- **commons-math3-3.6.1.jar** - Mathematical functions

### Supporting Libraries
- **commons-csv-1.10.0.jar** - CSV processing
- **commons-text-1.10.0.jar** - Text processing
- **commons-collections4-4.4.jar** - Collection utilities
- **commons-beanutils-1.9.4.jar** - Bean utilities
- **commons-logging-1.2.jar** - Logging framework
- **slf4j-api-2.0.6.jar** - Simple Logging Facade
- **parso-2.0.14.jar** - Excel file support

### Testing Libraries
- **junit-jupiter-5.9.2.jar** - JUnit 5 testing framework
- **junit-jupiter-api-5.9.2.jar** - JUnit 5 API
- **junit-jupiter-engine-5.9.2.jar** - JUnit 5 engine
- **junit-jupiter-params-5.9.2.jar** - Parameterized tests
- **junit-platform-commons-1.9.2.jar** - Platform commons
- **junit-platform-engine-1.9.2.jar** - Platform engine
- **opentest4j-1.2.0.jar** - OpenTest4J framework
- **apiguardian-api-1.1.2.jar** - API Guardian

## Project Structure

```
java-ml-project/
├── lib/                                    # Local JAR dependencies
│   ├── smile-core-3.0.1.jar
│   ├── opencsv-5.7.1.jar
│   ├── commons-lang3-3.12.0.jar
│   ├── commons-math3-3.6.1.jar
│   └── ... (other dependencies)
├── target/classes/                         # Compiled Java classes
├── simple_yelp_reviews.csv                # Dataset
├── YelpSentimentAnalysisSmileML.java      # Main class
├── run_with_local_jars.sh                 # Linux/Mac runner
├── run_with_local_jars.bat                # Windows runner
├── create_simple_yelp_dataset_fallback.py # Dataset creation
└── LOCAL_JARS.md                          # This documentation
```

## Commands Reference

### Compilation Commands

```bash
# Create target directory
mkdir -p target/classes

# Compile with all dependencies
javac -cp "lib/*" -d target/classes YelpSentimentAnalysisSmileML.java

# Compile specific files
javac -cp "lib/*" -d target/classes *.java
```

### Execution Commands

```bash
# Run main class
java -cp ".:target/classes:lib/*" YelpSentimentAnalysisSmileML

# Run with custom memory
java -Xmx2g -cp ".:target/classes:lib/*" YelpSentimentAnalysisSmileML

# Run with debug info
java -cp ".:target/classes:lib/*" -Djava.util.logging.config.file=logging.properties YelpSentimentAnalysisSmileML
```

### Windows Commands

```cmd
REM Create target directory
mkdir target\classes

REM Compile
javac -cp "lib\*" -d target\classes YelpSentimentAnalysisSmileML.java

REM Run
java -cp ".;target\classes;lib\*" YelpSentimentAnalysisSmileML
```

## Performance Benefits

### Build Time Comparison

| Method | First Build | Subsequent Builds |
|--------|-------------|-------------------|
| Maven | ~30-60 seconds | ~10-20 seconds |
| Local JARs | ~5-10 seconds | ~2-5 seconds |

### Memory Usage

- **Compilation**: ~100-200MB
- **Execution**: ~200-500MB
- **Total JAR size**: ~50MB

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**
   ```bash
   # Check if all JARs are in lib/ directory
   ls -la lib/
   
   # Verify classpath
   java -cp ".:target/classes:lib/*" -verbose:class YelpSentimentAnalysisSmileML
   ```

2. **Compilation Errors**
   ```bash
   # Check Java version
   java -version
   
   # Clean and recompile
   rm -rf target/classes
   mkdir -p target/classes
   javac -cp "lib/*" -d target/classes YelpSentimentAnalysisSmileML.java
   ```

3. **Missing Dependencies**
   ```bash
   # Re-download dependencies
   mvn dependency:copy-dependencies -DoutputDirectory=lib
   
   # Check for corrupted JARs
   file lib/*.jar
   ```

4. **Dataset Issues**
   ```bash
   # Create dataset manually
   python3 create_simple_yelp_dataset_fallback.py
   
   # Check dataset
   head -5 simple_yelp_reviews.csv
   ```

### Debug Commands

```bash
# Show classpath
java -cp ".:target/classes:lib/*" -XshowSettings:properties -version

# Verbose class loading
java -cp ".:target/classes:lib/*" -verbose:class YelpSentimentAnalysisSmileML

# Memory usage
java -cp ".:target/classes:lib/*" -XX:+PrintGCDetails YelpSentimentAnalysisSmileML
```

## Advantages Over Maven

### Pros
- ✅ **Faster compilation** - No dependency resolution
- ✅ **Offline capable** - No internet required
- ✅ **Simpler setup** - Just Java needed
- ✅ **Portable** - Easy to distribute
- ✅ **No Maven knowledge required** - Standard Java compilation
- ✅ **Explicit dependencies** - See exactly what's included

### Cons
- ❌ **Manual dependency management** - Need to update JARs manually
- ❌ **Larger project size** - JARs included in repository
- ❌ **Version conflicts** - Manual resolution required
- ❌ **No transitive dependency resolution** - Must handle manually

## Best Practices

1. **Keep JARs Updated**
   ```bash
   # Regular dependency updates
   mvn dependency:copy-dependencies -DoutputDirectory=lib -U
   ```

2. **Version Control**
   ```bash
   # Include lib/ in .gitignore for large projects
   echo "lib/" >> .gitignore
   
   # Or track specific versions
   git add lib/smile-core-3.0.1.jar
   ```

3. **Documentation**
   ```bash
   # Document JAR versions
   mvn dependency:list > dependencies.txt
   ```

4. **Testing**
   ```bash
   # Test with different Java versions
   java -version
   ./run_with_local_jars.sh
   ```

## Migration from Maven

If you want to switch from Maven to local JARs:

1. **Download dependencies**
   ```bash
   mvn dependency:copy-dependencies -DoutputDirectory=lib
   ```

2. **Update build scripts**
   ```bash
   # Replace Maven commands with javac/java
   # Update classpath to use lib/* instead of Maven classpath
   ```

3. **Test thoroughly**
   ```bash
   # Run all tests
   ./run_with_local_jars.sh
   ```

4. **Update documentation**
   ```bash
   # Update README and setup instructions
   ```

## Support

For issues with local JAR setup:
1. Check Java version compatibility
2. Verify all JARs are present and not corrupted
3. Ensure classpath is correct
4. Check for version conflicts between JARs