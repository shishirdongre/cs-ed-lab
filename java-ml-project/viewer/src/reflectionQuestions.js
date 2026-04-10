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

const PREAMBLE_SUFFIX = `Please answer each of the following questions, labeling your responses a), b), c), and d).

Please write your answers in the box below, labeling each part a), b), c), and d).`

function preamble(reading) {
  return `${reading.trim()}\n\n${PREAMBLE_SUFFIX}`
}

const SINGLE_ITEM = [{ item_id: 'reflection', question: REFLECTION_PROMPT }]

export const REFLECTION_QUESTIONS = {
  '1.0': {
    title: 'Reflection: 1.0 The training dataset',
    preamble: preamble(
      'Read the file `simple_yelp_reviews.csv` (Code tab or explorer). Notice the columns; later you will see them read in `DataLoader.load()` and referenced from `YelpSentimentAnalysis.main`.'
    ),
    items: SINGLE_ITEM,
  },
  '1.1': {
    title: 'Reflection: 1.1 Reading the CSV in load()',
    preamble: preamble(
      'Read `DataLoader.load()`. In `YelpSentimentAnalysis.main`, look at the call `loader.load(spark)` and how its result is assigned.'
    ),
    items: SINGLE_ITEM,
  },
  '1.2': {
    title: 'Reflection: 1.2 Calling the loader from the main function',
    preamble: preamble(
      'Read `YelpSentimentAnalysis.main`: `SparkSession` creation, `new DataLoader(...)`, and the `load(spark)` call. Compare with `DataLoader.load()` from 1.1.'
    ),
    items: SINGLE_ITEM,
  },
  '2.1': {
    title: 'Reflection: 2.1 DataSplitter & train setup in main',
    preamble: preamble(
      'Read `DataSplitter` (constructor) and `split(...)`. In `YelpSentimentAnalysis.main`, look at `new DataSplitter(...)`, `splitter.split(data)`, `trainData` / `testData`, and the start of training (`SentimentModelTrainer`, `train(...)`).'
    ),
    items: SINGLE_ITEM,
  },
  '2.2': {
    title: 'Reflection: 2.2 split()',
    preamble: preamble(
      'Read `DataSplitter.split()`. In `main`, find the call that uses `split` on the loaded dataset.'
    ),
    items: SINGLE_ITEM,
  },
  '3.1': {
    title: 'Reflection: 3.1 Text processing and features',
    preamble: preamble(
      'Read `SentimentModelTrainer.train()` where the pipeline stages are built (`setStages`, tokenizer, hashing, IDF, classifier). In `main`, look at `new SentimentModelTrainer()` and `trainer.train(trainData)`.'
    ),
    items: SINGLE_ITEM,
  },
  '4.1': {
    title: 'Reflection: 4.1 Train the model',
    preamble: preamble(
      'Read `SentimentModelTrainer.train()`: `pipeline.fit(trainData)` and the return type. In `main`, follow the `train(trainData)` call and what is stored in the `model` variable.'
    ),
    items: SINGLE_ITEM,
  },
  '5.1': {
    title: 'Reflection: 5.1 predict()',
    preamble: preamble(
      'Read `SentimentPredictor.predict(...)`. In `main`, find where `predictor.predict(...)` is called on the test data.'
    ),
    items: SINGLE_ITEM,
  },
  '5.2': {
    title: 'Reflection: 5.2 Predictor in main',
    preamble: preamble(
      'Read `SentimentPredictor` (constructor / `predict`). In `YelpSentimentAnalysis.main`, look at the `SentimentPredictor` instance and the `predict(testData, model)` call.'
    ),
    items: SINGLE_ITEM,
  },
  '5.3': {
    title: 'Reflection: 5.3 evaluate()',
    preamble: preamble(
      'Read `ModelEvaluator.evaluate(...)`. In `main`, find `evaluator.evaluate(predictions)` and what is passed in.'
    ),
    items: SINGLE_ITEM,
  },
  '5.4': {
    title: 'Reflection: 5.4 Confusion matrix',
    preamble: preamble(
      'Read `ModelEvaluator.getConfusionMatrix(...)` and its use inside `evaluate(...)`. In `main`, it is the same `evaluator.evaluate(predictions)` call as in 5.3.'
    ),
    items: SINGLE_ITEM,
  },
  '6.1': {
    title: 'Reflection: 6.1 run()',
    preamble: preamble(
      'Read `SampleReviewTester.run(...)`. In `main`, find where `run(...)` will be called (6.2 shows the wiring).'
    ),
    items: SINGLE_ITEM,
  },
  '6.2': {
    title: 'Reflection: 6.2 SampleReviewTester in main',
    preamble: preamble(
      'Read `SampleReviewTester` and `SampleReviewTester.run(...)`. In `YelpSentimentAnalysis.main`, look at `new SampleReviewTester()` and `sampleTester.run(model, spark)`.'
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
