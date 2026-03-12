// Split Java source files for Yelp Sentiment Analysis (viewer: stripped, no imports/comments/backend)
import yelpRefactored from './YelpSentimentAnalysisRefactored.java?raw'
import sparkInitializer from './SparkInitializer.java?raw'
import dataLoader from './DataLoader.java?raw'
import dataSplitter from './DataSplitter.java?raw'
import modelComponents from './ModelComponents.java?raw'
import sentimentModelTrainer from './SentimentModelTrainer.java?raw'
import sentimentPredictor from './SentimentPredictor.java?raw'
import modelEvaluator from './ModelEvaluator.java?raw'
import sampleReviewTester from './SampleReviewTester.java?raw'

export const sources = {
  YelpSentimentAnalysisRefactored: yelpRefactored,
  SparkInitializer: sparkInitializer,
  DataLoader: dataLoader,
  DataSplitter: dataSplitter,
  ModelComponents: modelComponents,
  SentimentModelTrainer: sentimentModelTrainer,
  SentimentPredictor: sentimentPredictor,
  ModelEvaluator: modelEvaluator,
  SampleReviewTester: sampleReviewTester,
}
