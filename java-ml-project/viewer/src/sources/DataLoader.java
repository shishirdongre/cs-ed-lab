package com.example.ml;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.when;

/**
 * Loads Yelp review rows from CSV and turns text + sentiment into columns the model can use.
 */
public class DataLoader {

    private final String csvPath;

    public DataLoader(String csvPath) {
        this.csvPath = csvPath;
    }

    /**
     * Reads the CSV, converts sentiment strings to numeric labels, and keeps only text + label.
     */
    public Dataset<Row> load(SparkSession spark) throws IOException {
        // Fixed schema so Spark does not infer types differently on different runs.
        StructType schema = new StructType(new StructField[]{
                DataTypes.createStructField("text", DataTypes.StringType, false),
                DataTypes.createStructField("sentiment", DataTypes.StringType, false)
        });

        Dataset<Row> data = spark.read()
                .option("header", "true")
                .option("inferSchema", "false")
                .schema(schema)
                .csv(csvPath);

        // Classifier expects a numeric label column: positive -> 1, everything else -> 0.
        data = data.withColumn("label",
                when(col("sentiment").equalTo("positive"), 1)
                        .otherwise(0)
        ).select("text", "label");

        return data;
    }
}
