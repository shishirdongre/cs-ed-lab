# Yelp Sentiment Analysis with Multinomial Naive Bayes

This project implements sentiment analysis on Yelp restaurant reviews using Apache Spark's Multinomial Naive Bayes classifier.

## Quick Start

```bash
# 1. Create the dataset
./create_dataset.sh

# 2. Setup Spark 4.0.1
./setup_spark.sh

# 3. Run sentiment analysis
./run_naive_bayes.sh
```

## Features

- **Multinomial Naive Bayes Classification** for sentiment analysis
- **Apache Spark 4.0.1** with Java 21 compatibility
- **Text Preprocessing Pipeline** including:
  - Tokenization
  - Stop words removal
  - TF-IDF feature extraction
- **Performance Metrics** including accuracy, precision, recall, and F1-score
- **Confusion Matrix** visualization

## Performance

- **Accuracy**: ~77.6%
- **Dataset**: 20,000 Yelp restaurant reviews (10,000 positive, 10,000 negative)
- **Train/Test Split**: 80/20

## Requirements

- Java 21
- Internet connection (for initial setup)
- wget (for downloading Spark)

## Setup

Run the setup scripts in order:

### 1. Create Dataset
```bash
# Generate the Yelp sentiment dataset
./create_dataset.sh
```

### 2. Setup Spark
```bash
# Download and configure Spark 4.0.1
./setup_spark.sh
```

This script will:
- Download Apache Spark 4.0.1 from the official repository
- Extract all JAR files to the `lib/` directory
- Clean up temporary files
- Verify the installation

### 3. Run Analysis
```bash
# Run the sentiment analysis
./run_naive_bayes.sh
```

## Project Structure

```
├── YelpSentimentAnalysisSpark.java    # Main implementation
├── create_dataset.sh                  # Dataset creation script
├── setup_spark.sh                     # Spark setup script
├── run_naive_bayes.sh                 # Run script
├── simple_yelp_reviews.csv            # Dataset (created by create_dataset.sh)
└── lib/                               # Spark 4.0.1 JAR files (created by setup_spark.sh)
```

## Algorithm Details

The implementation uses a complete ML pipeline:

1. **Data Loading**: Reads CSV with text and sentiment columns
2. **Text Preprocessing**: 
   - Tokenizes text into words
   - Removes stop words
   - Applies TF-IDF transformation
3. **Model Training**: Multinomial Naive Bayes classifier
4. **Evaluation**: Comprehensive metrics and confusion matrix
5. **Sample Predictions**: Tests on sample reviews

## Sample Output

```
=== Yelp Sentiment Analysis with Multinomial Naive Bayes ===
Loading data...
Loaded 20000 samples

Class Distribution:
+-----+-----+
|label|count|
+-----+-----+
|    1|10000|
|    0|10000|
+-----+-----+

Training samples: 16100
Test samples: 3900

Model Performance:
Accuracy: 0.776
Precision: 0.777
Recall: 0.776
F1-Score: 0.776
```