/**
 * Reflection questions for each sub-section (chunk).
 * Key: chunk id (e.g. "1.1", "2.3"). Value: { title, preamble?, items }.
 * Wording aligns with java-ml-project/docs/reflection-comparison-colab-vs-java-viewer.md (Java section).
 */

/** Single response box covers (a)–(d); same wording as Colab. */
const REFLECTION_PROMPT = `(a) What part of this step felt easiest to understand? Why?

(b) What part of this step felt most challenging or confusing? Why?

(c) What prior programming knowledge helped you understand this step, if any?

(d) What concept in this step felt new or unfamiliar, and was not helped much by your prior programming knowledge?`

const PREAMBLE_SUFFIX = `In the box below, answer (a)–(d), labeling each part a), b), c), and d).`

function preamble(reading) {
  return `${reading.trim()}\n\n${PREAMBLE_SUFFIX}`
}

const SINGLE_ITEM = [{ item_id: 'reflection', question: REFLECTION_PROMPT }]

/**
 * Preamble template (except 1.0 CSV-only):
 * Look at … in `SomeClass.java`. Read the `method` … and the surrounding code in that class.
 * Then read `YelpSentimentAnalysis.main` where … (no fixed line numbers—code may shift).
 */
export const REFLECTION_QUESTIONS = {
  '1.0': {
    title: 'Reflection: 1.0 The training dataset',
    preamble: preamble(
      'Look at the training data file `simple_yelp_reviews.csv` (Code tab or file list). Read the header row and a few data rows so you see the columns. This step has no Java class to open.'
    ),
    items: SINGLE_ITEM,
  },
  '1.1': {
    title: 'Reflection: 1.1 Reading the CSV in load()',
    preamble: preamble(
      'Look at the CSV loading code in `DataLoader.java`. Read the `load` method and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where the loader is created and `load` is called.'
    ),
    items: SINGLE_ITEM,
  },
  '1.2': {
    title: 'Reflection: 1.2 Calling the loader from the main function',
    preamble: preamble(
      'Look at the Spark session setup in `SparkInitializer.java`. Read `createSession` and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where the session is used with `DataLoader` and `load(spark)`. Optionally look at `DataLoader.load` again beside `main`.'
    ),
    items: SINGLE_ITEM,
  },
  '2.1': {
    title: 'Reflection: 2.1 DataSplitter & train setup in main',
    preamble: preamble(
      'Look at the train/test split code in `DataSplitter.java`. Read the `split` method and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` from the splitter through the first `train` call.'
    ),
    items: SINGLE_ITEM,
  },
  '2.2': {
    title: 'Reflection: 2.2 DataSplitter.split and the call in main',
    preamble: preamble(
      'Look at the split logic in `DataSplitter.java`. Read the `split` method and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where `split` is called on the loaded data.'
    ),
    items: SINGLE_ITEM,
  },
  '3.1': {
    title: 'Reflection: 3.1 Text processing and features',
    preamble: preamble(
      'Look at the text processing and feature code in `SentimentModelTrainer.java`. Read the `train` method and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where the trainer is built and `train` is called (lines 54–56).'
    ),
    items: SINGLE_ITEM,
  },
  '4.1': {
    title: 'Reflection: 4.1 Train the model',
    preamble: preamble(
      'Look at how the pipeline is fitted in `SentimentModelTrainer.java`. Read the `train` method (especially the end where `fit` runs) and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where `model` is assigned and saved.'
    ),
    items: SINGLE_ITEM,
  },
  '5.1': {
    title: 'Reflection: 5.1 SentimentPredictor.predict and the test-data call in main',
    preamble: preamble(
      'Look at the prediction code in `SentimentPredictor.java`. Read the `predict` method and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where `predict` is called on the test data.'
    ),
    items: SINGLE_ITEM,
  },
  '5.2': {
    title: 'Reflection: 5.2 Predictor in main',
    preamble: preamble(
      'Look at the prediction code in `SentimentPredictor.java`. Read the `predict` method and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where the predictor is constructed and `predict` is called, and connect those calls to the methods you read in `SentimentPredictor`.'
    ),
    items: SINGLE_ITEM,
  },
  '5.3': {
    title: 'Reflection: 5.3 ModelEvaluator.evaluate and the printed output',
    preamble: preamble(
      'Look at the evaluation and printed output in `ModelEvaluator.java`. Read the `evaluate` method and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where `evaluate` is called.'
    ),
    items: SINGLE_ITEM,
  },
  '5.4': {
    title: 'Reflection: 5.4 Confusion matrix',
    preamble: preamble(
      'Look at the confusion matrix code in `ModelEvaluator.java`. Read `getConfusionMatrix`, `evaluate`, and the surrounding code in that class. Then read `YelpSentimentAnalysis.main` where `evaluate` is called.'
    ),
    items: SINGLE_ITEM,
  },
  '6.1': {
    title: 'Reflection: 6.1 SampleReviewTester.run and the sample review list',
    preamble: preamble(
      'Look at the sample review harness in `SampleReviewTester.java`. Read the `SAMPLE_REVIEWS` block, the `run` method, and the surrounding code in that class. Then check `YelpSentimentAnalysis.main` and find where `SampleReviewTester` is constructed and `run` is called so you see how that harness is wired in.'
    ),
    items: SINGLE_ITEM,
  },
}

/** One combined answer for (a)–(d); roughly prior 4×75 minimum. */
const DEFAULT_MIN_LENGTH = 200

export function getReflectionForChunk(chunk) {
  if (!chunk || chunk.substep == null) return null
  const q = REFLECTION_QUESTIONS[chunk.id]
  if (!q) return null
  return {
    sectionId: q.sectionId ?? chunk.id,
    title: q.title,
    preamble: q.preamble,
    items: q.items,
    minLength: q.minLength ?? DEFAULT_MIN_LENGTH,
    showConfidence: q.showConfidence ?? true,
  }
}
