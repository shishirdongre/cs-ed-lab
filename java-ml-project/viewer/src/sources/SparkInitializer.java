public class SparkInitializer {

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
