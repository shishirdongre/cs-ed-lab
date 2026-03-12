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

        SparkSession spark = SparkSession.builder()
                .config(conf)
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        return spark;
    }
}
