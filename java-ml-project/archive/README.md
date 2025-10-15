# Java Text Classification ML Project

This project implements a text classification system to distinguish between formal and slang language using Java ML libraries, similar to the Python notebook implementation.

## Features

- **Data Creation**: Generates sample formal/slang dataset
- **Text Preprocessing**: Bag of Words and TF-IDF feature extraction
- **ML Models**: Naive Bayes and Logistic Regression classifiers
- **Model Evaluation**: Accuracy, confusion matrix, precision, recall, F1-score
- **Prediction Demo**: Interactive prediction on new text examples

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Quick Start

1. **Navigate to the project directory:**
   ```bash
   cd java-ml-project
   ```

2. **Set up Java environment (if not already done):**
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-24-openjdk
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. **Run the text classification demo:**
   ```bash
   mvn exec:java
   ```

## Alternative: Manual Compilation and Run

```bash
# Compile
mvn compile

# Run with classpath
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" com.example.ml.SimpleTextClassifier
```

## Project Structure

```
src/main/java/com/example/ml/
├── TextClassificationMain.java      # Main class - runs complete pipeline
├── TextClassificationDemo.java       # Sample data creation
├── DataLoader.java                  # CSV loading and DataFrame creation
├── TextPreprocessor.java            # Text preprocessing and feature extraction
├── TrainTestSplit.java              # Train-test split functionality
├── NaiveBayesClassifier.java        # Naive Bayes implementation
├── LogisticRegressionClassifier.java # Logistic Regression implementation
├── ModelEvaluator.java              # Evaluation metrics
└── PredictionDemo.java              # Prediction demonstration
```

## Running Individual Components

You can also run individual components:

```bash
# Create sample data only
mvn exec:java -Dexec.mainClass="com.example.ml.TextClassificationDemo"

# Run main pipeline
mvn exec:java -Dexec.mainClass="com.example.ml.TextClassificationMain"
```

## Dependencies

- **Smile ML Library**: Java equivalent of scikit-learn
  - `smile-core`: Core ML algorithms
  - `smile-data`: Data structures and utilities
- **OpenCSV**: CSV file handling

## Output

The program will:
1. Create a sample dataset with formal and slang examples
2. Load and preprocess the data
3. Split into training and test sets
4. Train both Naive Bayes and Logistic Regression models
5. Evaluate both models with comprehensive metrics
6. Demonstrate predictions on new text examples
7. Display a summary of model performance

## Sample Output

```
=== Simple Text Classification Demo ===

Text Classification Results:
Text			Classification
----------------------------------------
therefore      	formal
omg            	slang
consequently   	formal
lol            	slang
demonstrate    	formal
yolo           	slang
however        	formal
btw            	slang
furthermore    	formal
lit            	slang
illustrate     	formal
fyi            	slang

Feature Extraction Example:
Text: 'therefore'
Features: [9.0, 4.0, 5.0, 0.0, 0.0]
  - Length: 9.0
  - Vowels: 4.0
  - Consonants: 5.0
  - Uppercase: 0.0
  - Special chars: 0.0

✅ Simple text classification completed!
```

## Customization

- Modify `TextClassificationDemo.java` to add your own formal/slang examples
- Adjust train-test split ratio in `TextClassificationMain.java`
- Add new evaluation metrics in `ModelEvaluator.java`
- Extend with additional ML algorithms

## Troubleshooting

- Ensure Java 11+ is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- If you get dependency issues, try: `mvn clean install`