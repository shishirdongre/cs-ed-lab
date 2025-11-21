---
theme: seriph
background: https://cover.sli.dev
title: "Text Classification with Java and Apache Spark"
info: |
    ## Text Classification with Java and Apache Spark
    A comprehensive walkthrough of Apache Spark MLlib implementation for text classification.
class: text-center text-sm
drawings:
    persist: false
transition: slide-left
mdc: true
duration: 45min
---

# Text Classification with Java and Apache Spark

A comprehensive walkthrough of Apache Spark MLlib implementation for text classification

<div @click="$slidev.nav.next" class="mt-12 py-1" hover:bg="white op-10">
  Press Space for next page <carbon:arrow-right />
</div>

<div class="abs-br m-6 text-xl">
  <button @click="$slidev.nav.openInEditor()" title="Open in Editor" class="slidev-icon-btn">
    <carbon:edit />
  </button>
  <a href="https://github.com/slidevjs/slidev" target="_blank" class="slidev-icon-btn">
    <carbon:logo-github />
  </a>
</div>

---
transition: fade-out
---

# Introduction

Understanding Spark ML Pipeline Components

This presentation breaks down the **YelpSentimentAnalysisSpark** Java implementation, explaining each method and component of the machine learning pipeline.

We'll explore how Apache Spark MLlib processes text data through **transformers** and **estimators** to build a sentiment classification model using **Linear SVC (Support Vector Classifier)**.

The pipeline includes:
- **Data Loading**: Reading CSV files with Spark SQL
- **Text Preprocessing**: Tokenization, stop word removal, feature extraction
- **Model Training**: Linear SVC classifier with TF-IDF features
- **Evaluation**: Metrics, confusion matrix, and performance analysis

---
layout: center
class: text-center
---

# Architecture

<img src="/architecture.jpg" alt="Architecture Diagram" class="w-full max-w-6xl mx-auto mt-8" />

---
layout: default
---

# Project Overview

The **Yelp Sentiment Analysis** project uses Apache Spark MLlib to classify restaurant reviews as **positive** or **negative**.

**Key Components:**
- **Tokenizer**: Splits text into individual words
- **StopWordsRemover**: Removes common words (the, a, an, etc.)
- **HashingTF**: Converts words to numerical features
- **IDF**: Applies inverse document frequency weighting
- **LinearSVC**: Trains a support vector classifier

**Data Flow:**
```
CSV File → Spark DataFrame → Preprocessing → Features → Model → Predictions
```

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Main Method - Entry Point

The `main` method orchestrates the entire machine learning pipeline. It follows a **sequential workflow**: initialize Spark, load data, split into train/test sets, train the model, make predictions, evaluate performance, and test on sample reviews.

**Key Steps:**
1. Initialize Spark session with optimized configuration
2. Load and prepare data from CSV
3. Split data into training (80%) and test (20%) sets
4. Train the model with all preprocessing stages
5. Make predictions on test data
6. Evaluate model performance
7. Test on sample reviews

**Error Handling:** Wrapped in try-catch to ensure Spark session is properly closed even if errors occur.

::right::

```java
public static void main(String[] args) {
    SparkSession spark = initializeSpark();
    
    try {
        Dataset<Row> data = loadAndPrepareData(spark);
        Dataset<Row>[] splits = splitData(data);
        Dataset<Row> trainData = splits[0];
        Dataset<Row> testData = splits[1];
        
        ModelComponents model = trainModel(trainData);
        Dataset<Row> predictions = makePredictions(testData, model);
        evaluateModel(predictions);
        testSampleReviews(model, spark);
        
    } catch (Exception e) {
        System.err.println("❌ ERROR: " + e.getMessage());
        e.printStackTrace();
    } finally {
        spark.stop();
    }
}
```

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Initialize Spark - Configuration

The `initializeSpark()` method sets up the Spark session with optimized settings for local development. It configures Spark to run on all available CPU cores (`local[*]`) and disables adaptive query execution for more predictable behavior.

**Configuration Choices:**
- **`setMaster("local[*]")`**: Uses all CPU cores on the local machine
- **`setAppName(...)`**: Identifies the application in Spark UI
- **Adaptive execution disabled**: Ensures consistent partitioning behavior
- **Log level set to WARN**: Reduces console output noise

::right::

