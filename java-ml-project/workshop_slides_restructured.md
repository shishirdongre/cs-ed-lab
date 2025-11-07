# Java Machine Learning Workshop: Restaurant Review Classification
## 3-Hour Workshop on Sentiment Analysis with Apache Spark

---

## Workshop Overview

### 🎯 Learning Objectives
- Build a complete machine learning pipeline in Java
- Implement sentiment analysis on restaurant reviews using Apache Spark
- Apply text preprocessing and feature engineering
- Evaluate model performance with proper metrics
- Understand supervised learning through hands-on practice

### 🌐 Workshop Environment: GitHub Codespaces
- **Cloud-based development** - No local installation required
- **Pre-configured** with Java 21, Python 3, and all dependencies
- **Accessible from any browser** - Just click and start coding
- **Consistent environment** for all participants

### 📋 Workshop Structure (3 Hours)
1. **Problem Statement & Dataset** (30 min)
2. **Environment Setup** (30 min)
3. **Data Loading & Preprocessing** (45 min)
4. **Feature Engineering** (45 min)
5. **Model Training & Evaluation** (30 min)

---

## Part 1: Problem Statement & Dataset

### 🍽️ The Business Challenge

**Predict:** What do you think is the main challenge restaurants face with customer reviews?

**The Problem:**
- Restaurants receive thousands of reviews daily
- Manual review analysis is time-consuming and subjective
- Need automated system to classify reviews as positive or negative
- Help restaurants understand customer sentiment at scale

**Reveal:** This is a classic **text classification** problem in machine learning. We'll build a system that can automatically read restaurant reviews and determine if they express positive or negative sentiment.

**Reflect:** 
- Why might this be important for a restaurant business?
- What challenges do you think we'll face when trying to teach a computer to understand sentiment?

---

## Understanding the Yelp Dataset

### 📊 Dataset Overview

**Predict:** What kind of data do you think we need to train a sentiment classifier?

**Our Dataset:**
- **Source:** Yelp Open Dataset
- **Size:** 20,000 restaurant reviews
- **Classes:** 10,000 positive + 10,000 negative
- **Format:** CSV with text and sentiment columns
- **Language:** English reviews only

**Sample Data:**
```csv
text,sentiment
"Great food and excellent service!",positive
"Terrible experience, would not recommend",negative
"Amazing pizza, friendly staff",positive
```

**Reveal:** We need **labeled data** - examples where humans have already decided if each review is positive or negative. This is called **supervised learning** because we're teaching the computer using examples with correct answers.

**Reflect:**
- Why do we need both positive and negative examples?
- What makes this a "balanced" dataset?

---

## Part 2: Environment Setup

### 🌐 GitHub Codespaces Setup

**Predict:** What do you think are the advantages of using a cloud-based development environment?

**Getting Started:**
1. Go to: `https://github.com/shishirdongre/cs-ed-lab`
2. Click **"Code"** button (green)
3. Select **"Codespaces"** tab
4. Click **"Create codespace on main"**
5. Wait 2-3 minutes for setup

**Reveal:** Codespaces provides a complete development environment in the cloud with:
- Java 21 pre-installed
- Apache Spark ready to setup
- Python 3 with required packages
- VS Code interface
- No local installation needed

**Reflect:**
- How does this compare to setting up everything on your own computer?
- What are the benefits of having a consistent environment for all participants?

---

## Setting Up Apache Spark

### ⚡ Step 1: Spark Installation

**Predict:** What do you think Apache Spark is used for in machine learning?

**Run:**
```bash
./setup_spark.sh
```

**What the script does:**
1. Downloads Apache Spark 4.0.1
2. Extracts all JAR files to `lib/` directory
3. Verifies installation
4. Cleans up temporary files

**Reveal:** Apache Spark is a distributed computing framework that can:
- Process large datasets efficiently
- Run machine learning algorithms at scale
- Handle data that's too big for a single computer
- Provide built-in ML algorithms like Naive Bayes

**Reflect:**
- Why might we need distributed computing for machine learning?
- What advantages does Spark provide over simpler tools?

---

## Creating the Dataset

### 📊 Step 2: Dataset Generation

**Predict:** Why do you think we create our own dataset instead of using the full Yelp dataset?

**Run:**
```bash
./create_dataset.sh
```

**What it generates:**
- 20,000 restaurant reviews
- 10,000 positive + 10,000 negative
- CSV format with text and sentiment columns
- File: `simple_yelp_reviews.csv`

**Reveal:** We create a controlled dataset because:
- **Workshop size:** Full Yelp dataset is too large (millions of reviews)
- **Balanced classes:** Equal positive/negative samples
- **Clean format:** Consistent structure for learning
- **Educational purpose:** Focus on concepts, not data wrangling

**Reflect:**
- Why is it important to have balanced classes?
- What challenges might we face with imbalanced data?

---

## Part 3: Data Loading & Preprocessing

### 📁 Step 3: Loading Dataset with Java

**Predict:** What challenges do you think we face when loading CSV data in Java?

