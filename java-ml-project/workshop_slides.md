# Java Machine Learning Workshop: Restaurant Review Classification
## 3-Hour Workshop on Sentiment Analysis with Apache Spark

---

## Slide 1: Welcome & Workshop Overview

### 🎯 Learning Objectives
- Build a complete machine learning pipeline in Java
- Implement sentiment analysis on restaurant reviews
- Use Apache Spark for distributed computing
- Apply text preprocessing and feature engineering
- Evaluate model performance with proper metrics

### 📋 Workshop Agenda (3 Hours)
1. **Problem Statement & Dataset** (30 min)
2. **Environment Setup** (30 min)
3. **Data Loading & Preprocessing** (45 min)
4. **Feature Engineering** (30 min)
5. **Model Training & Evaluation** (45 min)
6. **Hands-on Exercises** (30 min)

---

## Slide 2: Problem Statement

### 🍽️ Restaurant Review Classification Challenge

**The Business Problem:**
- Restaurants receive thousands of reviews daily
- Manual review analysis is time-consuming and subjective
- Need automated system to classify reviews as positive or negative
- Help restaurants understand customer sentiment at scale

**The Technical Challenge:**
- Process large volumes of text data efficiently
- Extract meaningful features from unstructured text
- Build a classifier that generalizes to new reviews
- Deploy at scale using distributed computing

**Real-World Impact:**
- Customer service improvement
- Menu and service optimization
- Competitive analysis
- Marketing strategy insights

---

## Slide 3: Understanding the Yelp Dataset

### 📊 Dataset Overview

**Source:** Yelp Open Dataset
- **Size:** 20,000 restaurant reviews
- **Classes:** 10,000 positive + 10,000 negative
- **Format:** CSV with text and sentiment columns
- **Language:** English reviews only

### 📋 Dataset Structure
```csv
text,sentiment
"Great food and excellent service!",positive
"Terrible experience, would not recommend",negative
"Amazing pizza, friendly staff",positive
```

### 🎯 Why This Dataset?
- **Real-world data:** Actual customer reviews
- **Balanced classes:** Equal positive/negative samples
- **Diverse vocabulary:** Various writing styles and topics
- **Appropriate size:** Large enough for learning, small enough for workshop

---

## Slide 4: Machine Learning Pipeline Overview

### 🔄 Complete ML Workflow

```
Raw Text → Preprocessing → Feature Extraction → Model Training → Evaluation
    ↓           ↓              ↓                ↓              ↓
Yelp Reviews → Clean Text → TF-IDF Vectors → Naive Bayes → Metrics
```

### 🛠️ Technology Stack
- **Java 21:** Programming language
- **Apache Spark 4.0.1:** Distributed computing framework
- **Spark MLlib:** Machine learning library
- **Multinomial Naive Bayes:** Classification algorithm

### 📈 Expected Performance
- **Accuracy:** ~77.6%
- **Training Time:** ~2-3 minutes
- **Prediction Time:** Real-time

---

## Slide 5: Workshop Environment Setup

### 🚀 Step 1: VS Code Setup

**Prerequisites:**
- VS Code with Java Extension Pack
- Java 21 installed
- Git for cloning repository

**Setup Instructions:**
1. Clone the repository
2. Open in VS Code
3. Install recommended extensions
4. Verify Java installation

### 📝 Placeholder for Detailed Instructions
*[Instructor will provide step-by-step VS Code setup guide here]*

**Key Extensions:**
- Extension Pack for Java
- Apache Spark Tools
- GitLens

---

## Slide 6: Setting Up Apache Spark

### ⚡ Step 2: Spark Installation

**Why Apache Spark?**
- Distributed computing for large datasets
- Built-in machine learning algorithms
- Optimized for big data processing
- Industry standard for ML pipelines

### 🔧 Setup Process
```bash
# Run the setup script
./setup_spark.sh
```

**What the script does:**
1. Downloads Apache Spark 4.0.1
2. Extracts all JAR files to `lib/` directory
3. Verifies installation
4. Cleans up temporary files