```java
private static SparkSession initializeSpark() {
    SparkConf conf = new SparkConf()
        .setAppName("YelpSentimentAnalysis")
        .setMaster("local[*]")
        .set("spark.sql.adaptive.enabled", "false")
        .set("spark.sql.adaptive.coalescePartitions.enabled", "false")
        .set("spark.ui.showConsoleProgress", "false")
        .set("spark.sql.adaptive.skewJoin.enabled", "false");
    SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
    spark.sparkContext().setLogLevel("WARN");
    return spark;
}
```

**Why these settings?**
- Local mode is ideal for development and small datasets
- Disabling adaptive features makes the pipeline behavior more predictable
- Reduced logging keeps output focused on results

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Load Data - CSV Reading

The `loadData()` method reads the CSV file using Spark SQL's DataFrame API. It defines an explicit schema to ensure type safety and converts the string sentiment labels ("positive"/"negative") to integer labels (1/0) required by MLlib classifiers.

**Schema Definition:**
- **`text`**: StringType - the review text
- **`sentiment`**: StringType - original label ("positive"/"negative")

**Why explicit schema?**
- Prevents Spark from inferring types (can be slow)
- Ensures consistent data types across runs
- Catches schema mismatches early

::right::

```java
private static Dataset<Row> loadData(SparkSession spark) {
    StructType schema = new StructType(new StructField[]{
        DataTypes.createStructField("text", DataTypes.StringType, false),
        DataTypes.createStructField("sentiment", DataTypes.StringType, false)
    });
    Dataset<Row> data = spark.read()
        .option("header", "true")
        .option("inferSchema", "false")
        .schema(schema)
        .csv(CSV_FILE);
    data = data.withColumn("label", 
        when(col("sentiment").equalTo("positive"), 1).otherwise(0))
        .select("text", "label");
    return data;
}
```

**Label Conversion:**
- Uses Spark SQL's `when().otherwise()` to map:
  - "positive" → 1
  - "negative" → 0

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Load and Prepare Data - Orchestration

The `loadAndPrepareData()` method orchestrates data loading and provides initial insights. It calls `loadData()` to read the CSV, counts total samples, and displays the class distribution to help understand data balance.

**What it does:**
1. Calls `loadData()` to read CSV into DataFrame
2. Counts total number of samples
3. Shows class distribution (how many positive vs negative reviews)
4. Returns the prepared DataFrame

**Why show class distribution?**
- Helps identify class imbalance issues
- Important for understanding model performance
- Guides decisions about sampling strategies

::right::

```java
private static Dataset<Row> loadAndPrepareData(SparkSession spark) {
    Dataset<Row> data = loadData(spark);
    long totalSamples = data.count();
    System.out.println("✅ Loaded " + totalSamples + " samples");
    data.groupBy("label").count().show();
    return data;
}
```

**Output Example:**
```
✅ Loaded 1000 samples
📈 CLASS DISTRIBUTION:
+------+-----+
| label|count|
+------+-----+
|     0|  500|
|     1|  500|
+------+-----+
```

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Split Data - Train/Test Split

The `splitData()` method divides the dataset into training and test sets using Spark's `randomSplit()` method. It uses a fixed random seed (42) to ensure reproducible splits across runs.

**Split Strategy:**
- **Training set**: 80% of data (1.0 - TEST_SIZE)
- **Test set**: 20% of data (TEST_SIZE)
- **Random seed**: 42 (ensures reproducibility)

**Why split the data?**
- **Training set**: Used to learn model parameters
- **Test set**: Used to evaluate model performance on unseen data
- Prevents overfitting by testing on data the model hasn't seen

::right::

```java
private static Dataset<Row>[] splitData(Dataset<Row> data) {
    Dataset<Row>[] splits = data.randomSplit(
        new double[]{1.0 - TEST_SIZE, TEST_SIZE}, RANDOM_SEED);
    Dataset<Row> trainData = splits[0];
    Dataset<Row> testData = splits[1];
    long totalSamples = data.count();
    long trainCount = trainData.count();
    long testCount = testData.count();
    System.out.printf("Training: %d (%.1f%%), Test: %d (%.1f%%)\n", 
        trainCount, (double)trainCount/totalSamples*100,
        testCount, (double)testCount/totalSamples*100);
    return splits;
}
```

**Output:**
```
✂️  DATA SPLITTING
Training samples: 800 (80.0%)
Test samples:    200 (20.0%)
```

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Train Model - Stage 1: Tokenizer

The `trainModel()` method builds the complete ML pipeline. The first stage is the **Tokenizer**, which splits text into individual words (tokens).

