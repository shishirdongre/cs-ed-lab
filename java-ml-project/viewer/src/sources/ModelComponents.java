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