**Run:**
```java
// Look at the loadData method in YelpSentimentAnalysisSpark.java
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

**Reveal:** Java doesn't have built-in CSV support, so we use:
- **Spark's CSV reader** for efficient data loading
- **Schema definition** to specify data types
- **Header handling** to skip the first row
- **Type safety** to prevent runtime errors

**Reflect:**
- Why is it important to define a schema?
- What could go wrong if we let Spark guess the data types?

---

## Text Preprocessing Pipeline

### 🧹 Step 4: Cleaning Text Data

**Predict:** What do you think we need to do to prepare text data for machine learning?

**Run:**
```java
// Look at the buildPipeline method
Tokenizer tokenizer = new Tokenizer()
    .setInputCol("text")
    .setOutputCol("words");

StopWordsRemover stopWordsRemover = new StopWordsRemover()
    .setInputCol("words")
    .setOutputCol("filtered_words");
```

**Preprocessing Steps:**
1. **Tokenization:** Split text into individual words
2. **Stop Words Removal:** Remove common words like "the", "and", "is"
3. **Lowercasing:** Standardize case
4. **Punctuation Removal:** Clean special characters

**Reveal:** Text preprocessing is crucial because:
- **Noise reduction:** Remove irrelevant information
- **Standardization:** Make similar words look the same
- **Feature quality:** Better features lead to better models
- **Computational efficiency:** Smaller vocabulary = faster processing

**Reflect:**
- Why do we remove stop words?
- What other preprocessing steps might be useful?

---

## Part 4: Feature Engineering

### 🔢 Step 5: Converting Text to Numbers

**Predict:** Why do you think machine learning algorithms need numerical input instead of text?

**Run:**
```java
// Look at the feature engineering in buildPipeline
HashingTF hashingTF = new HashingTF()
    .setInputCol("filtered_words")
    .setOutputCol("rawFeatures")
    .setNumFeatures(10000);

IDF idf = new IDF()
    .setInputCol("rawFeatures")
    .setOutputCol("features");
```

**Feature Engineering Process:**
1. **Bag of Words:** Count word occurrences
2. **Hashing Trick:** Convert words to numbers efficiently
3. **TF-IDF:** Weight words by importance
4. **Sparse Vectors:** Efficient storage of features

**Reveal:** Machine learning algorithms work with numbers because:
- **Mathematical operations:** Algorithms need to calculate distances, probabilities
- **Pattern recognition:** Numbers allow statistical analysis
- **Efficiency:** Numerical computations are much faster
- **Generalization:** Numbers can represent any type of data

**Reflect:**
- What is the "bag of words" approach?
- Why might TF-IDF be better than simple word counts?

---

## Understanding Naive Bayes Algorithm

### 🧠 Step 6: Classification Algorithm

**Predict:** How do you think a computer can learn to classify text as positive or negative?

**The Algorithm:**
```java
NaiveBayes naiveBayes = new NaiveBayes()
    .setFeaturesCol("features")
    .setLabelCol("label")
    .setModelType("multinomial")
    .setSmoothing(1.0);
```

**How Naive Bayes Works:**
1. **Learn patterns:** Count which words appear with positive/negative labels
2. **Calculate probabilities:** P(positive|word) and P(negative|word)
3. **Make predictions:** Combine word probabilities for new text
4. **"Naive" assumption:** Words are independent (not always true, but works well)

**Reveal:** Naive Bayes is a probabilistic classifier that:
- **Learns from examples:** Uses training data to build probability tables
- **Fast training:** Simple calculations, works well with text
- **Good baseline:** Often performs surprisingly well
- **Interpretable:** We can see which words are most important

**Reflect:**
- Why is it called "naive"?
- What are the advantages and disadvantages of this approach?

---

## Part 5: Model Training & Evaluation

### 🏋️ Step 7: Training the Model

**Predict:** What do you think happens during the training process?

**Run:**
```java
// Look at the main method
PipelineModel model = pipeline.fit(trainData);
Dataset<Row> predictions = model.transform(testData);
```

**Training Process:**
1. **Split data:** 80% training, 20% testing
2. **Build pipeline:** Preprocessing + classification
3. **Fit model:** Learn patterns from training data
4. **Make predictions:** Test on unseen data

**Reveal:** Training involves:
- **Learning patterns:** Model finds relationships between words and sentiment
- **Parameter optimization:** Adjusting internal weights
- **Validation:** Testing on held-out data
- **Performance measurement:** Accuracy, precision, recall

**Reflect:**
- Why do we split data into training and testing sets?
- What could happen if we tested on the same data we trained on?

---

## Model Evaluation Metrics

### 📊 Step 8: Measuring Performance

**Predict:** How do you think we measure if our model is working well?

**Run:**
```java
// Look at the evaluateModel method
MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
    .setLabelCol("label")
    .setPredictionCol("prediction")
    .setMetricName("accuracy");

