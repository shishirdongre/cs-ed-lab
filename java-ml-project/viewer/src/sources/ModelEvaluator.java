package com.example.ml;

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

/**
 * Prints standard classification metrics and a 2x2 confusion matrix for test predictions.
 */
public class ModelEvaluator {

    /**
     * Each {@code evaluate(...)} call runs a separate Spark aggregation on {@code predictions};
     * predictions should already be cached upstream when possible.
     */
    public void evaluate(Dataset<Row> predictions) {
        MulticlassClassificationEvaluator ev = new MulticlassClassificationEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction");

        double accuracy = metric(ev, "accuracy", predictions);
        double precision = metric(ev, "weightedPrecision", predictions);
        double recall = metric(ev, "weightedRecall", predictions);
        double f1 = metric(ev, "f1", predictions);

        long[][] confusionMatrix = getConfusionMatrix(predictions);

        System.out.println("\n=== Model Evaluation ===");
        System.out.printf("Accuracy:  %.2f%%%n", accuracy * 100);
        System.out.printf("Precision: %.2f%%%n", precision * 100);
        System.out.printf("Recall:    %.2f%%%n", recall * 100);
        System.out.printf("F1:        %.2f%%%n", f1 * 100);
        System.out.println("Confusion matrix (rows=actual, cols=predicted):");
        System.out.printf("  TN=%d  FP=%d%n", confusionMatrix[0][0], confusionMatrix[0][1]);
        System.out.printf("  FN=%d  TP=%d%n", confusionMatrix[1][0], confusionMatrix[1][1]);
        System.out.println("========================\n");
    }

    private static double metric(MulticlassClassificationEvaluator ev, String metricName,
                                 Dataset<Row> predictions) {
        return ev.setMetricName(metricName).evaluate(predictions);
    }

    /**
     * Brings label/prediction pairs to the driver to count cells (OK for typical test-set sizes in this lab).
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