### ⏱️ Time: 5-10 minutes
*[Students will run this during the workshop]*

---

## Slide 7: Creating the Dataset

### 📊 Step 3: Dataset Generation

**Why create our own dataset?**
- Controlled size for workshop
- Balanced classes
- Clean, consistent format
- Educational purposes

### 🐍 Dataset Creation Script
```bash
# Run the dataset creation script
./create_dataset.sh
```

**What it generates:**
- 20,000 restaurant reviews
- 10,000 positive + 10,000 negative
- CSV format with text and sentiment columns
- File: `simple_yelp_reviews.csv`

### 📋 Dataset Statistics
- **File size:** ~2-3 MB
- **Average review length:** 50-100 words
- **Vocabulary size:** ~5,000 unique words

---

## Slide 8: Java Code Structure Overview

### 🏗️ Project Architecture

```
YelpSentimentAnalysisSpark.java
├── main()                    # Entry point
├── loadData()               # CSV loading
├── buildPipeline()          # ML pipeline setup
├── evaluateModel()          # Performance metrics
└── testSampleReviews()      # Sample predictions
```

### 📦 Key Dependencies
- **Spark Core:** Distributed computing
- **Spark SQL:** Data manipulation
- **Spark MLlib:** Machine learning algorithms
- **Java Collections:** Data structures

### 🎯 Design Principles
- **Modularity:** Each method has single responsibility
- **Error Handling:** Graceful failure management
- **Performance:** Optimized for large datasets

---

## Slide 9: Data Loading with CSV Library

### 📁 Step 4: Loading Dataset

**Challenge:** Java doesn't have built-in CSV support
**Solution:** Use Spark's built-in CSV reader

### 💻 Code Implementation
```java
private static Dataset<Row> loadData(SparkSession spark) {
    // Define schema for CSV
    StructType schema = new StructType(new StructField[]{
        DataTypes.createStructField("text", DataTypes.StringType, false),
        DataTypes.createStructField("sentiment", DataTypes.StringType, false)
    });
    
    // Read CSV file
    Dataset<Row> data = spark.read()
        .option("header", "true")
        .option("inferSchema", "false")
        .schema(schema)
        .csv(CSV_FILE);
    
    return data;
}
```

### 🔍 Key Concepts
- **Schema Definition:** Explicit data types
- **Header Handling:** Skip first row
- **Type Safety:** Prevent runtime errors

---

## Slide 10: Data Preprocessing Pipeline

### 🧹 Step 5: Text Cleaning

**Why Preprocessing?**
- Remove noise and irrelevant information
- Standardize text format
- Improve model performance
- Handle edge cases

### 🔄 Preprocessing Steps
1. **Tokenization:** Split text into words
2. **Stop Words Removal:** Remove common words
3. **Lowercasing:** Standardize case
4. **Punctuation Removal:** Clean special characters

### 💻 Spark ML Pipeline
```java
// Tokenizer - split text into words
Tokenizer tokenizer = new Tokenizer()
    .setInputCol("text")
    .setOutputCol("words");

// StopWordsRemover - remove common words
StopWordsRemover stopWordsRemover = new StopWordsRemover()
    .setInputCol("words")
    .setOutputCol("filtered_words");
```

---

## Slide 11: Feature Engineering with TF-IDF

### 🔢 Step 6: Converting Text to Numbers

**The Challenge:** Machine learning algorithms need numerical input
**The Solution:** TF-IDF (Term Frequency-Inverse Document Frequency)

### 📊 TF-IDF Explained
- **Term Frequency (TF):** How often a word appears in a document
- **Inverse Document Frequency (IDF):** How rare a word is across all documents
- **TF-IDF:** Combines both to create meaningful features

### 💻 Implementation
```java
// HashingTF - convert words to feature vectors
HashingTF hashingTF = new HashingTF()
    .setInputCol("filtered_words")
    .setOutputCol("rawFeatures")
    .setNumFeatures(10000); // Vocabulary size

// IDF - calculate inverse document frequency
IDF idf = new IDF()
    .setInputCol("rawFeatures")
    .setOutputCol("features");
```

