# Java to Python Mapping: Yelp Sentiment Analysis

This document takes each part of **YelpSentimentAnalysisSpark.java** and answers: **How would you do the same thing in Python?** It also explains **why** the code looks different in the two languages—how the way each language runs (compiled vs interpreted), how types work (static vs dynamic), and how their libraries are designed all affect how you write code. It is written in simple language for someone who has taken a Java course and is new to Python.

---

## How Java and Python run your code (compiled vs interpreted)

**Java:** Before your program runs, the compiler turns your `.java` file into bytecode. When you run the program, the JVM (Java Virtual Machine) executes that bytecode. So there is a separate “compile” step, and the compiler needs to know the type of every variable so it can check that you use them correctly (e.g. you don’t pass a String where an int is expected).

**Python:** There is no separate compile step. When you run a Python file (or run a cell in a notebook), the interpreter reads your code line by line and runs it. The interpreter figures out what type each value is **while the program is running**. So Python doesn’t need you to write down types in your code—that’s why you’ll see less “type” words in Python.

**Why it matters here:** In Java we write types everywhere (e.g. `String`, `int`, `Dataset<Row>`). In Python we usually don’t. The Python version of the same idea often looks shorter because the language doesn’t ask you to declare those types.

**Analogies**
- **Shipping a box:** Java is like shipping: you must label the box (fragile, weight, size) before it goes on the truck so the carrier knows how to handle it. Python is like handing a package to someone in person: they can see what it is when they take it.

---

## How types work (static vs dynamic typing)

**Java (static typing):** When you create a variable, you must say what type it holds, and that type doesn’t change. For example, `String name = "hello";` means `name` is always a String. The compiler uses this to catch mistakes early (e.g. using a number as text by mistake).

**Python (dynamic typing):** When you create a variable, you don’t write a type. You just assign a value: `name = "hello"`. The variable `name` holds whatever you last assigned to it. It could hold a string now and a number later (though we usually don’t do that). The language checks types when the code actually runs, not before.

**Why it matters here:** In Java we need things like `ModelComponents` or explicit `StructType` so the compiler knows “this method returns one object that holds five other objects.” In Python we can just keep five different variable names (`vectorizer`, `clf`, etc.) and the interpreter doesn’t need a single “container” type—so we often don’t create one.

**Analogies**
- **Lockers:** In Java, each locker has a label that never changes (“this locker is only for textbooks”). In Python, a locker can hold a textbook today and a lunch box tomorrow—the locker doesn’t have a permanent label; whatever you put in defines what it is.
- **Recipe ingredients:** Java is like a recipe that says “Bowl A = flour only, Bowl B = sugar only.” Python is like having bowls you reuse: you put flour in one, then later wash it and put sugar in; the bowl doesn’t have a fixed “flour only” label.
- **Form fields:** Java is like a form where each box is labeled “Name” or “Date” and can’t be used for anything else. Python is like sticky notes: you can write a name on one and a date on another, and the note doesn’t care—it just holds what you wrote.

---

## 1. Constants (fixed values used in the program)

**Java**
```java
private static final String CSV_FILE = "simple_yelp_reviews.csv";
private static final double TEST_SIZE = 0.2;
private static final long RANDOM_SEED = 42L;
```

**How would you do this in Python?**
```python
# You can use the values directly where you need them:
# test_size=0.2, random_state=42 in train_test_split, etc.

# Or define names at the top (no "final" or type—just the name and value):
CSV_FILE = "simple_yelp_reviews.csv"
TEST_SIZE = 0.2
RANDOM_SEED = 42
```

**Why the code is structured this way**

- In Java you must say the **type** of each constant (`String`, `double`, `long`) so the compiler knows what kind of data it is. Python doesn’t need that; it sees `0.2` and treats it as a decimal number, and `42` as an integer.
- In Java, `final` means “this variable can’t be changed after it’s set.” Python doesn’t have `final`. By habit, people use names in ALL_CAPS to mean “don’t change this,” but the language doesn’t enforce it.
- Java has different number types (e.g. `int` vs `long`), so you sometimes write `42L` to mean “this is a long.” In Python there’s basically one integer type, so you just write `42`.