**Tokenizer Configuration:**
- **Input column**: `"text"` (the review text)
- **Output column**: `"words"` (array of words)

**How it works:**
- Splits text on whitespace and punctuation
- Converts to lowercase
- Returns an array of strings

**Why tokenize?**
- Breaks text into analyzable units
- First step in converting text to numerical features

::right::

```java
Tokenizer tokenizer = new Tokenizer()
    .setInputCol("text")
    .setOutputCol("words");
Dataset<Row> trainTok = tokenizer.transform(trainData);
```

**Example:**
```
Input:  "Great food, excellent service!"
Output: ["great", "food", "excellent", "service"]
```

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Train Model - Stage 2: StopWordsRemover

The second preprocessing stage is **StopWordsRemover**, which filters out common words that don't carry much meaning (e.g., "the", "a", "an", "is", "are").

**StopWordsRemover Configuration:**
- **Input column**: `"words"` (from tokenizer)
- **Output column**: `"filtered_words"` (words without stop words)

**Why remove stop words?**
- Reduces feature space (fewer words to process)
- Focuses on meaningful words
- Improves model efficiency and sometimes accuracy

::right::

```java
StopWordsRemover stopWordsRemover = new StopWordsRemover()
    .setInputCol("words")
    .setOutputCol("filtered_words");
Dataset<Row> trainFilt = stopWordsRemover.transform(trainTok);
```

**Example:**
```
Input:  ["great", "food", "the", "service", "is", "excellent"]
Output: ["great", "food", "service", "excellent"]
        (removed: "the", "is")
```

**Default stop words:** Includes common English words like "the", "a", "an", "and", "or", "but", etc.

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Train Model - Stage 3: HashingTF

**HashingTF (Term Frequency)** converts words into numerical features using a hash function. It counts how many times each word appears in a document and maps words to feature indices using hashing.

**HashingTF Configuration:**
- **Input column**: `"filtered_words"` (array of words)
- **Output column**: `"rawFeatures"` (sparse vector)
- **Number of features**: 10000 (hash space size)

**How it works:**
1. Each word is hashed to a number between 0 and 9999
2. Counts occurrences of each word in the document
3. Creates a sparse vector with word frequencies

**Why hashing?**
- Avoids maintaining a vocabulary dictionary
- Fast and memory-efficient
- Handles new words automatically

::right::

```java
HashingTF hashingTF = new HashingTF()
    .setInputCol("filtered_words")
    .setOutputCol("rawFeatures")
    .setNumFeatures(10000);
Dataset<Row> trainRaw = hashingTF.transform(trainFilt);
```

**Example:**
```
Input:  ["great", "food", "great", "service"]
Output: SparseVector(10000, {hash("great"): 2, 
                              hash("food"): 1, 
                              hash("service"): 1})
```

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Train Model - Stage 4: IDF (Estimator)

**IDF (Inverse Document Frequency)** is an **Estimator** that must be **fitted** on training data. It weights features by how rare/common they are across all documents. Common words get lower weights, rare words get higher weights.

**IDF Configuration:**
- **Input column**: `"rawFeatures"` (from HashingTF)
- **Output column**: `"features"` (TF-IDF weighted features)

**How it works:**
1. **Fit phase**: Analyzes all training documents to compute IDF scores
2. **Transform phase**: Applies IDF weights to term frequencies

**Why IDF?**
- Reduces importance of common words (e.g., "the", "food")
- Increases importance of distinctive words
- Improves classification accuracy

**Note:** IDF is an **Estimator** - it must be fitted on training data only!

::right::

```java
IDF idf = new IDF()
    .setInputCol("rawFeatures")
    .setOutputCol("features");
IDFModel idfModel = idf.fit(trainRaw);  // Fit on training only
Dataset<Row> trainFeats = idfModel.transform(trainRaw).cache();
```

**IDF Formula:**
```
IDF(word) = log(total_documents / documents_containing_word)
TF-IDF = TF(word) × IDF(word)
```

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Train Model - Stage 5: LinearSVC (Estimator)

**LinearSVC (Linear Support Vector Classifier)** is the final **Estimator** that learns to classify reviews as positive or negative. It uses a linear decision boundary to separate classes.

**LinearSVC Configuration:**
- **Features column**: `"features"` (TF-IDF vectors)
- **Label column**: `"label"` (0=negative, 1=positive)
- **Max iterations**: 50 (training iterations)
- **Regularization parameter**: 0.1 (prevents overfitting)

