public class SentimentPredictor {

    public Dataset<Row> predict(Dataset<Row> data, PipelineModel model) {
        Dataset<Row> predictions = model.transform(data).cache();
        predictions.count();
        return predictions;
    }

    public Dataset<Row> transform(Dataset<Row> data, PipelineModel model) {
        return model.transform(data);
    }
}