double accuracy = evaluator.evaluate(predictions);
```

**Key Metrics:**
- **Accuracy:** Overall correctness (77.6% in our case)
- **Precision:** True positives / (True positives + False positives)
- **Recall:** True positives / (True positives + False negatives)
- **F1-Score:** Harmonic mean of precision and recall

**Reveal:** Evaluation helps us understand:
- **Model performance:** How well it generalizes to new data
- **Strengths and weaknesses:** Where it makes mistakes
- **Comparison:** How different algorithms perform
- **Confidence:** Whether the model is ready for production

**Reflect:**
- What does 77.6% accuracy mean?
- Why might precision and recall be more important than accuracy in some cases?

---

## Confusion Matrix Analysis

### 🔍 Step 9: Understanding Mistakes

**Predict:** What do you think a confusion matrix shows us about our model?

**Run:**
```java
// Look at the confusion matrix output
// Confusion Matrix:
// ┌─────────────┬─────────────┬─────────────┐
// │             │ Negative    │ Positive    │
// ├─────────────┼─────────────┼─────────────┤
// │ Negative     │ 1412        │ 500         │
// │ Positive     │ 373         │ 1615        │
// └─────────────┴─────────────┴─────────────┘
```

**Matrix Interpretation:**
- **True Negatives (1412):** Correctly predicted negative
- **False Positives (500):** Incorrectly predicted positive
- **False Negatives (373):** Incorrectly predicted negative
- **True Positives (1615):** Correctly predicted positive

**Reveal:** The confusion matrix shows:
- **Where mistakes happen:** Which classes are confused
- **Model bias:** Whether it favors one class over another
- **Error patterns:** Common types of misclassifications
- **Improvement opportunities:** What to focus on

**Reflect:**
- Which type of error might be worse for a restaurant: false positive or false negative?
- How could we use this information to improve the model?

---

## Sample Predictions Testing

### 🧪 Step 10: Testing on New Data

**Predict:** How do you think our model will perform on these sample reviews?

**Run:**
```java
// Look at the testSampleReviews method
String[][] sampleReviews = {
    {"Great food, excellent service!", "positive"},
    {"Terrible food, bad service", "negative"},
    {"Amazing pizza, friendly staff", "positive"},
    // ... more examples
};
```

**Sample Results:**
```
┌─────┬─────────────────────────────────────┬──────────┬──────────┐
│ #   │ Review Text                         │ Expected │ Predicted│
├─────┼─────────────────────────────────────┼──────────┼──────────┤
│ 1   │ Great food, excellent service!      │ positive │ positive │ ✅
│ 2   │ Terrible food, bad service          │ negative │ negative │ ✅
│ 3   │ Amazing pizza, friendly staff       │ positive │ positive │ ✅
│ 4   │ This place is absolutely horrible   │ negative │ negative │ ✅
│ 5   │ Love this restaurant, will come ... │ positive │ positive │ ✅
│ 6   │ Worst experience ever               │ negative │ negative │ ✅
│ 7   │ Outstanding quality and service     │ positive │ negative │ ❌
│ 8   │ Complete waste of money             │ negative │ negative │ ✅
└─────┴─────────────────────────────────────┴──────────┴──────────┘

📊 Sample Accuracy: 7/8 (87.5%)
```

**Reveal:** Testing on sample data shows:
- **Real-world performance:** How it works on new examples
- **Edge cases:** Where it struggles
- **Confidence building:** Seeing it work correctly
- **Error analysis:** Understanding mistakes

**Reflect:**
- Why might the model struggle with "Outstanding quality and service"?
- What does 87.5% accuracy on samples tell us?

---

## Hands-on Exercise 1: Data Exploration

### 🔍 Exercise: Understanding Your Data

**Predict:** What do you think you'll discover about the dataset?

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

**Reveal:** Data exploration helps us:
- **Understand patterns:** What the data looks like
- **Find issues:** Missing values, outliers
- **Plan preprocessing:** What cleaning is needed
- **Set expectations:** What performance to expect

**Reflect:**
- What patterns did you notice in the data?
- How might this information help improve the model?

---

## Hands-on Exercise 2: Feature Engineering

### ⚙️ Exercise: Experimenting with Features

**Predict:** How do you think changing feature parameters will affect performance?

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

**Reveal:** Feature engineering is crucial because:
- **Better features = better models:** Quality matters more than quantity
- **Domain knowledge:** Understanding the problem helps
- **Experimentation:** Try different approaches
- **Performance tuning:** Find optimal parameters

**Reflect:**
- How did changing parameters affect performance?
- What insights did you gain about feature engineering?

---

## Hands-on Exercise 3: Model Evaluation

### 📊 Exercise: Deep Dive into Performance

**Predict:** What additional metrics might be useful for understanding model performance?

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

**Reveal:** Comprehensive evaluation helps us:
- **Understand model behavior:** Where it succeeds and fails
- **Compare approaches:** Which algorithms work best
- **Identify improvements:** What to focus on next
- **Build confidence:** Know when the model is ready

**Reflect:**
- What did you learn about the model's strengths and weaknesses?
- How would you improve the model based on your analysis?

---

## Troubleshooting Common Issues

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
- Use VS Code debug configurations

---

## Real-World Applications

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

## Workshop Summary

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

## Next Steps and Resources

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

## Q&A Session

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

## Thank You!

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