---

## Slide 12: Understanding Naive Bayes Algorithm

### 🧠 Step 7: Classification Algorithm

**What is Naive Bayes?**
- Probabilistic classifier based on Bayes' theorem
- "Naive" assumption: features are independent
- Works well with text data
- Fast training and prediction

### 📈 Mathematical Foundation
```
P(class|features) = P(class) × P(feature1|class) × P(feature2|class) × ...
```

### 🎯 Why Naive Bayes for Text?
- Handles high-dimensional data well
- Works with sparse features (many zeros)
- Fast training on large datasets
- Good baseline for text classification

---

## Slide 13: Model Training Implementation

### 🏋️ Step 8: Training the Classifier

**Training Process:**
1. Split data into training and test sets
2. Build ML pipeline with preprocessing and classification
3. Fit the model on training data
4. Make predictions on test data

### 💻 Code Implementation
```java
// Multinomial Naive Bayes classifier
NaiveBayes naiveBayes = new NaiveBayes()
    .setFeaturesCol("features")
    .setLabelCol("label")
    .setModelType("multinomial")
    .setSmoothing(1.0); // Laplace smoothing

// Create pipeline
Pipeline pipeline = new Pipeline()
    .setStages(new PipelineStage[]{
        tokenizer,
        stopWordsRemover,
        hashingTF,
        idf,
        naiveBayes
    });

// Train model
PipelineModel model = pipeline.fit(trainData);
```

---

## Slide 14: Model Evaluation Metrics

### 📊 Step 9: Measuring Performance

**Why Evaluation?**
- Understand model performance
- Compare different approaches
- Identify areas for improvement
- Build confidence in predictions

### 📈 Key Metrics
- **Accuracy:** Overall correctness
- **Precision:** True positives / (True positives + False positives)
- **Recall:** True positives / (True positives + False negatives)
- **F1-Score:** Harmonic mean of precision and recall

### 💻 Implementation
```java
MulticlassClassificationEvaluator evaluator = 
    new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("accuracy");

double accuracy = evaluator.evaluate(predictions);
```

---

## Slide 15: Confusion Matrix Analysis

### 🔍 Step 10: Understanding Model Behavior

**What is a Confusion Matrix?**
- 2x2 table showing prediction vs actual results
- Helps identify model strengths and weaknesses
- Visual representation of classification performance

### 📊 Matrix Structure
```
                Predicted
Actual     Negative  Positive
Negative      TN       FP
Positive      FN       TP
```

**Where:**
- TN = True Negatives (correctly predicted negative)
- TP = True Positives (correctly predicted positive)
- FN = False Negatives (missed positive cases)
- FP = False Positives (incorrectly predicted positive)

---

## Slide 16: Sample Predictions Testing

### 🧪 Step 11: Testing on New Data

**Why Test Sample Reviews?**
- Validate model on unseen data
- Demonstrate real-world application
- Build confidence in the system
- Identify edge cases

### 💻 Implementation
```java
private static void testSampleReviews(PipelineModel model, SparkSession spark) {
    String[] sampleReviews = {
        "Great food, excellent service!",
        "Terrible food, bad service",
        "Amazing pizza, friendly staff",
        "This place is absolutely horrible",
        "Love this restaurant, will come back"
    };
    
    // Make predictions and display results
    for (String review : sampleReviews) {
        // ... prediction logic
        System.out.println("'" + review + "' -> " + sentiment);
    }
}
```

---

## Slide 17: Running the Complete Pipeline

### 🚀 Step 12: End-to-End Execution

**Complete Workflow:**
1. Load and preprocess data
2. Split into training/test sets
3. Build ML pipeline
4. Train the model
5. Evaluate performance
6. Test on sample reviews

