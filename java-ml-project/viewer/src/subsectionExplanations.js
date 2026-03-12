/**
 * 2–3 paragraph explanations for each subsection.
 * Medium technical depth, aimed at novice Java programmers.
 * Edit this file to update explanations.
 */
export const SUBSECTION_EXPLANATIONS = {
  step1: `Step 1 loads the Yelp review data and prepares it for the model. We read a CSV file with review text and sentiment labels (positive/negative), then convert those labels to numbers (1 and 0) that the classifier can use. We also set up Spark, the framework that handles large-scale data processing.`,

  step2: `Step 2 splits the data into training and test sets. We use about 80% for training (teaching the model) and 20% for testing (measuring how well it works on unseen data). The split is random but reproducible thanks to a fixed seed.`,

  step3: `Step 3 defines the text-processing pipeline. We tokenize the text into words, remove stop words, convert words to numeric features (TF-IDF), and set up the classifier. Each stage feeds into the next via named columns.`,

  step4: `Step 4 trains the model by fitting the pipeline on the training data. Spark runs all stages in sequence and the classifier learns to separate positive from negative reviews. We then save the trained model to disk.`,

  step5: `Step 5 evaluates the model on the test set. We run predictions, then compute accuracy, precision, recall, F1, and a confusion matrix. These metrics tell us how well the model generalizes to new reviews.`,

  step6: `Step 6 tries the model on sample reviews. We create small DataFrames with single reviews, run them through the pipeline, and display the predicted sentiment. This shows how to use the model on new text.`,

  '1.1': `These constants define the configuration for the entire pipeline. CSV_FILE tells the program where to find the review data. TEST_SIZE (0.2) means we reserve 20% of the data for testing and use 80% for training. RANDOM_SEED (42L) ensures that when we split the data, we get the same split every time—useful for reproducible results. MODEL_PATH is where we save the trained model so we can reuse it later without retraining.

Using static final for these values is a Java best practice: they never change during execution, and keeping them at the top makes it easy to adjust the pipeline without digging through the code.`,

  '1.2': `Every Java application starts with a main method. Here, main creates a Spark session (the entry point for all Spark operations) and wraps the rest of the logic in a try block. The try block is important because many operations—reading files, training models—can throw exceptions (e.g., file not found, out of memory).

By putting the main logic inside try, we ensure that if something goes wrong, we can catch the error and handle it gracefully instead of crashing. The finally block (shown later) will run no matter what, so we can always shut down Spark properly.`,

  '1.3': `SparkInitializer creates and configures the SparkSession. SparkConf holds settings like the application name and master URL. "local[*]" means run Spark locally using all available CPU cores. The various set("spark.sql.adaptive...", "false") calls turn off some adaptive optimizations that can cause issues in small local runs.

SparkSession.builder() constructs the session, and getOrCreate() reuses an existing session if one is already running. setLogLevel("WARN") reduces console noise by only showing warnings and errors.`,

  '1.4': `Here we create a DataLoader object and use it to load the data. The DataLoader constructor takes the path to the CSV file (from our constants). loadAndPrepare(spark) does the actual work: it reads the file and prepares it for the rest of the pipeline.

We pass the SparkSession (spark) because Spark needs it to perform distributed operations. The loader returns a Dataset<Row>—Spark’s tabular data structure, similar to a table with rows and columns.`,

  '1.5': `loadAndPrepare is a thin wrapper that simply calls load. Why have both? Sometimes "prepare" might involve extra steps (filtering, validation). Here it’s minimal, but the separation keeps the design flexible. If we later need to add preprocessing, we can do it in loadAndPrepare without changing how main calls it.

The method takes a SparkSession and returns a Dataset<Row>. It declares throws IOException because reading files can fail (e.g., file not found).`,

  '1.6': `The load method does the real work. First, it defines a schema: our CSV has two columns, "text" (the review) and "sentiment" (positive or negative). We tell Spark the types explicitly so it doesn’t have to guess. spark.read() starts a read operation; we chain options like header("true") and schema(schema), then call csv(csvPath) to load the file.

Next, we convert sentiment to numbers: "positive" becomes 1, anything else becomes 0. withColumn creates a new "label" column, and select("text", "label") keeps only those two columns. Machine learning models need numeric labels, so this conversion is essential.`,

  '1.7': `If any exception occurs inside the try block, execution jumps to the catch block. We print the error message and stack trace so we can debug. The finally block always runs—whether we finished successfully or hit an error. Here we call spark.stop() to shut down Spark and release resources.

Always stopping Spark in finally is important. If we don’t, Spark processes may keep running in the background, and we might run out of resources or get confusing errors on the next run.`,

  '2.1': `This code shows the full pipeline in one place. After loading data, we split it, train a model, save the model, run predictions on the test set, evaluate performance, and finally test on sample reviews. Each step uses a dedicated class (DataLoader, DataSplitter, SentimentModelTrainer, etc.), which keeps the code organized and easier to understand.

Breaking the pipeline into steps like this is good practice. Each class has a clear responsibility, and we can test or modify one part without touching the others. The flow reads like a recipe: load, split, train, save, predict, evaluate, try samples.`,

  '2.2': `We create a DataSplitter with the test fraction (0.2) and random seed from our constants. split(data) divides the dataset into two parts: trainData (about 80%) and testData (about 20%). We store them in an array; splits[0] is the training set, splits[1] is the test set.

Next, we create a SentimentModelTrainer and call train(trainData). The trainer fits a model on the training data and returns a PipelineModel—the trained model we’ll use for predictions. We never train on the test set; that data is held back to measure how well the model generalizes.`,

  '2.3': `The split method uses randomSplit to divide the data. trainFraction is 1.0 - testFraction (e.g., 0.8 when testFraction is 0.2). We pass an array of two fractions: [0.8, 0.2] means 80% goes to the first split, 20% to the second.

The randomSeed ensures we get the same split every run. Without it, each run could produce different train/test sets, making results hard to compare. randomSplit returns an array of Datasets; we use splits[0] for training and splits[1] for testing.`,

  '3.1': `ModelComponents is a simple container class that holds the five pieces of our model pipeline: the tokenizer, stop-words remover, hashing TF, IDF model, and SVC classifier. In Java, a method can only return one value, but we need to pass several objects from the training step to the prediction step. Wrapping them in one class solves that.

The constructor takes all five components and stores them in final fields. Using final means once they’re set, they can’t change—good for components that shouldn’t be modified after creation.`,

  '3.2': `We define each stage of the text-processing pipeline. Tokenizer splits text into words (setInputCol/setOutputCol specify which columns to read and write). StopWordsRemover removes common words like "the" and "a" that don’t help with sentiment. HashingTF converts words to numeric features using a hash function. IDF adjusts those features by how rare or common each word is across the dataset.

LinearSVC is the classifier: it learns to separate positive from negative reviews based on the features. Each stage is configured with input and output column names so they chain together: text → words → filtered_words → rawFeatures → features → prediction.`,

  '4.1': `We build a Pipeline by passing all stages (tokenizer, stopWordsRemover, hashingTF, idf, lsvc) to setStages. The pipeline runs them in order: first tokenization, then stop-word removal, then hashing, then IDF, then the classifier.

fit(trainData) trains the entire pipeline on the training data. Spark runs each stage in sequence, and the classifier learns from the final features. The result is a PipelineModel—a fitted pipeline we can use to transform new data.`,

  '4.2': `trainer.train(trainData) returns the fitted PipelineModel. We then save it to disk with model.write().overwrite().save(MODEL_PATH). overwrite() means we replace any existing model at that path. save() writes the model files to the specified directory.

Saving the model lets us reuse it later without retraining. For example, we could load it in a different program to predict sentiment for new reviews. Training can take minutes; loading a saved model takes seconds.`,

  '5.1': `The predict method takes the test data and the trained model, then calls model.transform(data) to run each pipeline stage and produce predictions. We chain .cache() so Spark keeps the result in memory (we’ll use it for evaluation). predictions.count() forces Spark to actually compute the result; without it, Spark might delay the work.

The method returns a Dataset<Row> with an extra "prediction" column. Each row now has both the true label and the model’s predicted label, which we need for evaluation.`,

  '5.2': `We create a SentimentPredictor and call predictor.predict(testData, model). The predictor runs the test data through the trained model and returns a Dataset with predictions. We pass this to the ModelEvaluator to compute accuracy, precision, recall, and other metrics.

Using a separate predictor class keeps the main method clean and makes it easy to swap in different prediction logic (e.g., loading a model from disk) without changing the evaluation code.`,

  '5.3': `evaluate uses MulticlassClassificationEvaluator to compute several metrics. We tell it which columns hold the true labels ("label") and predictions ("prediction"). Then we call evaluate() with different metric names: "accuracy" (fraction correct), "weightedPrecision", "weightedRecall", and "f1" (a balance of precision and recall).

Each metric gives a different view of performance. Accuracy is simple but can be misleading if classes are imbalanced. Precision and recall focus on how well we identify each class. F1 combines them into a single number.`,

  '5.4': `getConfusionMatrix builds a 2×2 table: rows are actual labels (0 or 1), columns are predicted labels. matrix[0][0] counts true negatives, matrix[0][1] false positives, matrix[1][0] false negatives, matrix[1][1] true positives. We iterate over the predictions, and for each row we increment the right cell.

The confusion matrix shows where the model makes mistakes. For example, a high count in matrix[1][0] means we often miss positive reviews (predict negative when they’re positive). This is more informative than a single accuracy number.`,

  '6.1': `run loops over a list of sample reviews. For each review, we create a small DataFrame with one row using spark.createDataFrame and a schema (text and label). We use label 0 as a placeholder since we don't need the true label for prediction. predictor.transform applies the full pipeline and returns the prediction.

We extract the predicted label from the result row. The prediction is 0 or 1; we convert it to "positive" or "negative" for display. This demonstrates how to use the model on brand-new text that wasn't in the training or test set.`,

  '6.2': `In main, we create a SampleReviewTester and call sampleTester.run(model, spark). We pass the trained model and the SparkSession. The tester uses them to run predictions on its built-in list of sample reviews.

This is the final step of the pipeline: after loading, splitting, training, evaluating, we try the model on a few example sentences to see it in action. It's a quick sanity check that the model produces sensible outputs.`,
}