**How it works:**
1. **Fit phase**: Learns optimal weights for features
2. Finds a hyperplane that best separates positive and negative reviews
3. Uses hinge loss function for optimization

**Why LinearSVC?**
- Fast training and prediction
- Works well with high-dimensional sparse features (like text)
- Good baseline for text classification

::right::

```java
LinearSVC lsvc = new LinearSVC()
    .setFeaturesCol("features")
    .setLabelCol("label")
    .setMaxIter(50)
    .setRegParam(0.1);
LinearSVCModel svcModel = lsvc.fit(trainFeats);  // Fit on training
return new ModelComponents(
    tokenizer, stopWordsRemover, hashingTF, idfModel, svcModel);
```

**Training Process:**
- Iteratively adjusts feature weights
- Minimizes classification error
- Stops after max iterations or convergence

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# ModelComponents - Container Class

The `ModelComponents` inner class holds all the trained/prepared model stages. This allows passing the entire pipeline between methods without losing any components.

**Why use a container class?**
- **Organized**: Groups related model components together
- **Convenient**: Single object to pass around
- **Type-safe**: Ensures all components are present

**Usage:**
- Passed from `trainModel()` to `makePredictions()`
- Used in `testSampleReviews()` for inference
- All components needed for the full prediction pipeline

::right::

```java
private static class ModelComponents {
    public final Tokenizer tokenizer;
    public final StopWordsRemover stopWordsRemover;
    public final HashingTF hashingTF;
    public final IDFModel idfModel;
    public final LinearSVCModel svcModel;
    
    public ModelComponents(Tokenizer t, StopWordsRemover s, 
            HashingTF h, IDFModel i, LinearSVCModel l) {
        this.tokenizer = t;
        this.stopWordsRemover = s;
        this.hashingTF = h;
        this.idfModel = i;
        this.svcModel = l;
    }
}
```

**Components stored:**
1. **Tokenizer**: Already configured (no training needed)
2. **StopWordsRemover**: Already configured (no training needed)
3. **HashingTF**: Already configured (no training needed)
4. **IDFModel**: Fitted on training data (contains IDF weights)
5. **LinearSVCModel**: Trained classifier (contains learned weights)

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Make Predictions - Inference Pipeline

The `makePredictions()` method applies the trained model to test data. It follows the same preprocessing pipeline used during training, but uses the **fitted** models (IDFModel, LinearSVCModel) instead of fitting new ones.

**Key Principle:**
- **Training**: Fit estimators on training data
- **Prediction**: Transform using fitted models (no fitting!)

**Why same pipeline?**
- Ensures test data is processed identically to training data
- Maintains feature consistency
- Critical for accurate predictions

**Output:**
- Adds `"prediction"` column with predicted labels (0 or 1)

::right::

```java
private static Dataset<Row> makePredictions(
    Dataset<Row> testData, ModelComponents models) {
    Dataset<Row> testTok = models.tokenizer.transform(testData);
    Dataset<Row> testFilt = models.stopWordsRemover.transform(testTok);
    Dataset<Row> testRaw = models.hashingTF.transform(testFilt);
    Dataset<Row> testFeats = models.idfModel.transform(testRaw);
    Dataset<Row> predictions = models.svcModel.transform(testFeats);
    return predictions;
}
```

**Pipeline Steps:**
1. Tokenize test text
2. Remove stop words
3. Convert to TF features (HashingTF)
4. Apply IDF weights (using fitted IDFModel)
5. Make predictions (using fitted LinearSVCModel)

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Evaluate Model - Metrics Overview

The `evaluateModel()` method computes comprehensive performance metrics using Spark's `MulticlassClassificationEvaluator`. It calculates accuracy, precision, recall, and F1-score.

**Metrics Explained:**
- **Accuracy**: Overall correctness (correct predictions / total)
- **Precision**: Of predicted positives, how many were actually positive?
- **Recall**: Of actual positives, how many were correctly predicted?
- **F1-Score**: Harmonic mean of precision and recall

**Why multiple metrics?**
- Accuracy alone can be misleading with imbalanced data
- Precision/Recall trade-off shows model strengths/weaknesses
- F1-score balances precision and recall

::right::