### 💻 Execution Command
```bash
# Compile the Java code
javac -cp "lib/*" YelpSentimentAnalysisSpark.java

# Run the analysis (with suppressed logs)
./run_naive_bayes.sh
```

**Note:** The run script automatically suppresses Spark info logs for cleaner output.

### 📊 Expected Output
```
=== Yelp Sentiment Analysis with Apache Spark MLlib ===
Loading data...
Loaded 20000 samples

Class Distribution:
+-----+-----+
|label|count|
+-----+-----+
|    1|10000|
|    0|10000|
+-----+-----+

Model Performance:
Accuracy: 0.776
Precision: 0.777
Recall: 0.776
F1-Score: 0.776
```

---

## Slide 18: Hands-on Exercise 1: Data Exploration

### 🔍 Exercise: Understanding Your Data

**Task:** Modify the code to explore the dataset
**Time:** 10 minutes

**What to do:**
1. Print first 5 reviews from the dataset
2. Count average words per review
3. Find the longest and shortest reviews
4. Check for any missing values

**Code hints:**
```java
// Show sample data
data.show(5);

// Calculate statistics
data.select("text").show();
```

**Learning Objective:** Understand data characteristics before modeling

---

## Slide 19: Hands-on Exercise 2: Feature Engineering

### ⚙️ Exercise: Experimenting with Features

**Task:** Modify feature extraction parameters
**Time:** 15 minutes

**What to try:**
1. Change vocabulary size (numFeatures)
2. Experiment with different smoothing parameters
3. Try different TF-IDF settings
4. Compare performance

**Code hints:**
```java
// Modify vocabulary size
HashingTF hashingTF = new HashingTF()
    .setNumFeatures(5000); // Try 5000, 10000, 20000

// Adjust smoothing
NaiveBayes naiveBayes = new NaiveBayes()
    .setSmoothing(0.5); // Try 0.1, 1.0, 2.0
```

**Learning Objective:** Understand impact of hyperparameters

---

## Slide 20: Hands-on Exercise 3: Model Evaluation

### 📊 Exercise: Deep Dive into Performance

**Task:** Implement additional evaluation metrics
**Time:** 15 minutes

**What to add:**
1. Confusion matrix visualization
2. Per-class precision and recall
3. Classification report
4. Error analysis

**Code hints:**
```java
// Show confusion matrix
predictions.groupBy("label", "prediction").count().show();

// Calculate per-class metrics
// ... implementation details
```

**Learning Objective:** Understand model strengths and weaknesses

---

## Slide 21: Common Issues and Troubleshooting

### 🐛 Debugging Tips

**Common Problems:**
1. **Classpath Issues:** Missing JAR files
2. **Memory Errors:** OutOfMemoryError with large datasets
3. **Data Format:** Incorrect CSV structure
4. **Version Conflicts:** Java/Spark compatibility
5. **Verbose Logs:** Too many Spark info messages

**Solutions:**
```bash
# Check classpath
echo $CLASSPATH

# Increase memory
java -Xmx4g -cp ".:lib/*" YelpSentimentAnalysisSpark

# Verify data
head -5 simple_yelp_reviews.csv

# Suppress Spark logs
java -Dlog4j.configuration=file:log4j.properties -cp ".:lib/*" YelpSentimentAnalysisSpark
```

**Best Practices:**
- Always check data before processing
- Use try-catch blocks for error handling
- Monitor memory usage
- Test with small datasets first

---

## Slide 22: Performance Optimization

### ⚡ Making It Faster

**Optimization Strategies:**
1. **Caching:** Cache frequently used datasets
2. **Partitioning:** Optimize data distribution
3. **Memory Management:** Tune JVM settings
4. **Algorithm Selection:** Choose appropriate algorithms

**Code Examples:**
```java
// Cache dataset for multiple uses
data.cache();

// Optimize Spark configuration
SparkConf conf = new SparkConf()
    .set("spark.sql.adaptive.enabled", "true")
    .set("spark.sql.adaptive.coalescePartitions.enabled", "true");
```

