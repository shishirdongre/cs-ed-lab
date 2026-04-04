/**
 * Reflection questions for each sub-section (chunk).
 * Key: chunk id (e.g. "1.1", "2.3"). Value: { title, items }.
 * Edit this file to change questions without touching component code.
 */
export const REFLECTION_QUESTIONS = {
  '1.0': {
    title: 'Reflection: 1.0 The training dataset',
    items: [
      {
        item_id: 'dataset_columns',
        question:
          'What does each row of the CSV represent, and what are the two column names? Why does the model need a numeric label in addition to the words?',
      },
    ],
  },
  '1.1': {
    title: 'Reflection: 1.1 Reading the CSV in load()',
    items: [
      {
        item_id: 'load_schema_labels',
        question:
          'How does load() declare the CSV columns, read the file, and turn the sentiment strings into numeric labels?',
      },
    ],
  },
  '1.2': {
    title: 'Reflection: 1.2 Calling the loader from the main function',
    items: [
      {
        item_id: 'entry_loader_wiring',
        question:
          'In the highlighted lines, what happens after main starts: how is DataLoader constructed and how is load(spark) connected to the result variable?',
      },
    ],
  },
  '2.1': {
    title: 'Reflection: 2.1 DataSplitter & train setup in main',
    items: [{ item_id: 'splitter_train_main', question: 'How are the train and test sets obtained, and why is the model trained only on trainData?' }],
  },
  '2.2': {
    title: 'Reflection: 2.2 split()',
    items: [{ item_id: 'split_main', question: 'What does the random seed contribute to the split?' }],
  },
  '3.1': {
    title: 'Reflection: 3.1 Text processing and features',
    items: [{ item_id: 'stage_defs_main', question: 'Describe how each stage connects to the next via input and output columns.' }],
  },
  '4.1': {
    title: 'Reflection: 4.1 Train the model',
    items: [{ item_id: 'pipeline_fit_main', question: 'What occurs when fit is called on the pipeline, and what object does train() return?' }],
  },
  '5.1': {
    title: 'Reflection: 5.1 predict()',
    items: [{ item_id: 'predict_main', question: 'What does the cache and count accomplish in the predict method?' }],
  },
  '5.2': {
    title: 'Reflection: 5.2 Predictor in main',
    items: [{ item_id: 'predictor_main_main', question: 'How does the predictor use the trained model on the test data?' }],
  },
  '5.3': {
    title: 'Reflection: 5.3 evaluate()',
    items: [{ item_id: 'evaluate_main', question: 'What does each metric (accuracy, precision, recall, F1) capture?' }],
  },
  '5.4': {
    title: 'Reflection: 5.4 Confusion matrix',
    items: [{ item_id: 'confusion_matrix_main', question: 'What information does the confusion matrix provide that the other metrics do not?' }],
  },
  '6.1': {
    title: 'Reflection: 6.1 run()',
    items: [{ item_id: 'run_main', question: 'Describe how a single review is prepared and passed through the model.' }],
  },
  '6.2': {
    title: 'Reflection: 6.2 SampleReviewTester in main',
    items: [{ item_id: 'sample_tester_main_main', question: 'How does the main program invoke the sample review tester?' }],
  },
}

const DEFAULT_MIN_LENGTH = 75

export function getReflectionForChunk(chunk) {
  if (!chunk || chunk.substep == null) return null
  const q = REFLECTION_QUESTIONS[chunk.id]
  if (!q) return null
  return {
    sectionId: q.sectionId ?? chunk.id,
    title: q.title,
    items: q.items,
    minLength: q.minLength ?? DEFAULT_MIN_LENGTH,
    showConfidence: q.showConfidence ?? true,
  }
}