```java
private static void evaluateModel(Dataset<Row> predictions) {
    MulticlassClassificationEvaluator evaluator = 
        new MulticlassClassificationEvaluator()
            .setLabelCol("label")
            .setPredictionCol("prediction");
    double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
    double precision = evaluator.setMetricName("weightedPrecision").evaluate(predictions);
    double recall = evaluator.setMetricName("weightedRecall").evaluate(predictions);
    double f1 = evaluator.setMetricName("f1").evaluate(predictions);
    // Display metrics table...
}
```

**Performance Interpretation:**
- **≥ 80%**: Excellent
- **≥ 70%**: Good
- **≥ 60%**: Fair
- **< 60%**: Poor

**Output Format:**
- Clean table with metrics and percentages
- Performance interpretation message
- Detailed confusion matrix

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Evaluate Model - Confusion Matrix

The confusion matrix shows detailed classification results in a 2×2 grid. It reveals how many predictions were correct/incorrect for each class.

**Confusion Matrix Structure:**
```
                Predicted
              Negative  Positive
Actual Negative   TN      FP
       Positive   FN      TP
```

**Terminology:**
- **TP (True Positives)**: Correctly predicted positive
- **TN (True Negatives)**: Correctly predicted negative
- **FP (False Positives)**: Incorrectly predicted positive (Type I error)
- **FN (False Negatives)**: Incorrectly predicted negative (Type II error)


::right::

**Why it matters:**
- Shows where model makes mistakes
- Helps identify class-specific issues
- Guides model improvement strategies


```java
long[][] confusionMatrix = getConfusionMatrix(predictions);
// Display 2x2 matrix: TN, FP, FN, TP
long tn = confusionMatrix[0][0];
long fp = confusionMatrix[0][1];
long fn = confusionMatrix[1][0];
long tp = confusionMatrix[1][1];
double sensitivity = (double) tp / (tp + fn);
double specificity = (double) tn / (tn + fp);
```

**Derived Metrics:**
- **Sensitivity (Recall)**: TP / (TP + FN) - How well we catch positives
- **Specificity**: TN / (TN + FP) - How well we catch negatives

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Get Confusion Matrix - Implementation

The `getConfusionMatrix()` method manually computes the confusion matrix by iterating through predictions and counting matches/mismatches between actual and predicted labels.

**How it works:**
1. Collects all (label, prediction) pairs from DataFrame
2. Iterates through results
3. Increments appropriate cell in 2×2 matrix
4. Handles type conversion (Integer vs Double from Spark)

**Why manual calculation?**
- More control over output format
- Can handle type variations
- Educational to see the computation

::right::

```java
private static long[][] getConfusionMatrix(Dataset<Row> predictions) {
    long[][] matrix = new long[2][2];
    List<Row> results = predictions.select("label", "prediction").collectAsList();
    for (Row row : results) {
        int actual = row.get(0) instanceof Integer ? 
            row.getInt(0) : ((Double) row.get(0)).intValue();
        int predicted = row.get(1) instanceof Integer ? 
            row.getInt(1) : ((Double) row.get(1)).intValue();
        matrix[actual][predicted]++;
    }
    return matrix;
}
```

**Matrix Structure:**
```
matrix[actual][predicted]++
```

**Output:**
- Returns `long[2][2]` matrix
- `[0][0]` = True Negatives
- `[0][1]` = False Positives
- `[1][0]` = False Negatives
- `[1][1]` = True Positives

**Type Handling:**
- Spark may return Integer or Double types
- Method handles both to ensure compatibility
- Converts to int for indexing

---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Test Sample Reviews - Inference on New Data

The `testSampleReviews()` method demonstrates how to use the trained model for inference on new, unseen reviews. It processes sample reviews through the complete pipeline and compares predictions to expected labels.

**Key Points:**
- Shows how to use the model in production
- Demonstrates end-to-end inference
- Provides visual feedback on model performance
- Calculates sample accuracy

**Why test on samples?**
- Validates model works on new data
- Provides intuitive understanding of model behavior
- Helps identify edge cases

::right::