**When to Optimize:**
- Large datasets (>1M records)
- Slow training times
- Memory constraints
- Production deployment

---

## Slide 23: Real-World Applications

### 🌍 Beyond Restaurant Reviews

**Other Use Cases:**
- **Social Media:** Twitter sentiment analysis
- **E-commerce:** Product review classification
- **Finance:** News sentiment for trading
- **Healthcare:** Patient feedback analysis
- **Customer Service:** Support ticket classification

**Scaling Considerations:**
- **Data Volume:** Millions of reviews
- **Real-time Processing:** Stream processing
- **Model Updates:** Continuous learning
- **Deployment:** Cloud vs on-premises

**Business Impact:**
- Automated customer insights
- Improved decision making
- Cost reduction
- Competitive advantage

---

## Slide 24: Advanced Topics

### 🚀 Next Steps

**Advanced Techniques:**
1. **Deep Learning:** Neural networks for text
2. **Ensemble Methods:** Combining multiple models
3. **Feature Engineering:** Word embeddings, n-grams
4. **Hyperparameter Tuning:** Grid search, random search

**Production Considerations:**
- **Model Serving:** REST APIs, microservices
- **Monitoring:** Model drift detection
- **A/B Testing:** Model comparison
- **CI/CD:** Automated model deployment

**Learning Resources:**
- Apache Spark documentation
- Machine learning courses
- Text mining tutorials
- Java ML libraries

---

## Slide 25: Workshop Summary

### 🎯 What We Accomplished

**Built a Complete ML Pipeline:**
✅ Data loading and preprocessing
✅ Feature engineering with TF-IDF
✅ Model training with Naive Bayes
✅ Performance evaluation
✅ Sample prediction testing

**Key Learning Outcomes:**
- Java programming for ML
- Apache Spark for distributed computing
- Text preprocessing techniques
- Model evaluation methodologies
- Real-world ML application

**Skills Gained:**
- End-to-end ML development
- Big data processing
- Performance optimization
- Error handling and debugging

---

## Slide 26: Next Steps and Resources

### 📚 Continue Learning

**Immediate Next Steps:**
1. Experiment with different algorithms
2. Try larger datasets
3. Implement cross-validation
4. Add more evaluation metrics

**Recommended Resources:**
- **Books:** "Hands-On Machine Learning" by Aurélien Géron
- **Courses:** Coursera ML Specialization
- **Documentation:** Apache Spark MLlib Guide
- **Practice:** Kaggle competitions

**Project Ideas:**
- Multi-class sentiment analysis
- Real-time sentiment monitoring
- Sentiment analysis API
- Mobile app integration

---

## Slide 27: Q&A Session

### ❓ Questions and Discussion

**Common Questions:**
- How to handle imbalanced datasets?
- What about other ML algorithms?
- How to improve accuracy?
- Scaling to production?

**Discussion Topics:**
- Challenges in text classification
- Business applications
- Technical limitations
- Future improvements

**Get Help:**
- Workshop materials available online
- Instructor contact information
- Community forums
- Office hours

---

## Slide 28: Thank You!

### 🙏 Workshop Conclusion

**Thank you for participating!**

**Key Takeaways:**
- Machine learning is accessible in Java
- Apache Spark enables scalable ML
- Text classification has real-world impact
- Practice makes perfect

**Stay Connected:**
- Follow up on project improvements
- Share your implementations
- Join the ML community
- Continue learning and experimenting

**Happy Coding! 🚀**

---

## Appendix A: Complete Code Listing

### 📄 Full Implementation

*[Complete Java code will be provided as reference]*

---

## Appendix B: Setup Instructions

### 🔧 Detailed Setup Guide

*[Step-by-step environment setup instructions]*

---

## Appendix C: Troubleshooting Guide

### 🐛 Common Issues and Solutions

*[Comprehensive troubleshooting reference]*

---

## Appendix D: Additional Resources

### 📚 Learning Materials

*[Curated list of resources for continued learning]*