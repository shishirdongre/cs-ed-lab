# Lab Manual: Java Sentiment Analysis with Machine Learning

**Course:** Introduction to Programming  
**Lab:** Analyzing Yelp Reviews with Java and Apache Spark  
**Duration:** 60-90 minutes

---

## Table of Contents
1. [Getting Started](#getting-started)
2. [Setting Up Your Environment](#setting-up-your-environment)
3. [Understanding the Code Structure](#understanding-the-code-structure)
4. [Running the Code](#running-the-code)
5. [Completing the Google Form](#completing-the-google-form)
6. [Reviewing the Results](#reviewing-the-results)

---

## Getting Started

### Step 1: Access the GitHub Repository

1. Open your web browser and navigate to: **https://github.com/shishirdongre/cs-ed-lab**
2. You should see the repository page with files and folders.

### Step 2: Create a GitHub Account (If Needed)

If you don't have a GitHub account:
1. Click the **"Sign up"** button in the top-right corner of GitHub
2. Enter your email address, create a password, and choose a username
3. Verify your email address when prompted
4. You're now ready to use GitHub!

---

## Setting Up Your Environment

### Step 3: Open the Repository in GitHub Codespaces

GitHub Codespaces is a cloud-based development environment that runs in your browser. You don't need to install anything on your computer!

1. **On the repository page**, click the green **"Code"** button (in the top-right area)
2. A dropdown menu will appear. Click on the **"Codespaces"** tab
3. Click **"Create codespace on main"**
4. A new browser tab will open, and you'll see "Setting up your codespace..." message

### Step 4: Wait for Codespace to Build

**This is important:** The codespace needs to build a complete development environment. This takes **2-5 minutes**.

**What you'll see:**
- A progress bar or loading indicator
- Messages like "Building container..." or "Installing extensions..."
- The VS Code interface will appear once ready

**What's being set up:**
- Java 21 programming environment
- Python 3 for data processing
- Apache Spark library (for machine learning)
- VS Code editor with helpful extensions
- All necessary tools and dependencies

**Don't close the browser tab** Wait until you see the VS Code interface fully loaded with the file explorer on the left side.

---

## Understanding the Code Structure

### Step 5: Explore the Project Files

Once your codespace is ready, you'll see VS Code with a file explorer on the left side.

1. **Look for the folder:** `java-ml-project`
2. **Click on it** to expand and see the files inside
3. **Find the main file:** `YelpSentimentAnalysisSpark.java`

This is the main Java program we'll be working with.

### Step 6: Open this Google form
- Enter your Research ID
- Follow the Google form, open the links to Github on the form, open the link in a new tab, read the code and come back to the form to answer follow up questions about the code
---


## We will run the code after you finish the Google form

## Running the Code

### Step 7: Understanding the Debugger (VS Code's Run Buttons)

VS Code has a special feature called the **Debugger** that lets you run programs with one click. We've set up three different scripts you can run.

**Where to find the Run buttons:**

1. **Look at the left sidebar** in VS Code
2. **Click the icon that looks like a play button with a bug** - this is the "Run and Debug" panel
3. **At the top of VS Code**, you'll see a dropdown menu that says something like "Run Sentiment Analysis"
4. **Next to that dropdown**, there's a green **play button**

**The three scripts available:**

1. **"Create Dataset"** - Creates the data file with restaurant reviews
2. **"Setup Spark"** - Downloads and sets up the machine learning library  
3. **"Run Sentiment Analysis"** - Runs the main program using LinearSVC classifier

### Step 8: Running Script 1 - Create Dataset

**What this does:** Creates a file called `simple_yelp_reviews.csv` with 10,000 positive and 10,000 negative restaurant reviews.

**How to run it:**

1. **Click the dropdown** at the top that says "Run and Debug"
2. **Select "Create Dataset"** from the list
3. **Click the green play button** (or press `F5`)
4. **Watch the terminal** at the bottom of VS Code
5. **Wait for it to finish** - you'll see messages like:
   - "Creating Yelp Sentiment Dataset..."
   - "Running Python script to create dataset..."
   - "Dataset created successfully!"

**Expected time:** 10-30 seconds

**What to look for:** You should see a success message and information about the dataset size.

### Step 9: Running Script 2 - Setup Spark

**What this does:** Downloads Apache Spark (a machine learning library) and sets it up so Java can use it.

**How to run it:**

1. **Click the dropdown** again
2. **Select "Setup Spark"** from the list
3. **Click the green play button**
4. **Watch the terminal** - this one takes longer!

**Expected time:** 2-5 minutes (depends on internet speed)

**What you'll see:**
- "Setting up Apache Spark 4.0.1..."
- "Downloading Apache Spark 4.0.1..." (with a progress bar)
- "Extracting Spark..."
- "Copying all JAR files to lib/..."
- "Setup complete!"

**Important:** Don't close the codespace while this is running. Wait for the "Setup complete!" message.

### Step 10: Running Script 3 - Run Sentiment Analysis

**What this does:** This is the main program! It:
- Loads the review data
- Processes the text
- Trains a machine learning model
- Makes predictions
- Shows you the results

**How to run it:**

1. **Click the dropdown**
2. **Select "Run Sentiment Analysis"**
3. **Click the green play button**
4. **Watch the output** - A terminal will open and the script will run in the terminal

**Expected time:** 30-60 seconds

**What you'll see:**

The program will print lots of information to the terminal. Here's what each section means:

#### DATA LOADING & PREPARATION
- Shows how many reviews were loaded (should be 20,000)
- Shows how many are positive vs negative

#### DATA SPLITTING
- Shows how the data is divided:
  - **Training data:** 80% (16,000 reviews) - used to teach the model
  - **Test data:** 20% (4,000 reviews) - used to test how well it learned

#### MACHINE LEARNING PIPELINE
- Lists the steps the program takes:
  - Tokenizer: splits text into words
  - StopWordsRemover: removes common words like "the", "and"
  - HashingTF: converts words to numbers
  - IDF: calculates word importance
  - LinearSVC: the learning algorithm

#### MODEL TRAINING
- Shows how long it took to train the model
- Usually takes 5-15 seconds

#### MAKING PREDICTIONS
- Shows how long it took to make predictions
- Usually takes 1-5 seconds

#### MODEL EVALUATION RESULTS
- **Accuracy:** What percentage of predictions were correct (usually 75-85%)
- **Precision:** How reliable the positive predictions are
- **Recall:** How many positive reviews were found
- **F1-Score:** A combined measure of precision and recall

#### CONFUSION MATRIX
A table showing:
- **True Positives:** Correctly identified positive reviews
- **True Negatives:** Correctly identified negative reviews
- **False Positives:** Negative reviews incorrectly labeled as positive
- **False Negatives:** Positive reviews incorrectly labeled as negative

#### SAMPLE PREDICTIONS
Shows predictions for example reviews like:
- "Great food and excellent service!" → positive
- "Terrible experience, would not recommend." → negative


---

## Reviewing the Results

### Step 13: Understanding What You Learned

After the lab, you should understand:

1. **How to use GitHub Codespaces** - a cloud-based coding environment
2. **How to run Java programs** using VS Code's debugger
3. **Basic machine learning concepts:**
   - Training data vs test data
   - How computers learn from examples
   - How to measure if a program is working well
4. **Text processing:**
   - How computers convert words to numbers
   - Why we remove common words
   - How patterns in text can be recognized