**Analogies**
- **Labeled jars in the kitchen:** Java is like writing “SUGAR” and “FLOUR” on jars and sealing them so no one can put something else in. Python is like writing “sugar” on a sticky note on the jar—everyone agrees not to change it, but nothing stops you from reusing the jar for something else later.
- **Settings on a phone:** Java is like a phone that makes you pick a type for each setting (e.g. “Volume: number, Ringtone: text”) and locks it. Python is like typing the value once (e.g. “0.2” or “42”) and the phone figures out whether it’s a number or text from what you typed.
- **Signs on doors:** Java is like a building where every room has a permanent sign (“Storage: cleaning supplies only”). Python is like a room where you put a sign that says “Meeting at 3pm”—people treat it as fixed for now, but there’s no lock on the door.

---

## 2. Holding several things together (the ModelComponents class)

**Java**
```java
private static class ModelComponents {
    public final Tokenizer tokenizer;
    public final StopWordsRemover stopWordsRemover;
    public final HashingTF hashingTF;
    public final IDFModel idfModel;
    public final LinearSVCModel svcModel;

    public ModelComponents(Tokenizer t, StopWordsRemover s, HashingTF h, IDFModel i, LinearSVCModel l) {
        this.tokenizer = t;
        this.stopWordsRemover = s;
        this.hashingTF = h;
        this.idfModel = i;
        this.svcModel = l;
    }
}
// Later: trainModel returns one ModelComponents object; we pass it to makePredictions and testSampleReviews.
```

**How would you do this in Python?**
```python
# We don't need a class. We just keep the pieces in separate variables.
# After we create the vectorizer and train the classifier, we use those names in later steps:

vectorizer = TfidfVectorizer(ngram_range=(1, 2), min_df=1, stop_words='english')
X_train_tfidf = vectorizer.fit_transform(X_train)
X_test_tfidf = vectorizer.transform(X_test)

clf = LinearSVC()
clf.fit(X_train_tfidf, y_train)

# When we want to predict, we use the same vectorizer and clf by name:
y_pred = clf.predict(X_test_tfidf)
```

**Why the code is structured this way**

- In Java, a method can only **return one thing**. Here we need to pass five things (tokenizer, stop-words remover, hashing, IDF model, SVC model) from `trainModel` to `makePredictions` and `testSampleReviews`. So we put all five inside one object—the `ModelComponents` class. That way we “return one thing” that actually holds five.
- In Python, we don’t have to return everything through one object. We create the vectorizer and the classifier, and we keep using the names `vectorizer` and `clf` in the same file (or in later notebook cells). So we never need to “bundle” them into a single class. The way we run the code (e.g. running cells one after another) lets us keep several variables around and use them by name.
- Different **library design**: Spark’s Java API is built around passing Datasets and fitted “stages” around, so having one object that holds all stages fits that style. In Python, scikit-learn often uses one object (e.g. `TfidfVectorizer`) that does several steps at once, and you just keep that one object (and the classifier) in variables.

**Analogies**
- **Returning from a trip:** Java is like a rule that you can only bring back one suitcase from a trip. So you pack five items (toothbrush, charger, book, etc.) into one bag and call it “my luggage.” Python is like coming back with a bag in one hand, a charger in the other, and a book under your arm—you don’t have to put everything in one bag just to satisfy a “one thing only” rule.

---

## 3. Where the program starts and “starting up” the system

**Java**
```java
public static void main(String[] args) {
    SparkSession spark = initializeSpark();
    try {
        Dataset<Row> data = loadAndPrepareData(spark);
        // ... do everything ...
    } finally {
        spark.stop();
    }
}

private static SparkSession initializeSpark() {
    SparkConf conf = new SparkConf().setAppName("...").setMaster("local[*]");
    SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
    return spark;
}
```

