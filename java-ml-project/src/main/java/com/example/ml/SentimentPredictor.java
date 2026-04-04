package com.example.ml;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Collections;

/**
 * Runs the trained pipeline on data: batch test set, or a single review string.
 */
public class SentimentPredictor {

    /** One row of text + placeholder label for inference (label is ignored by transform). */
    private static final StructType SINGLE_REVIEW_SCHEMA = new StructType(new StructField[]{
            DataTypes.createStructField("text", DataTypes.StringType, false),
            DataTypes.createStructField("label", DataTypes.IntegerType, false)
    });

    /**
     * Runs predictions on a dataset, caches them, and triggers execution with {@code count()}
     * so later steps (metrics, confusion matrix) reuse the same materialized result.
     */
    public Dataset<Row> predict(Dataset<Row> data, PipelineModel model) {
        Dataset<Row> predictions = transform(data, model).cache();
        predictions.count();
        return predictions;
    }

    /** Applies the fitted pipeline without caching (fine for tiny one-row inputs). */
    public Dataset<Row> transform(Dataset<Row> data, PipelineModel model) {
        return model.transform(data);
    }

    /**
     * Predicts sentiment as 0 or 1 for a single review string (CLI and sample tests).
     */
    public int predictLabelForText(PipelineModel model, SparkSession spark, String reviewText) {
        Dataset<Row> oneRow = spark.createDataFrame(
                Collections.singletonList(RowFactory.create(reviewText, 0)),
                SINGLE_REVIEW_SCHEMA);
        Row row = transform(oneRow, model).select("prediction").first();
        return predictionColumnAsInt(row, 0);
    }

    private static int predictionColumnAsInt(Row row, int index) {
        Object v = row.get(index);
        if (v instanceof Integer) {
            return (Integer) v;
        }
        return ((Number) v).intValue();
    }
}
