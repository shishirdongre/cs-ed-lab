import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Splits a dataset into training and test sets for model evaluation.
 */
public class DataSplitter {

    private final double testFraction;
    private final long randomSeed;

    public DataSplitter(double testFraction, long randomSeed) {
        this.testFraction = testFraction;
        this.randomSeed = randomSeed;
    }

    /**
     * Split data into [train, test] and print sample counts.
     */
    public Dataset<Row>[] split(Dataset<Row> data) {
        double trainFraction = 1.0 - testFraction;
        Dataset<Row>[] splits = data.randomSplit(new double[]{trainFraction, testFraction}, randomSeed);
        return splits;
    }
}