**How would you do this in Python?**
```python
# There is no main() in a notebook. You just run the cells from top to bottom.
# "Starting up" is just loading the libraries and reading the data:

import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
# ... then in other cells you load data, split, train, etc.
# There is nothing to "stop" at the end—when you close the notebook, everything is done.
```

**Why the code is structured this way**

- Java programs are built to run as one big program that starts at `main`. So we have a clear “start” (create Spark, load data, run steps) and “end” (stop Spark). Spark is a separate engine that runs in the background, so we have to tell it to start and later to stop.
- In a Python notebook, there is no single “main” program. You run one cell, then another. So there’s no single “entry point.” The “start” is just “run the first cell that imports and loads data.” There’s no background engine like Spark that we have to start and stop—we just use pandas and scikit-learn in memory, so we don’t need a “session” object or a `stop()` call.



---

## 4. Reading the CSV file and saying what columns exist

**Java**
```java
StructType schema = new StructType(new StructField[]{
    DataTypes.createStructField("text", DataTypes.StringType, false),
    DataTypes.createStructField("sentiment", DataTypes.StringType, false)
});

Dataset<Row> data = spark.read()
    .option("header", "true")
    .option("inferSchema", "false")
    .schema(schema)
    .csv(CSV_FILE);
```

**How would you do this in Python?**
```python
# If reading from a file path (like Java):
df = pd.read_csv("simple_yelp_reviews.csv")

# In Colab, often you upload the file first, then read from it:
uploaded = files.upload()
fname = next(iter(uploaded.keys()))
df = pd.read_csv(uploaded[fname])
```

**Why the code is structured this way**

- In Java with Spark, the library is built for big data that can be split across many machines. So Spark wants to know up front: “What columns exist and what type is each?” That’s the **schema**. You build it with `StructType` and `StructField` and pass it to `read()`. The library’s design assumes you declare structure before you read.
- In Python with pandas, the library is built for data that fits in memory on one machine. So `read_csv` looks at the first row (the header) and often guesses column types from the data. You usually **don’t** write down the schema—the library figures it out. So you get less code, but you rely on the library’s guessing. That’s a different **library API**: Spark asks you to say everything explicitly; pandas tries to do it for you.



---

## 5. Converting “positive”/“negative” into numbers and cleaning text

**Java**
```java
data = data.withColumn("label",
    when(col("sentiment").equalTo("positive"), 1)
    .otherwise(0)
).select("text", "label");
```

**How would you do this in Python?**
```python
# Clean the text and sentiment columns
df['text'] = df['text'].astype(str).str.strip()
df = df[df['text'] != '']
df['sentiment'] = df['sentiment'].astype(str).str.strip().str.lower()

# If you want a numeric label like Java (1 = positive, 0 = negative):
df['label'] = (df['sentiment'] == 'positive').astype(int)

# To see how many of each: df['sentiment'].value_counts()
```

**Why the code is structured this way**

- In Java we work on a **table** (Dataset). We add a column with `withColumn` and a condition: “if sentiment equals ‘positive’ put 1, else put 0.” Then we keep only the columns we want with `select`. So we’re always describing operations on columns of the table.
- In Python we also work on a table (a DataFrame), but we write it like “assign to a column”: `df['label'] = ...`. The right-hand side can be a condition: `df['sentiment'] == 'positive'` gives True/False for each row; `.astype(int)` turns that into 1 and 0. So we’re doing the same logic, but with a different **style**: direct assignment to columns and use of True/False, then convert to numbers. The `.str` is how pandas lets you run string methods (like strip, lower) on every value in a column.


## 6. Splitting data into training and test sets

**Java**
```java
Dataset<Row>[] splits = data.randomSplit(new double[]{0.8, 0.2}, RANDOM_SEED);
Dataset<Row> trainData = splits[0];
Dataset<Row> testData = splits[1];
```

