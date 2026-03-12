import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.classification.LinearSVCModel;

/**
 * Container class to hold all fitted model components for the sentiment pipeline.
 * Used to pass the pipeline between training, prediction, and inference.
 */
public class ModelComponents {
    public final Tokenizer tokenizer;
    public final StopWordsRemover stopWordsRemover;
    public final HashingTF hashingTF;
    public final IDFModel idfModel;
    public final LinearSVCModel svcModel;

    public ModelComponents(Tokenizer tokenizer, StopWordsRemover stopWordsRemover,
                          HashingTF hashingTF, IDFModel idfModel, LinearSVCModel svcModel) {
        this.tokenizer = tokenizer;
        this.stopWordsRemover = stopWordsRemover;
        this.hashingTF = hashingTF;
        this.idfModel = idfModel;
        this.svcModel = svcModel;
    }
}
