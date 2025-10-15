# Yelp Sentiment Analysis

A simple Java sentiment analysis tool that uses real Yelp reviews to classify text as positive or negative.

## Quick Start

1. **Run the analysis:**
   ```bash
   ./run_with_local_jars.sh
   ```

2. **Generate new dataset (if needed):**
   ```bash
   # Activate virtual environment
   source ../venv/bin/activate
   
   # Generate dataset
   python3 create_simple_yelp_dataset.py
   ```

## Setup Instructions

### Prerequisites
- Java 8+
- Python 3+ (for dataset generation)

### Using Virtual Environment

The project uses a Python virtual environment for dataset generation. The virtual environment is located in the parent directory (`../venv/`).

**To activate the virtual environment:**
```bash
# From the java-ml-project directory
source ../venv/bin/activate
```

**To deactivate:**
```bash
deactivate
```

**To install Python dependencies (if needed):**
```bash
# Activate virtual environment first
source ../venv/bin/activate

# Install dependencies
pip install datasets pandas
```

## Files

- `YelpSentimentAnalysisSmileML.java` - Main sentiment analysis class
- `simple_yelp_reviews.csv` - Real Yelp dataset (2000 reviews)
- `create_simple_yelp_dataset.py` - Script to generate dataset from Hugging Face
- `run_with_local_jars.sh` - Run script using local JAR dependencies
- `lib/` - Local JAR dependencies
- `archive/` - Old/unused files

## Features

- Uses real Yelp reviews from Hugging Face dataset
- Naive Bayes classification with 71.5% accuracy
- Text preprocessing and feature extraction
- Train/test split and model evaluation
- Student exercises for learning

## Requirements

- Java 8+
- Python 3+ (for dataset generation)
- No Maven needed (uses local JARs)

## Troubleshooting

**If you get "Dataset not found" error:**
1. Activate the virtual environment: `source ../venv/bin/activate`
2. Generate the dataset: `python3 create_simple_yelp_dataset.py`
3. Run the analysis: `./run_with_local_jars.sh`

**If you get Python module errors:**
1. Activate the virtual environment: `source ../venv/bin/activate`
2. Install dependencies: `pip install datasets pandas`
3. Generate the dataset: `python3 create_simple_yelp_dataset.py`