# Apache Spark Java Sentiment Analysis: Complete Analysis

## Overview

This document provides a comprehensive analysis of the `YelpSentimentAnalysisSpark.java` file, which implements a sentiment analysis system using Apache Spark MLlib for text classification. The system uses Multinomial Naive Bayes to classify Yelp restaurant reviews as either positive or negative sentiment.

## Architecture & Components

### 1. **Spark Configuration & Initialization**

```java
SparkConf conf = new SparkConf()
    .setAppName("YelpSentimentAnalysis")
    .setMaster("local[*]")
    .set("spark.sql.adaptive.enabled", "false")
    .set("spark.sql.adaptive.coalescePartitions.enabled", "false")
    .set("spark.ui.showConsoleProgress", "false")
    .set("spark.sql.adaptive.skewJoin.enabled", "false");
```

**What's happening:**
- **Local Execution**: Uses `local[*]` to run on all available CPU cores locally
- **Adaptive Query Execution (AQE) Disabled**: Disables Spark's adaptive query optimization for predictable behavior
- **Log Suppression**: Reduces verbose Spark logging for cleaner output
- **Resource Management**: Optimized for single-machine execution

### 2. **Data Loading & Preprocessing**

#### Data Schema Definition
```java
StructType schema = new StructType(new StructField[]{
    DataTypes.createStructField("text", DataTypes.StringType, false),
    DataTypes.createStructField("sentiment", DataTypes.StringType, false)
});
```

#### Label Conversion
```java
data = data.withColumn("label", 
    when(col("sentiment").equalTo("positive"), 1)
    .otherwise(0)
).select("text", "label");
```

**What's happening:**
- **CSV Loading**: Reads from `simple_yelp_reviews.csv` with predefined schema
- **Label Encoding**: Converts string sentiment ("positive"/"negative") to binary labels (1/0)
- **Data Selection**: Keeps only text and label columns for ML pipeline

## Feature Engineering Pipeline

The feature engineering process transforms raw text into numerical features suitable for machine learning:

### 1. **Text Tokenization**
```java
Tokenizer tokenizer = new Tokenizer()
    .setInputCol("text")
    .setOutputCol("words");
```

**Purpose**: Splits text into individual words/tokens
- Input: `"Great food, excellent service!"`
- Output: `["Great", "food", "excellent", "service"]`

### 2. **Stop Words Removal**
```java
StopWordsRemover stopWordsRemover = new StopWordsRemover()
    .setInputCol("words")
    .setOutputCol("filtered_words");
```

**Purpose**: Removes common words that don't carry sentiment information
- Removes: "the", "a", "an", "and", "or", "but", etc.
- Input: `["Great", "food", "and", "excellent", "service"]`
- Output: `["Great", "food", "excellent", "service"]`

### 3. **Hashing Term Frequency (HashingTF)**
```java
HashingTF hashingTF = new HashingTF()
    .setInputCol("filtered_words")
    .setOutputCol("rawFeatures")
    .setNumFeatures(10000);
```

**Purpose**: Converts words to numerical features using hashing
- **Hash Function**: Maps words to feature indices (0-9999)
- **Term Frequency**: Counts how many times each word appears
- **Vocabulary Size**: 10,000 features (configurable)
- **Advantage**: Handles unknown words and large vocabularies efficiently

**Example:**
- Input: `["Great", "food", "excellent", "service"]`
- Process: Hash each word → count frequencies
- Output: Sparse vector with non-zero counts at hashed positions

### 4. **Inverse Document Frequency (IDF)**
```java
IDF idf = new IDF()
    .setInputCol("rawFeatures")
    .setOutputCol("features");
```

**Purpose**: Reduces weight of common words across the entire dataset
- **Formula**: `IDF(t) = log(N / df(t))` where N = total documents, df(t) = documents containing term t
- **Effect**: Rare words get higher weights, common words get lower weights
- **Result**: TF-IDF features that better represent document uniqueness

## Model Training

### Multinomial Naive Bayes Classifier
```java
NaiveBayes naiveBayes = new NaiveBayes()
    .setFeaturesCol("features")
    .setLabelCol("label")
    .setModelType("multinomial")
    .setSmoothing(1.0);
```

**Key Parameters:**
- **Model Type**: `multinomial` (suitable for text classification with word counts)
- **Smoothing**: 1.0 (Laplace smoothing to handle unseen words)
- **Algorithm**: Probabilistic classifier based on Bayes' theorem

