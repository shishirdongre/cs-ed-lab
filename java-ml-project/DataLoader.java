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
 * Loads and prepares Yelp review data from CSV for sentiment analysis.
 */
public class DataLoader {

    private final String csvPath;

    public DataLoader(String csvPath) {
        this.csvPath = csvPath;
    }

    /**
     * Load and prepare data: read CSV, convert sentiment to label, show distribution.
     */
    public Dataset<Row> loadAndPrepare(SparkSession spark) throws IOException {
        return load(spark);
    }

    /**
     * Load raw CSV and convert sentiment column to numeric label (positive=1, negative=0).
     */
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
