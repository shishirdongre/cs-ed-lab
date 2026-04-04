// Split Java source files for Yelp Sentiment Analysis (full sources with package and imports)
import yelpMain from './YelpSentimentAnalysis.java?raw'
import sparkInitializer from './SparkInitializer.java?raw'
import dataLoader from './DataLoader.java?raw'
import dataSplitter from './DataSplitter.java?raw'
import sentimentModelTrainer from './SentimentModelTrainer.java?raw'
import sentimentPredictor from './SentimentPredictor.java?raw'
import modelEvaluator from './ModelEvaluator.java?raw'
import sampleReviewTester from './SampleReviewTester.java?raw'
import simpleYelpReviewsCsv from './simple_yelp_reviews.csv?raw'

export const sources = {
  YelpSentimentAnalysis: yelpMain,
  SparkInitializer: sparkInitializer,
  DataLoader: dataLoader,
  simple_yelp_reviews: simpleYelpReviewsCsv,
  DataSplitter: dataSplitter,
  SentimentModelTrainer: sentimentModelTrainer,
  SentimentPredictor: sentimentPredictor,
  ModelEvaluator: modelEvaluator,
  SampleReviewTester: sampleReviewTester,
}
