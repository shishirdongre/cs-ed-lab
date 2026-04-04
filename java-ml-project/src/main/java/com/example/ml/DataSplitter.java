package com.example.ml;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Splits labeled data into training and test portions with a fixed random seed.
 */
public class DataSplitter {

    private final double testFraction;
    private final long randomSeed;

    public DataSplitter(double testFraction, long randomSeed) {
        this.testFraction = testFraction;
        this.randomSeed = randomSeed;
    }

    /**
     * Returns {@code [train, test]}; same seed yields the same split across runs.
     */
    public Dataset<Row>[] split(Dataset<Row> data) {
        double trainFraction = 1.0 - testFraction;
        return data.randomSplit(new double[]{trainFraction, testFraction}, randomSeed);
    }
}