**How would you do this in Python?**
```python
import numpy as np
from sklearn.model_selection import train_test_split

X = np.asarray(df['text'])
y = np.asarray(df['sentiment'])   # or df['label'] if you made a numeric column
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)
```

**Why the code is structured this way**

- In Java, `randomSplit` gives you **two tables**: one for train and one for test. Each table still has both the text and the label in the same rows. So you get back one “thing” that is an array of two Datasets, and you take `splits[0]` and `splits[1]`. The **return type** is “array of Dataset.”
- In Python, the usual function is `train_test_split`. You pass it your inputs (`X`) and your labels (`y`) as separate arguments. It returns **four separate arrays**: train inputs, test inputs, train labels, test labels. So the **library’s design** is different: instead of “split one table into two tables,” it’s “give me X and y, I’ll give you back four pieces.” That’s why we have four variables on the left. Using `stratify=y` keeps the same proportion of positive/negative in both train and test; Java’s `randomSplit` doesn’t do that—it just randomly splits rows.


---

## 7. Turning text into numbers the model can use (tokenize, stop words, TF–IDF)

**Java**
```java
Tokenizer tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words");
StopWordsRemover stopWordsRemover = new StopWordsRemover()
    .setInputCol("words").setOutputCol("filtered_words");
HashingTF hashingTF = new HashingTF()
    .setInputCol("filtered_words").setOutputCol("rawFeatures").setNumFeatures(10000);
IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");

// Run on training data:
Dataset<Row> trainTok = tokenizer.transform(trainData);
Dataset<Row> trainFilt = stopWordsRemover.transform(trainTok);
Dataset<Row> trainRaw = hashingTF.transform(trainFilt);
IDFModel idfModel = idf.fit(trainRaw);
Dataset<Row> trainFeats = idfModel.transform(trainRaw);
// For test data we run the same chain with the same fitted objects.
```

**How would you do this in Python?**
```python
from sklearn.feature_extraction.text import TfidfVectorizer

vectorizer = TfidfVectorizer(ngram_range=(1, 2), min_df=1, stop_words='english')
X_train_tfidf = vectorizer.fit_transform(X_train)
X_test_tfidf = vectorizer.transform(X_test)
```

**Why the code is structured this way**

- In Java, Spark’s library is built as a **pipeline of steps**. Each step is its own object: one turns text into words, one removes stop words, one turns words into term counts, one turns counts into TF–IDF. You tell each object which **column** to read from and which **column** to write to (`setInputCol`, `setOutputCol`). So the library is designed around “tables with named columns” and “one step at a time.” You have to keep all those objects (tokenizer, stopWordsRemover, etc.) and run the test data through the same steps—that’s why we needed something like `ModelComponents` to hold them.
- In Python, scikit-learn often **combines** several steps into one object. `TfidfVectorizer` does tokenization, dropping stop words, counting terms, and applying IDF all in one go. You don’t name columns; you just pass a list of text strings (or an array of them). So the **library API** is different: one object, one `fit_transform` on training data and one `transform` on test data. You don’t get intermediate “words” or “rawFeatures” columns—the library hides those. So the same job is done with fewer objects and no column names. That’s why in Python we don’t need a container to hold four separate steps—we have one vectorizer.


---

## 8. Training the classifier (Linear SVC)

**Java**
```java
LinearSVC lsvc = new LinearSVC()
    .setFeaturesCol("features")
    .setLabelCol("label")
    .setMaxIter(50)
    .setRegParam(0.1);
LinearSVCModel svcModel = lsvc.fit(trainFeats);
```

**How would you do this in Python?**
```python
from sklearn.svm import LinearSVC
clf = LinearSVC()
clf.fit(X_train_tfidf, y_train)
```

**Why the code is structured this way**