```java
private static void testSampleReviews(ModelComponents models, SparkSession spark) {
    String[][] sampleReviews = {
        {"Great food, excellent service!", "positive"},
        {"Terrible food, bad service", "negative"}
    };
    int correct = 0;
    for (int i = 0; i < sampleReviews.length; i++) {
        Dataset<Row> reviewData = spark.createDataFrame(
            Arrays.asList(RowFactory.create(sampleReviews[i][0], 0)),
            new StructType(new StructField[]{
                DataTypes.createStructField("text", DataTypes.StringType, false),
                DataTypes.createStructField("label", DataTypes.IntegerType, false)
            }));
        Dataset<Row> tok = models.tokenizer.transform(reviewData);
        Dataset<Row> filt = models.stopWordsRemover.transform(tok);
        Dataset<Row> raw = models.hashingTF.transform(filt);
        Dataset<Row> feats = models.idfModel.transform(raw);
        Dataset<Row> pred = models.svcModel.transform(feats);
        // Compare prediction with expected...
    }
}
```


---
layout: two-cols
layoutClass: gap-4 text-xs
---

# Spark ML Concepts: Transformers vs Estimators

Understanding the difference between **Transformers** and **Estimators** is crucial for building Spark ML pipelines.

**Transformers:**
- **What**: Already fitted/prepared objects
- **Method**: `transform()` - applies transformation
- **Examples**: Tokenizer, StopWordsRemover, HashingTF, IDFModel, LinearSVCModel
- **Usage**: Same on training and test data

**Estimators:**
- **What**: Need to be fitted on data first
- **Methods**: 
  - `fit()` - learns from data, returns Transformer
  - `transform()` - applies learned transformation
- **Examples**: IDF (becomes IDFModel), LinearSVC (becomes LinearSVCModel)
- **Usage**: Fit on training data only, then transform test data

::right::

**Key Principle:**
- **Fit estimators on training data only**
- **Use fitted models (transformers) on both training and test data**
- This prevents data leakage and ensures proper evaluation

```java
// TRANSFORMERS (no fitting)
Tokenizer tokenizer = new Tokenizer()
    .setInputCol("text").setOutputCol("words");
Dataset<Row> tokens = tokenizer.transform(data);

// ESTIMATORS (must fit first)
IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
IDFModel idfModel = idf.fit(trainData);  // Fit on training
Dataset<Row> trainFeats = idfModel.transform(trainData);
Dataset<Row> testFeats = idfModel.transform(testData);  // Use fitted model
// ESTIMATOR → fit() → TRANSFORMER
```

**In our code:**
- IDF: `IDFModel idfModel = idf.fit(trainRaw)` ← Fit once
- LinearSVC: `LinearSVCModel svcModel = lsvc.fit(trainFeats)` ← Fit once
- Then use `idfModel.transform()` and `svcModel.transform()` for all data

---
layout: default
class: text-[10px]
---

# Pipeline Execution Flow - Training Phase

<img src="/ml_java_train_and_test.jpg" alt="Pipeline Execution Flow" class="w-full max-w-xl mx-auto mb-4" />

<div class="grid grid-cols-2 gap-4">

<div>

**Training Phase:**
1. Load CSV → DataFrame with text and labels
2. Split into train/test sets
3. **Train pipeline:**
   - Tokenize → words array
   - Remove stop words → filtered words
   - HashingTF → raw features (term frequencies)
   - **IDF.fit()** → learns IDF weights from training data
   - **IDF.transform()** → applies IDF to get TF-IDF features
   - **LinearSVC.fit()** → learns classifier weights
4. Save fitted models

</div>

<div>

**Key Points:**
- Fit estimators (IDF, LinearSVC) on training data only
- Preprocessing steps: Tokenize → StopWords → HashingTF
- Feature extraction: TF-IDF weighting
- Model learns optimal weights for classification

Understanding the complete data flow through the pipeline helps visualize how text becomes predictions.

</div>

</div>

---
layout: default
class: text-[10px]
---

# Pipeline Execution Flow - Prediction Phase

<img src="/ml_java_train_and_test.jpg" alt="Pipeline Execution Flow" class="w-full max-w-xl mx-auto mb-4" />

<div class="grid grid-cols-2 gap-4">

<div>

**Prediction Phase:**
1. Load new text data
2. **Apply same pipeline:**
   - Tokenize → words array
   - Remove stop words → filtered words
   - HashingTF → raw features
   - **IDFModel.transform()** → applies learned IDF weights
   - **LinearSVCModel.transform()** → makes predictions
3. Output predictions

</div>

<div>

**Critical Point:**
- Same preprocessing steps for training and prediction
- Use fitted models (not refit) for prediction
- Ensures consistency and prevents data leakage
- Transform using already-fitted IDFModel and LinearSVCModel

</div>

</div>

---
layout: center
class: text-center
---

# Thank You!

Questions & Discussion
