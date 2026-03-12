import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

/**
 * Initializes and configures the Spark session for Yelp sentiment analysis.
 */
public class SparkInitializer {

    /**
     * Create a Spark session with optimized configuration for local ML runs.
     */
    public static SparkSession createSession() {
        SparkConf conf = new SparkConf()
                .setAppName("YelpSentimentAnalysis")
                .setMaster("local[*]")
                .set("spark.sql.adaptive.enabled", "false")
                .set("spark.sql.adaptive.coalescePartitions.enabled", "false")
                .set("spark.ui.showConsoleProgress", "false")
                .set("spark.sql.adaptive.skewJoin.enabled", "false");

        // Lambda: only /tmp is writable; bind driver to localhost (Lambda has restricted networking)
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