- In Java, the classifier is built to work with a **table** (Dataset). The table has columns named `"features"` and `"label"`. So you don’t pass the data as “first argument = features, second = labels.” You pass one table and tell the classifier **which column names** to use for features and which for the label. The **return type** of `fit` is a **model** object (`LinearSVCModel`) that you use later to predict.
- In Python, the classifier’s `fit` method takes **two separate arguments**: the feature matrix (e.g. `X_train_tfidf`) and the label vector (`y_train`). There are no column names—just “first argument is X, second is y.” So the **library API** is different: it expects separate arrays, not one table with named columns. If you wanted to match Java’s settings, you could write `LinearSVC(max_iter=50, C=10.0)` because in scikit-learn the regularization is controlled by `C` (bigger C = less regularization; Java’s `regParam=0.1` corresponds roughly to `C=10`).



---

## 9. Making predictions on the test set

**Java**
```java
Dataset<Row> testTok = models.tokenizer.transform(testData);
Dataset<Row> testFilt = models.stopWordsRemover.transform(testTok);
Dataset<Row> testRaw = models.hashingTF.transform(testFilt);
Dataset<Row> testFeats = models.idfModel.transform(testRaw);
Dataset<Row> predictions = models.svcModel.transform(testFeats);
// predictions has a column "prediction".
```

**How would you do this in Python?**
```python
y_pred = clf.predict(X_test_tfidf)
```

**Why the code is structured this way**

- In Java, test data is also a **table**. We run it through the same four steps (tokenizer, stop words, hashing, IDF) so we get a table with a `"features"` column. Then we pass that table to the model’s `transform`, and we get back a **new table** that has a `"prediction"` column. So “prediction” is: run a chain of steps, then one more step that adds a column. The **return type** of the model’s `transform` is a Dataset (a table).
- In Python, we already built `X_test_tfidf` in the “text to numbers” step. So prediction is: call `clf.predict(X_test_tfidf)`. The **return type** is an array of predicted labels (one per row). We don’t get a table with columns; we get one array. So the way the library gives you the answer is different: Java adds a column to a table; Python returns a single array.

---

## 10. Computing accuracy and other metrics

**Java**
```java
MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
    .setLabelCol("label")
    .setPredictionCol("prediction");

double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
double precision = evaluator.setMetricName("weightedPrecision").evaluate(predictions);
double recall = evaluator.setMetricName("weightedRecall").evaluate(predictions);
double f1 = evaluator.setMetricName("f1").evaluate(predictions);
```

**How would you do this in Python?**
```python
from sklearn.metrics import accuracy_score
y_pred = clf.predict(X_test_tfidf)
acc = accuracy_score(y_test, y_pred)
print("Model Training Accuracy:", acc)

# For more metrics you use separate functions:
# from sklearn.metrics import precision_score, recall_score, f1_score
# precision_score(y_test, y_pred, average='weighted')
# recall_score(y_test, y_pred, average='weighted')
# f1_score(y_test, y_pred, average='weighted')
```

**Why the code is structured this way**

- In Java, Spark gives you **one evaluator object**. You tell it which columns have the true label and the predicted label. Then you use the **same object** for every metric: you change the metric name (accuracy, precision, recall, f1) and call `evaluate(predictions)` again. So the **API** is: one object, one method, different “settings” for what to compute. The data is always the same table; the evaluator reads from it.
- In Python, scikit-learn gives you **different functions** for each metric. Each function takes the same two arguments: the true labels and the predicted labels (as two arrays). So there’s no “evaluator object”—just `accuracy_score(y_test, y_pred)`, `precision_score(y_test, y_pred, ...)`, etc. The **return type** of each is a single number. So the library design is “one function per metric” instead of “one object with a metric name.”

---

## 11. Building the confusion matrix

**Java**
```java
long[][] matrix = new long[2][2];
List<Row> results = predictions.select("label", "prediction").collectAsList();
for (Row row : results) {
    int actual = ...;   // get from row, handling Integer or Double
    int predicted = ...;
    matrix[actual][predicted]++;
}
return matrix;
```

