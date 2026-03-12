import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

/**
 * Evaluates a trained sentiment model: metrics and confusion matrix.
 */
public class ModelEvaluator {

    /**
     * Compute and print accuracy, precision, recall, F1, confusion matrix, and interpretation.
     */
    public void evaluate(Dataset<Row> predictions) {
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction");

        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
        double precision = evaluator.setMetricName("weightedPrecision").evaluate(predictions);
        double recall = evaluator.setMetricName("weightedRecall").evaluate(predictions);
        double f1 = evaluator.setMetricName("f1").evaluate(predictions);

        long[][] confusionMatrix = getConfusionMatrix(predictions);
    }

    /**
     * Compute 2x2 confusion matrix from predictions (rows=actual, cols=predicted).
     */
    public long[][] getConfusionMatrix(Dataset<Row> predictions) {
        long[][] matrix = new long[2][2];
        List<Row> results = predictions.select("label", "prediction").collectAsList();

        for (Row row : results) {
            int actual = getIntFromRow(row, 0);
            int predicted = getIntFromRow(row, 1);
            matrix[actual][predicted]++;
        }
        return matrix;
    }

    private static int getIntFromRow(Row row, int index) {
        Object v = row.get(index);
        if (v instanceof Integer) {
            return (Integer) v;
        }
        return ((Number) v).intValue();
    }
}
