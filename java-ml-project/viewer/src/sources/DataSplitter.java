public class DataSplitter {

    private final double testFraction;
    private final long randomSeed;

    public DataSplitter(double testFraction, long randomSeed) {
        this.testFraction = testFraction;
        this.randomSeed = randomSeed;
    }

    public Dataset<Row>[] split(Dataset<Row> data) {
        double trainFraction = 1.0 - testFraction;
        Dataset<Row>[] splits = data.randomSplit(new double[]{trainFraction, testFraction}, randomSeed);
        return splits;
    }
}