**How would you do this in Python?**
```python
from sklearn.metrics import confusion_matrix
cm = confusion_matrix(y_test, y_pred)
# cm is a 2D array: rows = true label, columns = predicted label.
```

**Why the code is structured this way**

- In Java, Spark gives you a **table** of predictions. To get a confusion matrix, you have to bring the data back to your program (e.g. `collectAsList()`) and then **write a loop** yourself: go through each row, read the label and prediction, and add 1 to the right cell of a 2D array. You also have to handle the fact that Spark might give you Integer or Double from different places, so there’s a bit of type-checking code. So the **return type** from Spark is a list of rows; building the matrix is your job.
- In Python, scikit-learn has a **function** that does exactly that: you pass the true labels and the predicted labels (two arrays), and it returns the 2D confusion matrix. So the library does the loop and the counting for you. You get a simple “give me two arrays, get back one matrix” **API**. That’s a common pattern in Python: many things that in Java you’d write as a small loop are provided as a function in a library.


---

## 12. Predicting one new review (e.g. user types a sentence)

**Java**
```java
Dataset<Row> reviewData = spark.createDataFrame(
    Arrays.asList(RowFactory.create(review, 0)),
    new StructType(new StructField[]{
        DataTypes.createStructField("text", DataTypes.StringType, false),
        DataTypes.createStructField("label", DataTypes.IntegerType, false)
    })
);
Dataset<Row> reviewTok = models.tokenizer.transform(reviewData);
Dataset<Row> reviewFilt = models.stopWordsRemover.transform(reviewTok);
Dataset<Row> reviewRaw = models.hashingTF.transform(reviewFilt);
Dataset<Row> reviewFeats = models.idfModel.transform(reviewRaw);
Dataset<Row> prediction = models.svcModel.transform(reviewFeats);
Row result = prediction.select("prediction").collectAsList().get(0);
int predictedLabel = result.getInt(0);
String predicted = (predictedLabel == 1) ? "positive" : "negative";
```

**How would you do this in Python?**
```python
# User types in a text box (e.g. box.value). Then:
Xq = vectorizer.transform([box.value])
prediction = clf.predict(Xq)[0]
print('Prediction:', prediction)
```

**Why the code is structured this way**

- In Java, Spark only works on **tables** (Datasets). So to predict one review, we have to **make a tiny table** with one row and the same columns (text, label) and the same schema we used everywhere else. Then we run that one row through the same four steps and the model, and we get back a table with one row. We then take that row and read the prediction out of it. So “one string” has to be wrapped in a table and unwrapped at the end. That’s because the **library is built** around tables, not single rows.
- In Python, the vectorizer’s `transform` can take a **list of strings** (here a list of one string: `[box.value]`). It returns a small matrix with one row. The classifier’s `predict` takes that and returns an array of one label. We take the first element with `[0]`. So we never build a “table” or a schema—we just pass a list of one string and get back one value. The **library API** is built to accept lists/arrays of any size, including length 1, so we don’t need a special “one-row table” idea.

**Analogies**
- **One person at a party vs a guest list:** Java is like the host only having a “guest list” form: to seat one person you still have to create a full list with one name and fill in all the columns (text, label, schema). Python is like saying “Here’s one name” and the host seats them—no need to create a formal list of one.
- **Sending one letter vs a batch:** Java is like the post office that only accepts “batches”: you have to put your single letter in a tray, label the tray with a full schema (sender, recipient, etc.), and run it through the sorting machine to get one letter out at the end. Python is like handing one letter to the clerk and getting a single stamp or result back—the system is built to handle “one or many” the same way.
- **Ordering one item at a drive-through:** Java is like the drive-through that only accepts “orders” as a full form (order number, items, prices); so you fill out a form for one burger and hand it in. Python is like saying “One burger, please” and getting back “Burger”—no form, no table, just one item in and one answer out.

---
