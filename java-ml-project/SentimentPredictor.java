import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Runs the fitted sentiment pipeline to produce predictions on a dataset.
 */
public class SentimentPredictor {

    /**
     * Transform test data through the fitted pipeline and return predictions (with console output).
     */
    public Dataset<Row> predict(Dataset<Row> data, PipelineModel model) {
        Dataset<Row> predictions = model.transform(data).cache();
        predictions.count(); // trigger execution and cache for evaluator
        return predictions;
    }

    /**
     * Transform data through the fitted pipeline without printing.
     */
    public Dataset<Row> transform(Dataset<Row> data, PipelineModel model) {
        return model.transform(data);
    }
}
