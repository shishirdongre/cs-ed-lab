public class DataLoader {

    private final String csvPath;

    public DataLoader(String csvPath) {
        this.csvPath = csvPath;
    }

    public Dataset<Row> loadAndPrepare(SparkSession spark) throws IOException {
        return load(spark);
    }

    public Dataset<Row> load(SparkSession spark) throws IOException {
        StructType schema = new StructType(new StructField[]{
                DataTypes.createStructField("text", DataTypes.StringType, false),
                DataTypes.createStructField("sentiment", DataTypes.StringType, false)
        });

        Dataset<Row> data = spark.read()
                .option("header", "true")
                .option("inferSchema", "false")
                .schema(schema)
                .csv(csvPath);

        data = data.withColumn("label",
                when(col("sentiment").equalTo("positive"), 1)
                        .otherwise(0)
        ).select("text", "label");

        return data;
    }
}
