/**
 * Reflection questions for each sub-section (chunk).
 * Key: chunk id (e.g. "1.1", "2.3"). Value: { title, items }.
 * Edit this file to change questions without touching component code.
 */
export const REFLECTION_QUESTIONS = {
  '1.1': {
    title: 'Reflection: 1.1 Constants',
    items: [{ item_id: 'constants_main', question: 'What role do these constants play in the overall pipeline?' }],
  },
  '1.2': {
    title: 'Reflection: 1.2 main declaration & try',
    items: [{ item_id: 'main_try_main', question: 'Describe the structure of the main method and its error handling.' }],
  },
  '1.3': {
    title: 'Reflection: 1.3 SparkInitializer.createSession()',
    items: [{ item_id: 'spark_init_main', question: 'What configuration choices are made when creating the Spark session?' }],
  },
  '1.4': {
    title: 'Reflection: 1.4 DataLoader in main',
    items: [{ item_id: 'dataloader_main_main', question: 'How is the DataLoader used to prepare data for the pipeline?' }],
  },
  '1.5': {
    title: 'Reflection: 1.5 loadAndPrepare()',
    items: [{ item_id: 'load_prepare_main', question: 'What does loadAndPrepare delegate to, and why might it be structured this way?' }],
  },
  '1.6': {
    title: 'Reflection: 1.6 load()',
    items: [{ item_id: 'load_main', question: 'Describe how the raw sentiment labels are converted for the model.' }],
  },
  '1.7': {
    title: 'Reflection: 1.7 Error handling',
    items: [{ item_id: 'error_handling_main', question: 'What happens when an exception occurs during execution?' }],
  },
  '2.1': {
    title: 'Reflection: 2.1 Full pipeline overview',
    items: [{ item_id: 'pipeline_overview_main', question: 'Describe the sequence of steps in the full analysis pipeline.' }],
  },
  '2.2': {
    title: 'Reflection: 2.2 DataSplitter & train setup in main',
    items: [{ item_id: 'splitter_train_main', question: 'How are the train and test sets obtained and used?' }],
  },
  '2.3': {
    title: 'Reflection: 2.3 split()',
    items: [{ item_id: 'split_main', question: 'What does the random seed contribute to the split?' }],
  },
  '3.1': {
    title: 'Reflection: 3.1 ModelComponents',
    items: [{ item_id: 'model_components_main', question: 'Why might these components be grouped into a single class?' }],
  },
  '3.2': {
    title: 'Reflection: 3.2 Stage definitions',
    items: [{ item_id: 'stage_defs_main', question: 'Describe how each stage connects to the next via input and output columns.' }],
  },
  '4.1': {
    title: 'Reflection: 4.1 Pipeline build & fit',
    items: [{ item_id: 'pipeline_fit_main', question: 'What occurs when fit is called on the pipeline?' }],
  },
  '4.2': {
    title: 'Reflection: 4.2 Train and save in main',
    items: [{ item_id: 'train_save_main', question: 'Why would we save the fitted model to disk?' }],
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
    title: 'Reflection: 5.4 getConfusionMatrix()',
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
    sectionId: chunk.id,
    title: q.title,
    items: q.items,
    minLength: DEFAULT_MIN_LENGTH,
    showConfidence: true,
  }
}
