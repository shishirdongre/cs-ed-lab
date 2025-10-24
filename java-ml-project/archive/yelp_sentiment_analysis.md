# Yelp Sentiment Analysis

A Java implementation of sentiment analysis for restaurant reviews using the Yelp dataset and professional libraries.

## Overview

This project demonstrates how to build a sentiment classifier using:
- **Real Yelp dataset** (1000 restaurant reviews)
- **Naive Bayes algorithm** for classification
- **Professional libraries** (OpenCSV, Apache Commons)
- **Clean, educational code** with student exercises

## Features

- ✅ Load and process Yelp review dataset
- ✅ Text preprocessing with Apache Commons Lang
- ✅ Train-test split with configurable parameters
- ✅ Naive Bayes sentiment classification
- ✅ Model evaluation with detailed metrics
- ✅ Interactive student exercises
- ✅ Edge case testing

## Dependencies

### Maven Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Smile ML Library - Complete ML toolkit -->
    <dependency>
        <groupId>com.github.haifengl</groupId>
        <artifactId>smile-core</artifactId>
        <version>3.0.1</version>
    </dependency>

    <!-- CSV handling -->
    <dependency>
        <groupId>com.opencsv</groupId>
        <artifactId>opencsv</artifactId>
        <version>5.7.1</version>
    </dependency>
    
    <!-- Apache Commons Lang - String utilities -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
    
    <!-- Apache Commons Math - Statistical functions -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-math3</artifactId>
        <version>3.6.1</version>
    </dependency>

    <!-- JUnit for testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Quick Start

### 1. Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Git

### 2. Clone and Setup

```bash
# Clone the repository
git clone <repository-url>
cd cs-ed-lab/java-ml-project

# Install dependencies
mvn clean install
```

### 3. Prepare Dataset

```bash
# Create Python virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install Python dependencies
pip install datasets pandas

# Download and process Yelp dataset
python create_simple_yelp_dataset.py
```

### 4. Run the Analysis

```bash
# Compile the project
mvn clean compile

# Run the sentiment analysis
java -cp ".:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" YelpSentimentAnalysisSmileML
```

## Commands Reference

### Maven Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the project
mvn package

# Install dependencies
mvn install

# Show dependency tree
mvn dependency:tree

# Get classpath for running
mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout
```

### Java Execution Commands

```bash
# Basic execution
java -cp ".:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" YelpSentimentAnalysisSmileML

# With custom JVM options
java -Xmx2g -cp ".:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" YelpSentimentAnalysisSmileML

# Debug mode
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -cp ".:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" YelpSentimentAnalysisSmileML
```

### Python Dataset Commands

```bash
# Create simple dataset (recommended)
python create_simple_yelp_dataset.py

# Create full dataset (larger)
python download_yelp_dataset.py

# Check dataset files
ls -la *.csv
```

## Project Structure

```
java-ml-project/
├── pom.xml                                    # Maven configuration
├── simple_yelp_reviews.csv                   # Dataset (1000 reviews)
├── YelpSentimentAnalysisSmileML.java         # Main analysis class
├── create_simple_yelp_dataset.py             # Dataset creation script
├── download_yelp_dataset.py                  # Full dataset download
├── yelp_sentiment_analysis.md                # This documentation
└── target/                                   # Maven build output
    └── classes/                              # Compiled Java classes
```

## Performance Results

### Model Performance
- **Overall Accuracy**: 75.0%
- **Positive Class Accuracy**: 69.6%
- **Negative Class Accuracy**: 80.6%
- **Precision**: 78.9%
- **Recall**: 69.6%
- **F1-Score**: 74.0%

### Dataset Statistics
- **Total Reviews**: 1000
- **Positive Reviews**: 500
- **Negative Reviews**: 499
- **Training Set**: 800 reviews
- **Test Set**: 200 reviews
- **Features**: 1000 (Bag of Words)

## Student Exercises

The code includes interactive exercises for learning:

1. **Basic Exercise**: Test your own review examples
2. **Challenge Exercise**: Test edge cases (sarcasm, typos, mixed sentiment)
3. **Reflection Questions**: Think about library benefits and ML concepts

## Library Benefits

### Why Use Professional Libraries?

| Library | Purpose | Benefits |
|---------|---------|----------|
| **OpenCSV** | CSV handling | Handles edge cases, reliable parsing |
| **Apache Commons Lang** | Text processing | Robust string operations, well-tested |
| **Apache Commons Math** | Statistics | Optimized mathematical functions |
| **Smile ML** | Machine Learning | Professional ML algorithms |

### Code Comparison

**Custom Implementation:**
```java
String processed = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", " ");
processed = processed.replaceAll("\\s+", " ").trim();
```

**Library Implementation:**
```java
String processed = StringUtils.lowerCase(text);
processed = StringUtils.replaceChars(processed, "!@#$%^&*()_+-=[]{}|;':\",./<>?`~", " ");
processed = StringUtils.normalizeSpace(processed);
```

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**
   ```bash
   # Make sure classpath includes all dependencies
   mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout
   ```

2. **CSV File Not Found**
   ```bash
   # Run the dataset creation script first
   python create_simple_yelp_dataset.py
   ```

3. **Memory Issues**
   ```bash
   # Increase JVM heap size
   java -Xmx2g -cp "..." YelpSentimentAnalysisSmileML
   ```

4. **Maven Dependencies**
   ```bash
   # Clean and reinstall
   mvn clean install -U
   ```

### Debug Mode

```bash
# Enable debug logging
mvn clean compile -X

# Run with debug output
java -Djava.util.logging.config.file=logging.properties -cp "..." YelpSentimentAnalysisSmileML
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is for educational purposes. Please respect the Yelp dataset terms of use.

## References

- [Yelp Dataset](https://huggingface.co/datasets/Yelp/yelp_review_full)
- [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)
- [Apache Commons Math](https://commons.apache.org/proper/commons-math/)
- [OpenCSV](http://opencsv.sourceforge.net/)
- [Smile ML](https://haifengl.github.io/smile/)