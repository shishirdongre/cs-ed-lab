/**
 * 2 or 3 paragraph explanations for each subsection.
 * Medium technical depth, aimed at novice Java programmers.
 * Edit this file to update explanations.
 */
export const SUBSECTION_EXPLANATIONS = {
  dataset: `This workshop learns from Yelp-style restaurant reviews. Each row of the training file pairs the full text of one review with a single label: positive or negative. Those labels are what we want the model to predict from the words alone: classic supervised learning.

The preview bundled in this viewer is only a small excerpt so the page stays fast. When you run the Java project on your machine, it reads a much larger CSV from disk next to the source code, on the order of 10,000 rows.

Use the **Code** tab to scroll the preview as raw comma-separated text. The **Code explorer** sidebar tab lists the same file as simple_yelp_reviews.csv next to the Java classes.`,

  step1: `Step 1 loads the Yelp review data and prepares it for the model: the CSV stores review text and sentiment labels, and the code turns those labels into numbers 1 and 0 that the classifier can use.

Work through 1.0 to see the file format, then 1.1 for the loading logic in DataLoader, then 1.2 for calling the loader from the main function.`,

  '1.0': `Before we step into the loader code, it helps to look at the data file itself. Each row is one restaurant review in plain text plus a human-chosen label: positive or negative. Those two columns are all Spark needs to treat this as supervised learning: learn a mapping from words in the review to the label.

The file is comma-separated CSV. The boxed section shows five sample rows in a table. The full bundled preview as raw data, every line exactly as in the file, is in the Code section: use View code or the Code tab, which opens the same preview the workshop ships with; you can also open simple_yelp_reviews.csv in Code explorer. On your machine, the real training file is larger, on the order of 10,000 rows.`,

  step2: `In Step 1 you loaded every labeled review into one Spark dataset typed as Dataset<Row> in Java, meaning a distributed table of Row records as in Step 1.2. Before training, we divide those rows into two piles. Most rows form the training set, which the model learns from. The rest form the test set, held aside so we can score the model honestly on reviews it was not trained on.

This step uses about 80% for training and 20% for testing. The split is random but reproducible because the code uses a fixed random seed. After this, later steps in the program fit the model on trainData only, run predictions on testData for metrics, and finally try a few hand-written sample reviews.`,

  step3: `Step 3 defines the text-processing pipeline in SentimentModelTrainer. We tokenize the text into words, remove stop words, convert words to numeric TF-IDF features, and set up the classifier. Each stage feeds into the next via named columns. Work through 3.1 for how raw text becomes features in code.

The diagram below shows only the Spark ML stage chain; the same boxes appear again inside the full program diagram in Step 4.`,

  step4: `Step 4 trains the model by fitting the pipeline on the training data. Spark runs all stages in sequence and the classifier learns to separate positive from negative reviews. In Step 5, after evaluation, this model typically achieves around 76 to 77% accuracy on the test set, meaning about 1 in 4 reviews may be misclassified.

The pipeline diagram below summarizes the whole program flow and the five stages inside the train method.`,

  step5: `Step 5 evaluates the model on the test set. We run predictions, then compute accuracy, precision, recall, F1, and a confusion matrix. These metrics tell us how well the model generalizes to new reviews.`,

  step6: `Step 6 tries the model on sample reviews. We create small one-row Spark tables, still expressed with the same Row-based generics as Dataset<Row> in the rest of the project, run them through the pipeline, and display the predicted sentiment. This shows how to use the model on new text. Note: with ~76% accuracy, positive reviews may sometimes be classified as negative, or the other way around. Sarcasm, short text, or ambiguous wording can confuse the model.`,

  '1.1': `The DataLoader class stores the CSV path in its constructor. The load method is where reading and cleaning happen.

It defines a schema with two columns, "text" for the review body and "sentiment" as positive or negative, so Spark does not have to guess column types. The Spark reader is configured with header enabled, the schema, and the CSV path, to load the file.

Then it adds a numeric "label" column: "positive" becomes 1, everything else becomes 0, and it keeps only text and label. Classifiers need numeric labels, so this step bridges the human-readable CSV and the model.

The method is declared to return Dataset<Row> in the Java source. That uses normal Java generics: Dataset plays a role similar to List or Set, and Row is the type parameter that tells the compiler each record is a Spark row object with columns, much like List<Person> would mean each list element is a Person.`,

  '1.2': `When you run the program, the JVM starts at main. That is the entry point every Java application uses. The highlighted lines are only the first small part of main: obtain a Spark session object named spark, construct a DataLoader with the CSV path constant, and assign the result of loader.load with spark passed in to data.

Focus on how the DataLoader from 1.1 is constructed with the file name and called with the same spark instance the program uses for data I/O. The variable data has type Dataset<Row>: Spark’s distributed dataset specialized so each row is a Row instance. That mirrors standard library generics. List<String> is a list whose elements are strings; Map<String, Integer> associates string keys with integers. Here Dataset<Row> says this Spark dataset is a collection of Row values, each holding the columns defined in the loader, not a different row type. You do not need to understand the whole of main yet.`,

  '2.1': `After load finishes with spark, the variable data holds every review row we loaded in Step 1. The next lines in main are where that single dataset gets split and where training begins.

We construct a DataSplitter using the same TEST_SIZE of 0.2 and RANDOM_SEED you would see with the CSV path at the top of the class. Calling split on the full dataset returns two datasets: trainData is roughly 80% of the rows and is the only data we pass into trainer.train with trainData as the argument. testData is the other 20% and is not used for learning; it is reserved for evaluation later so accuracy reflects performance on unseen reviews.

The same block also creates a SentimentModelTrainer and calls train. That kicks off the training you will study in Steps 3 and 4. Still further down in main, outside the lines highlighted here, the program runs predictions and metrics on the test set and tries sample reviews, but the key idea here is split first, then train only on trainData.`,

  '2.2': `Our DataSplitter.split method does not reinvent random splitting. It delegates to randomSplit, a method on Spark’s Dataset type that comes with the Spark SQL library as part of the machine learning stack.

At the top of DataSplitter, the imports are org.apache.spark.sql.Dataset and org.apache.spark.sql.Row. The parameter and return type Dataset<Row> applies that same generics idea: Row is the type argument, so split accepts any Spark dataset whose rows are Row instances and the outputs are again Dataset<Row>. The unparameterized name Dataset is the raw generic class; Dataset<Row> is the version typed for Spark SQL rows. Because randomSplit is defined on Dataset, any Dataset<Row> in the project can call it.

What randomSplit does: you give it an array of positive weights that sum to 1, here trainFraction and testFraction after we set trainFraction to 1.0 minus testFraction, for example 0.8 and 0.2 so roughly 80% of rows land in the first output dataset and 20% in the second. Spark pseudo-randomly assigns each row to one bucket according to those weights. We also pass randomSeed so the assignment is repeatable across runs. The method returns an array of two Dataset<Row> values, still parameterized with Row for each split; index 0 is training, index 1 is testing.`,

  '3.1': `Here we wire up text processing and feature extraction before the classifier. Tokenizer splits text into words; methods like setInputCol and setOutputCol tell each stage which columns to read and write. StopWordsRemover removes common words like "the" and "a" that don’t help with sentiment. HashingTF converts words to numeric features using a hash function. IDF adjusts those features by how rare or common each word is across the dataset.

LinearSVC is the classifier: it learns to separate positive from negative reviews based on the features. Each stage is configured with input and output column names so they chain together: text → words → filtered_words → rawFeatures → features → prediction.`,

  '4.1': `We build a Pipeline by passing all stages to setStages: tokenizer, stopWordsRemover, hashingTF, idf, and lsvc. The pipeline runs them in order: first tokenization, then stop-word removal, then hashing, then IDF, then the classifier.

Calling fit on trainData trains the entire pipeline on the training data. The argument trainData is a Dataset<Row>: training examples as Spark rows. Spark runs each stage in sequence, and the classifier learns from the final features. The result is a PipelineModel, a fitted pipeline we can use to transform new data. This LinearSVC plus TF-IDF setup typically achieves roughly 76 to 77% accuracy on Yelp-style reviews; Step 5 covers evaluation.

In main, trainer.train with trainData returns that same PipelineModel so the rest of the program can call predict and evaluate on the in-memory object.`,

  '5.1': `predict is the batch path for the held-out test set. It takes the test rows, each still carrying the true "label" from the CSV, plus the fitted PipelineModel from training.

Under the hood, transform calls model.transform on the data. Spark runs the same ordered stages you defined in SentimentModelTrainer: tokenize, remove stop words, TF-IDF features, then the linear classifier. Each review becomes a row with a new "prediction" column, either 0 or 1, while the original "label" column stays as ground truth for evaluation.

Spark builds a lazy execution plan, so nothing runs until something asks for results. predict caches the transformed dataset and then calls count. That combination forces Spark to actually compute every row once and keep the result in memory. The return type is still Dataset<Row>: same generic pattern as before, now with extra columns produced by the pipeline. Without the cache and count trick, later code that reads the predictions several times for accuracy, F1, and confusion cells might re-run the whole pipeline from scratch. Returning the cached dataset hands ModelEvaluator a stable table where "label" and "prediction" can be compared line by line.`,

  '5.2': `We create a SentimentPredictor and call predict on the predictor with testData and the model. The predictor runs the test data through the trained model and returns another Dataset<Row> with prediction columns added, still using Row as the type parameter so the compiler treats it as the same kind of distributed table of rows. We pass this to the ModelEvaluator to compute accuracy, precision, recall, and other metrics.

Using a separate predictor class keeps the main method clean and makes it easy to swap in different prediction logic without changing the evaluation code.`,

  '5.3': `evaluate takes the predictions table as Dataset<Row>, the same generic row type as elsewhere, so each row still has named columns the evaluator can read. The method uses MulticlassClassificationEvaluator to compute several metrics. We tell it which columns hold the true labels, "label", and predictions, "prediction". Then we call evaluate with different metric names: "accuracy" means fraction correct, "weightedPrecision", "weightedRecall", and "f1" balances precision and recall.

Each metric gives a different view of performance. Accuracy is simple but can be misleading if classes are imbalanced. Precision and recall focus on how well we identify each class. F1 combines them into a single number.`,

  '5.4': `This step builds the confusion matrix in code with getConfusionMatrix, which takes a Dataset<Row> of predictions, again typed with Row so we traverse Spark rows that carry label and prediction columns. The matrix is a 2x2 table where rows are actual labels, 0 or 1, and columns are predicted labels. matrix[0][0] counts true negatives, matrix[0][1] false positives, matrix[1][0] false negatives, matrix[1][1] true positives. We iterate over the predictions, and for each row we increment the right cell.

The confusion matrix shows where the model makes mistakes. For example, a high count in matrix[1][0] means we often miss positive reviews: we predict negative when they’re positive. This is more informative than a single accuracy number.

The HTML table below shows the same layout with example counts; your run will differ.`,

  '6.1': `run loops over a list of sample reviews. For each review, predictLabelForText builds a one-row DataFrame, runs the pipeline, and reads the integer prediction. We compare the expected label to the model output and print a short table row.

This demonstrates how to use the model on brand-new text that wasn't in the training or test set. With ~76% accuracy, some predictions will be wrong; positive reviews may be labeled negative, for example when sarcasm or short, ambiguous text confuses the model.`,

  '6.2': `In main, we create a SampleReviewTester and call sampleTester.run with the model and spark. We pass the trained model and the SparkSession. The tester uses them to run predictions on its built-in list of sample reviews.

This is the final step of the pipeline: after loading, splitting, training, evaluating, we try the model on a few example sentences to see it in action. It's a quick sanity check that the model produces sensible outputs. To improve accuracy, consider alternative classifiers such as Naive Bayes, Logistic Regression, or tree-based models like Random Forest or GBT, which often perform differently; more features such as n-grams, bigrams or trigrams, to capture phrases; more or better data such as larger or cleaner datasets; hyperparameter tuning, for example grid search over maxIter, regParam, numFeatures; and handling class imbalance, for example oversampling or class weights if positive and negative are skewed.

When you are done with reflections here, continue to Step 7 in the sidebar for a thank-you message and optional workshop feedback.`,

  step7: `You have reached the end of the guided walkthrough: loading, splitting, training, evaluation, and sample predictions.

Open substep 7.1 for a short thank-you and an optional feedback box. Anything you submit there is stored the same way as your section reflections and helps improve this workshop.`,

  '7.1': `Thank you for working through this material.

Below you can add optional general feedback and submit it once. You may leave the box empty if you prefer; submissions use the same backend as your earlier reflections so facilitators can review them.`,

}
