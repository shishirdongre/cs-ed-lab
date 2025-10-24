# Simple Java Text Classification (No External Dependencies)

This is a simplified version that uses only standard Java libraries for basic text classification demonstration.

## Quick Test (No Maven Required)

1. **Navigate to the project directory:**
   ```bash
   cd java-ml-project
   ```

2. **Run the simple test:**
   ```bash
   ./compile_and_run.sh
   ```

## Alternative: Manual Compilation

If the script doesn't work, you can manually compile and run:

```bash
# Create directories
mkdir -p target/classes lib

# Download dependencies manually
wget -O lib/smile-core-3.0.1.jar https://repo1.maven.org/maven2/com/github/haifengl/smile-core/3.0.1/smile-core-3.0.1.jar
wget -O lib/smile-data-3.0.1.jar https://repo1.maven.org/maven2/com/github/haifengl/smile-data/3.0.1/smile-data-3.0.1.jar
wget -O lib/opencsv-5.7.1.jar https://repo1.maven.org/maven2/com/opencsv/opencsv/5.7.1/opencsv-5.7.1.jar

# Compile
javac -cp "lib/*" -d target/classes src/main/java/com/example/ml/*.java

# Run
java -cp "target/classes:lib/*" com.example.ml.TextClassificationMain
```

## What You'll See

The program will:
1. Create a sample dataset with formal/slang examples
2. Load and preprocess the data
3. Split into training/test sets
4. Train Naive Bayes and Logistic Regression models
5. Evaluate both models
6. Show predictions on new examples
7. Display accuracy results

## Expected Output

```
=== Java Text Classification ML Pipeline ===

1. Creating sample dataset...
   Sample dataset created with 49 examples
   Formal examples: 24
   Slang examples: 25

2. Loading dataset...
   Dataset loaded successfully!
   Shape: 49 rows, 2 columns

3. Text preprocessing...
   Text preprocessing completed!
   Bag of Words vocabulary size: 49
   TF-IDF vocabulary size: 49

4. Train-test split...
   Train-test split completed!
   Training set size: 39
   Test set size: 10

5. Training Naive Bayes model...
   Naive Bayes model trained successfully!

6. Training Logistic Regression model...
   Logistic Regression model trained successfully!

7. Model evaluation...
   [Detailed metrics...]

8. Prediction demo...
   [Example predictions...]

=== SUMMARY ===
Naive Bayes Accuracy: 90.0%
Logistic Regression Accuracy: 100.0%
```

## Troubleshooting

- **Java not found**: Install Java 11+ (`sudo pacman -S jdk11-openjdk` on Manjaro)
- **Permission denied**: Make script executable (`chmod +x compile_and_run.sh`)
- **Network issues**: Download JAR files manually from Maven Central
- **Compilation errors**: Check Java version (`java -version`)

## Files Created

- `formal_slang.csv`: Sample dataset
- `target/classes/`: Compiled Java classes
- `lib/`: Downloaded dependencies

## Customization

- Edit `TextClassificationDemo.java` to add your own examples
- Modify `TextClassificationMain.java` to change parameters
- Add new features in the respective classes