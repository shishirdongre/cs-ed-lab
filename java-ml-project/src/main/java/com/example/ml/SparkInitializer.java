package com.example.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

/**
 * Builds a SparkSession tuned for local, single-machine ML workloads.
 */
public class SparkInitializer {

    /**
     * Configures Spark for local execution, disables some adaptive SQL features that can
     * complicate small runs, and quiets console logging.
     */
    public static SparkSession createSession() {
        SparkConf conf = new SparkConf()
                .setAppName("YelpSentimentAnalysis")
                .setMaster("local[*]")
                .set("spark.sql.adaptive.enabled", "false")
                .set("spark.sql.adaptive.coalescePartitions.enabled", "false")
                .set("spark.ui.showConsoleProgress", "false")
                .set("spark.sql.adaptive.skewJoin.enabled", "false");

        // Lambda / restricted environments: only /tmp is writable; bind driver to localhost.
        boolean inLambda = System.getenv("AWS_LAMBDA_FUNCTION_NAME") != null
                || "1".equals(System.getProperty("AWS_LAMBDA"));
        if (inLambda) {
            conf.set("spark.local.dir", "/tmp")
                    .set("spark.driver.bindAddress", "127.0.0.1")
                    .set("spark.driver.host", "127.0.0.1")
                    .set("spark.driver.extraJavaOptions", "-Djava.io.tmpdir=/tmp")
                    .set("spark.executor.extraJavaOptions", "-Djava.io.tmpdir=/tmp");
        }

        SparkSession spark = SparkSession.builder()
                .config(conf)
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        return spark;
    }
}