### Training Process
1. **Data Split**: 80% training, 20% testing with random seed 42
2. **Pipeline Fitting**: All preprocessing steps + model training in one operation
3. **Feature Learning**: Model learns word probabilities for each class
4. **Parameter Estimation**: Calculates prior probabilities and likelihoods

## Model Evaluation

### Metrics Calculated
```java
double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
double precision = evaluator.setMetricName("weightedPrecision").evaluate(predictions);
double recall = evaluator.setMetricName("weightedRecall").evaluate(predictions);
double f1 = evaluator.setMetricName("f1").evaluate(predictions);
```

### Performance Interpretation
- **Accuracy ≥ 80%**: Excellent performance
- **Accuracy ≥ 70%**: Good performance  
- **Accuracy ≥ 60%**: Fair performance
- **Accuracy < 60%**: Poor performance

### Confusion Matrix Analysis
The system calculates and displays:
- **True Positives**: Correctly predicted positive reviews
- **True Negatives**: Correctly predicted negative reviews
- **False Positives**: Incorrectly predicted as positive (Type I error)
- **False Negatives**: Incorrectly predicted as negative (Type II error)
- **Sensitivity**: True Positive Rate (Recall for positive class)
- **Specificity**: True Negative Rate (Recall for negative class)

## Sample Prediction Testing

The system tests the trained model on 19 hand-crafted sample reviews:

### Sample Reviews Include:
- **Positive Examples**: "Great food, excellent service!", "Amazing pizza, friendly staff"
- **Negative Examples**: "Terrible food, bad service", "This place is absolutely horrible"

### Prediction Process:
1. **Single Review Processing**: Each review goes through the same pipeline
2. **Feature Extraction**: Same tokenization, stop word removal, TF-IDF process
3. **Classification**: Model predicts probability and final label
4. **Validation**: Compares predicted vs expected sentiment

## Data Flow Summary

```
Raw Text → Tokenization → Stop Word Removal → HashingTF → IDF → Naive Bayes → Prediction
    ↓           ↓              ↓              ↓         ↓         ↓
"Great food" → ["Great","food"] → ["Great","food"] → [1,0,1,0...] → [0.1,0.0,0.1,0.0...] → Positive
```

## Key Technical Decisions

### 1. **Multinomial vs Gaussian Naive Bayes**
- **Choice**: Multinomial Naive Bayes
- **Reason**: Better suited for text classification with discrete word counts
- **Alternative**: Gaussian assumes continuous features (not suitable for text)

### 2. **HashingTF vs CountVectorizer**
- **Choice**: HashingTF
- **Advantages**: 
  - Handles large vocabularies efficiently
  - No need to build vocabulary beforehand
  - Memory efficient
- **Trade-off**: Potential hash collisions (rare with 10K features)

### 3. **Feature Engineering Pipeline**
- **Sequential Processing**: Each step builds on the previous
- **Reproducible**: Same pipeline for training and prediction
- **Scalable**: Can handle large datasets efficiently

## Performance Characteristics

### Strengths:
1. **Scalability**: Spark's distributed computing capabilities
2. **Efficiency**: HashingTF handles large vocabularies
3. **Robustness**: Laplace smoothing handles unseen words
4. **Interpretability**: Naive Bayes provides probability scores

### Limitations:
1. **Feature Independence**: Assumes words are independent (naive assumption)
2. **Hash Collisions**: Rare but possible with HashingTF
3. **Context Ignorance**: Doesn't consider word order or context
4. **Binary Classification**: Only handles positive/negative (no neutral)

## Usage Instructions

### Prerequisites:
- Java 8+
- Apache Spark 3.x
- Maven (for dependency management)

### Running the Code:
```bash
# Compile
javac -cp "lib/*" YelpSentimentAnalysisSpark.java

# Execute
java -cp ".:lib/*" YelpSentimentAnalysisSpark
```

### Expected Output:
- Data loading statistics
- Class distribution
- Training progress
- Evaluation metrics
- Sample predictions with accuracy

## Conclusion

This Spark-based sentiment analysis system demonstrates a complete machine learning pipeline for text classification. It effectively combines Apache Spark's distributed computing capabilities with proven text processing techniques to create a scalable and efficient sentiment analysis solution. The use of Multinomial Naive Bayes with TF-IDF features provides a good balance between performance and interpretability for binary sentiment classification tasks.