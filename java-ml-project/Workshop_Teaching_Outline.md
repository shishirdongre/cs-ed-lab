# Java Machine Learning Workshop: Naive Bayes Sentiment Analysis

## Workshop Overview
This workshop teaches students how to implement a complete machine learning pipeline in Java using the Smile library for sentiment analysis of Yelp reviews.

## Learning Objectives
- Understand the complete ML pipeline from data loading to model evaluation
- Learn Java design patterns for data handling
- Implement text preprocessing and feature extraction
- Use external libraries (Smile) for machine learning
- Apply proper software engineering practices in ML code

---

## Part 1: Project Setup and Dependencies

### 1.1 Understanding the Project Structure
```
java-ml-project/
├── lib/                          # External JAR files
│   ├── smile-core-3.0.1.jar     # Machine learning library
│   ├── smile-base-3.0.1.jar     # Statistical distributions
│   └── opencsv-5.7.1.jar        # CSV file handling
├── simple_yelp_reviews.csv       # Dataset
└── YelpSentimentAnalysisSmileML.java  # Main implementation
```

### 1.2 Key Dependencies and Why We Need Them
- **OpenCSV**: For reading CSV files (Java doesn't have built-in CSV support)
- **Smile Library**: Professional ML library (better than writing algorithms from scratch)
- **Apache Commons**: String utilities for text preprocessing
- **SLF4J**: Logging framework (comes with Smile)

### 1.3 Compilation and Execution
```bash
# Compile with classpath
javac -cp "lib/*" YelpSentimentAnalysisSmileML.java

# Run with classpath
java -cp ".:lib/*" YelpSentimentAnalysisSmileML
```

---

## Part 2: Data Loading and Preprocessing Pipeline

### 2.1 Step 1: Import Required Libraries
```java
import java.util.*;
import java.io.*;
import com.opencsv.CSVReader;                    // For CSV handling
import org.apache.commons.lang3.StringUtils;    // For text preprocessing
import smile.classification.NaiveBayes;         // ML algorithm
import smile.stat.distribution.Distribution;    // Statistical distributions
import smile.stat.distribution.GaussianDistribution; // Gaussian fitting
```

**Teaching Point**: Explain why we need external libraries and how to manage dependencies.

### 2.2 Step 2: Data Loading with CSV Reader
```java
private static DataPreparationResult loadAndPrepareData() {
    List<String[]> csvData = new ArrayList<>();
    try (CSVReader reader = new CSVReader(new FileReader("simple_yelp_reviews.csv"))) {
        csvData = reader.readAll();
    } catch (IOException | com.opencsv.exceptions.CsvException e) {
        throw new RuntimeException("Error loading CSV file", e);
    }
    // ... rest of the method
}
```

**Teaching Points**:
- **Try-with-resources**: Automatic resource management
- **Exception handling**: Proper error handling in file I/O
- **CSV structure**: Understanding the data format

### 2.3 Step 3: Data Extraction and Preprocessing
```java
// Extract texts and labels
String[] texts = new String[csvData.size() - 1];
String[] labels = new String[csvData.size() - 1];

for (int i = 1; i < csvData.size(); i++) {
    texts[i-1] = csvData.get(i)[0];    // Review text
    labels[i-1] = csvData.get(i)[1];   // Sentiment label
}

// Text preprocessing pipeline
String[] processedTexts = Arrays.stream(texts)
    .map(text -> StringUtils.lowerCase(text))                    // Convert to lowercase
    .map(text -> StringUtils.replaceChars(text, "!@#$%^&*()_+-=[]{}|;':\",./<>?`~", " "))  // Remove punctuation
    .map(text -> StringUtils.normalizeSpace(text))               // Normalize whitespace
    .toArray(String[]::new);
```

**Teaching Points**:
- **Array indexing**: Why we start from index 1 (skip header)
- **Stream API**: Functional programming approach to data transformation
- **Text preprocessing**: Why each step is necessary for ML

---

## Part 3: Design Patterns and Data Structures

### 3.1 The Container Class Pattern (Data Transfer Object)

**Problem**: Java methods can only return one value, but we need to return multiple related pieces of data.

**Solution**: Create container classes that group related data together.

```java
/**
 * Data preparation result container
 * Groups related data from the preprocessing step
 */
private static class DataPreparationResult {
    final String[] originalTexts;    // Raw text data
    final String[] processedTexts;   // Preprocessed text data
    final String[] labels;           // Class labels
    
    DataPreparationResult(String[] originalTexts, String[] processedTexts, String[] labels) {
        this.originalTexts = originalTexts;
        this.processedTexts = processedTexts;
        this.labels = labels;
    }
}
```

**Design Pattern**: **Data Transfer Object (DTO)**
- **Purpose**: Transfer multiple related data items between methods
- **Benefits**: 
  - Type safety
  - Clear data relationships
  - Easy to extend
  - Self-documenting code

### 3.2 Other Container Classes in the Project

```java
// For train-test split results
private static class TrainTestSplitResult {
    final double[][] trainFeatures;
    final double[][] testFeatures;
    final int[] trainLabels;
    final int[] testLabels;
    // Constructor...
}

// For model training results
private static class ModelTrainingResult {
    final NaiveBayes nbModel;
    final double[][] features;
    final TrainTestSplitResult split;
    final int[] predictions;
    final String[] originalLabels;
    // Constructor...
}

// For evaluation metrics
private static class ClassificationMetrics {
    final double precision;
    final double recall;
    final double f1;
    // Constructor...
}
```

**Teaching Points**:
- **Immutability**: Using `final` fields for data integrity
- **Encapsulation**: Grouping related data together
- **Method chaining**: How containers enable clean method signatures

---

## Part 4: Feature Engineering

### 4.1 Bag of Words Implementation
```java
private static double[][] createBagOfWordsFeatures(String[] texts) {
    double[][] features = new double[texts.length][1000]; // Simplified feature size
    
    for (int i = 0; i < texts.length; i++) {
        String[] words = texts[i].split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                int hash = Math.abs(word.hashCode()) % 1000;
                features[i][hash]++;
            }
        }
    }
    return features;
}
```

**Teaching Points**:
- **Feature extraction**: Converting text to numerical features
- **Hashing trick**: Efficient way to handle large vocabularies
- **Sparse representation**: Why we use counts instead of binary features

### 4.2 Label Encoding
```java
private static int[] convertLabelsToInt(String[] labels) {
    int[] intLabels = new int[labels.length];
    for (int i = 0; i < labels.length; i++) {
        intLabels[i] = "positive".equals(labels[i]) ? 1 : 0;
    }
    return intLabels;
}
```

**Teaching Points**:
- **Categorical encoding**: Converting strings to numbers
- **Binary classification**: Two-class problem setup

---

## Part 5: Train-Test Split Implementation

### 5.1 The Split Algorithm
```java
private static TrainTestSplitResult performTrainTestSplit(double[][] features, int[] labels, 
                                                        double testSize, long randomSeed) {
    Random random = new Random(randomSeed);
    int totalSize = features.length;
    int testSizeInt = (int) (totalSize * testSize);
    
    // Create indices and shuffle
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < totalSize; i++) {
        indices.add(i);
    }
    Collections.shuffle(indices, random);
    
    // Split indices
    List<Integer> testIndices = indices.subList(0, testSizeInt);
    List<Integer> trainIndices = indices.subList(testSizeInt, totalSize);
    
    // Create arrays using stream API
    double[][] trainFeatures = trainIndices.stream().map(i -> features[i]).toArray(double[][]::new);
    double[][] testFeatures = testIndices.stream().map(i -> features[i]).toArray(double[][]::new);
    int[] trainLabels = trainIndices.stream().mapToInt(i -> labels[i]).toArray();
    int[] testLabels = testIndices.stream().mapToInt(i -> labels[i]).toArray();
    
    return new TrainTestSplitResult(trainFeatures, testFeatures, trainLabels, testLabels);
}
```

**Teaching Points**:
- **Random sampling**: Why we shuffle before splitting
- **Reproducibility**: Using random seeds for consistent results
- **Stream API**: Modern Java approach to array operations
- **Data integrity**: Ensuring features and labels stay aligned

---

## Part 6: Machine Learning with Smile Library

### 6.1 Understanding the Smile NaiveBayes Constructor
```java
public NaiveBayes(double[] priori, Distribution[][] condprob)
```

**Parameters**:
- `priori`: Prior probabilities P(class) for each class
- `condprob`: Conditional distributions P(feature|class) for each feature in each class

### 6.2 Training the Model
```java
private static NaiveBayes trainSmileNaiveBayes(double[][] features, int[] labels) {
    int numClasses = 2;
    int numFeatures = features[0].length;
    
    // 1. Calculate prior probabilities
    double[] priori = new double[numClasses];
    for (int label : labels) {
        priori[label]++;
    }
    for (int i = 0; i < numClasses; i++) {
        priori[i] /= labels.length;
    }
    
    // 2. Calculate conditional distributions
    Distribution[][] condprob = new Distribution[numClasses][numFeatures];
    
    for (int classIdx = 0; classIdx < numClasses; classIdx++) {
        // Get features for this class
        List<double[]> classFeatures = new ArrayList<>();
        for (int i = 0; i < features.length; i++) {
            if (labels[i] == classIdx) {
                classFeatures.add(features[i]);
            }
        }
        
        // Fit Gaussian distribution for each feature
        for (int featureIdx = 0; featureIdx < numFeatures; featureIdx++) {
            double[] featureValues = new double[classFeatures.size()];
            for (int i = 0; i < classFeatures.size(); i++) {
                featureValues[i] = classFeatures.get(i)[featureIdx];
            }
            condprob[classIdx][featureIdx] = GaussianDistribution.fit(featureValues);
        }
    }
    
    return new NaiveBayes(priori, condprob);
}
```

**Teaching Points**:
- **Statistical learning**: Fitting probability distributions to data
- **Gaussian assumption**: Why we use normal distributions
- **Class-conditional independence**: The "naive" assumption in Naive Bayes

---

## Part 7: Model Evaluation

### 7.1 Accuracy Calculation
```java
private static double calculateAccuracy(int[] trueLabels, int[] predictedLabels) {
    int correct = 0;
    for (int i = 0; i < trueLabels.length; i++) {
        if (trueLabels[i] == predictedLabels[i]) {
            correct++;
        }
    }
    return (double) correct / trueLabels.length;
}
```

### 7.2 Confusion Matrix
```java
private static int[][] calculateConfusionMatrix(int[] trueLabels, int[] predictedLabels) {
    int[][] matrix = new int[2][2];
    for (int i = 0; i < trueLabels.length; i++) {
        matrix[trueLabels[i]][predictedLabels[i]]++;
    }
    return matrix;
}
```

### 7.3 Classification Metrics
```java
private static ClassificationMetrics calculateClassificationMetrics(int[] trueLabels, int[] predictedLabels) {
    int[][] matrix = calculateConfusionMatrix(trueLabels, predictedLabels);
    
    double precision = (double) matrix[1][1] / (matrix[0][1] + matrix[1][1]);
    double recall = (double) matrix[1][1] / (matrix[1][0] + matrix[1][1]);
    double f1 = 2 * (precision * recall) / (precision + recall);
    
    return new ClassificationMetrics(precision, recall, f1);
}
```

**Teaching Points**:
- **Evaluation metrics**: Different ways to measure model performance
- **Precision vs Recall**: Trade-offs in classification
- **F1-Score**: Harmonic mean of precision and recall

---

## Part 8: Main Program Flow

### 8.1 The Complete Pipeline
```java
public static void main(String[] args) {
    System.out.println("=== Yelp Review Sentiment Analysis ===");
    
    try {
        // 1. Load and prepare data
        DataPreparationResult dataResult = loadAndPrepareData();
        
        // 2. Train the model
        ModelTrainingResult model = trainModel(dataResult);
        
        // 3. Evaluate the model
        evaluateModel(model);
        
        // 4. Test on sample reviews
        testSampleReviews(model);
        
        System.out.println("\n✅ Analysis completed!");
        
    } catch (Exception e) {
        System.err.println("Error in Yelp sentiment analysis: " + e.getMessage());
        e.printStackTrace();
    }
}
```

**Teaching Points**:
- **Pipeline architecture**: Sequential processing steps
- **Error handling**: Graceful failure management
- **Separation of concerns**: Each method has a single responsibility

---

## Part 9: Key Programming Concepts

### 9.1 Object-Oriented Design
- **Encapsulation**: Data and methods grouped together
- **Abstraction**: Hiding implementation details
- **Composition**: Using objects within objects

### 9.2 Functional Programming
- **Stream API**: Processing collections declaratively
- **Method references**: `String[]::new`, `i -> features[i]`
- **Lambda expressions**: `text -> StringUtils.lowerCase(text)`

### 9.3 Error Handling
- **Try-catch blocks**: Handling exceptions gracefully
- **Try-with-resources**: Automatic resource management
- **Runtime exceptions**: When to throw vs catch

### 9.4 Data Structures
- **Arrays**: Fixed-size collections
- **Lists**: Dynamic collections
- **Maps**: Key-value pairs for lookups

---

## Part 10: Workshop Exercises

### 10.1 Beginner Exercises
1. **Modify text preprocessing**: Add stemming or lemmatization
2. **Change feature size**: Experiment with different bag-of-words dimensions
3. **Add new metrics**: Implement specificity or sensitivity

### 10.2 Intermediate Exercises
1. **Cross-validation**: Implement k-fold cross-validation
2. **Feature selection**: Add chi-square feature selection
3. **Hyperparameter tuning**: Experiment with different smoothing parameters

### 10.3 Advanced Exercises
1. **Different algorithms**: Implement SVM or Random Forest
2. **Ensemble methods**: Combine multiple models
3. **Real-time prediction**: Add a web interface for live predictions

---

## Part 11: Common Pitfalls and Debugging

### 11.1 Common Issues
- **Classpath problems**: Missing JAR files
- **Array bounds**: Index out of bounds exceptions
- **Null pointers**: Uninitialized objects
- **Memory issues**: Large datasets causing OutOfMemoryError

### 11.2 Debugging Tips
- **Print statements**: Add logging to track data flow
- **Unit testing**: Test individual methods
- **Data validation**: Check data at each step
- **Performance profiling**: Monitor memory and CPU usage

---

## Part 12: Extensions and Real-World Applications

### 12.1 Production Considerations
- **Scalability**: Handling large datasets
- **Performance**: Optimizing for speed
- **Monitoring**: Tracking model performance over time
- **Deployment**: Packaging and distribution

### 12.2 Real-World Applications
- **Social media analysis**: Twitter sentiment
- **Customer feedback**: Product reviews
- **Financial analysis**: News sentiment
- **Healthcare**: Patient feedback analysis

---

## Conclusion

This workshop teaches students how to build a complete machine learning pipeline in Java, emphasizing:
- **Software engineering practices** in ML
- **Design patterns** for data handling
- **Library integration** for complex algorithms
- **Evaluation methodologies** for model assessment

The code demonstrates how to combine multiple Java concepts (OOP, functional programming, exception handling) with machine learning principles to create a robust, maintainable application.