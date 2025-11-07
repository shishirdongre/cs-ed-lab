// BlueJ version - Yelp Review Sentiment Analysis (Linear SVC)

import java.util.*;
import java.io.*;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import smile.classification.LinearSVM;
import smile.nlp.stemmer.PorterStemmer;

/** Yelp Review Sentiment Analysis with Linear SVC (hinge loss) */
public class YelpSentimentAnalysisSmileML {

    static class Vocab {
        final Map<String,Integer> index;
        Vocab(Map<String,Integer> index) { this.index = index; }
        int size() { return index.size(); }
    }
    private static class Vectorization {
        final Vocab vocab; final double[][] X;
        Vectorization(Vocab vocab, double[][] X) { this.vocab = vocab; this.X = X; }
    }
    private static class IndexSplit {
        final int[] trainIdx, testIdx;
        IndexSplit(int[] tr, int[] te){trainIdx=tr;testIdx=te;}
    }

    public static void main(String[] args) {
        System.out.println("=== Yelp Review Sentiment Analysis (Linear SVC) ===");
        try {
            DataPreparationResult data = loadAndPrepareData();
            ModelTrainingResult model = trainModel(data);
            evaluateModel(model);
            testSampleReviews(model);
            System.out.println("\n✅ Analysis completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DataPreparationResult loadAndPrepareData() {
        List<String[]> rows;
        try (CSVReader reader = new CSVReader(new FileReader("simple_yelp_reviews.csv"))) {
            rows = reader.readAll();
        } catch (Exception e) {
            throw new RuntimeException("Error loading CSV file", e);
        }
        if (rows.isEmpty()) throw new IllegalArgumentException("Empty CSV");
        rows = rows.subList(1, rows.size());

        String[] texts  = new String[rows.size()];
        String[] labels = new String[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            texts[i]  = rows.get(i)[0];
            labels[i] = rows.get(i)[1].trim();
        }

        String[] processed = Arrays.stream(texts)
            .map(StringUtils::lowerCase)
            .map(t -> t.replaceAll("[^a-zA-Z\\s]", " "))
            .map(StringUtils::normalizeSpace)
            .toArray(String[]::new);

        return new DataPreparationResult(texts, processed, labels);
    }

    private static Map<String,Integer> bagOfWords(String text, PorterStemmer stemmer) {
        String processed = text.toLowerCase()
            .replaceAll("http\\S+|www\\S+|@\\S+", "")
            .replaceAll("[^a-zA-Z\\s]", " ")
            .replaceAll("\\s+", " ").trim();

        String[] words = processed.split("\\s+");
        Map<String,Integer> bag = new HashMap<>();
        for (String w : words) {
            if (w.length() > 2 && w.matches("[a-zA-Z]+")) {
                String s = stemmer.stem(w);
                bag.put(s, bag.getOrDefault(s, 0) + 1);
            }
        }
        return bag;
    }

    private static Vectorization buildVocabAndVectorizeTrain(String[] trainTexts, int vocabSize) {
        PorterStemmer stemmer = new PorterStemmer();
        Map<String,Integer> df = new HashMap<>();
        List<Map<String,Integer>> bags = new ArrayList<>();

        for (String t : trainTexts) {
            Map<String,Integer> bag = bagOfWords(t, stemmer);
            bags.add(bag);
            for (String w : bag.keySet()) df.put(w, df.getOrDefault(w, 0) + 1);
        }

        Map<String,Integer> filteredDf = new HashMap<>();
        for (Map.Entry<String,Integer> e : df.entrySet()) if (e.getValue() >= 2) filteredDf.put(e.getKey(), e.getValue());

        List<Map.Entry<String,Integer>> sorted = new ArrayList<>(filteredDf.entrySet());
        sorted.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));
        int K = Math.min(vocabSize, sorted.size());
        Map<String,Integer> idx = new HashMap<>(K);
        for (int i = 0; i < K; i++) idx.put(sorted.get(i).getKey(), i);
        Vocab vocab = new Vocab(idx);

        double[][] X = new double[trainTexts.length][K];
        for (int i = 0; i < trainTexts.length; i++) fillRow(X[i], bags.get(i), vocab);
        return new Vectorization(vocab, X);
    }

    private static double[][] vectorizeWithVocab(String[] texts, Vocab vocab) {
        PorterStemmer stemmer = new PorterStemmer();
        double[][] X = new double[texts.length][vocab.size()];
        for (int i = 0; i < texts.length; i++) {
            Map<String,Integer> bag = bagOfWords(texts[i], stemmer);
            fillRow(X[i], bag, vocab);
        }
        return X;
    }

    private static void fillRow(double[] row, Map<String,Integer> bag, Vocab vocab) {
        Arrays.fill(row, 0.0);
        for (Map.Entry<String,Integer> e : bag.entrySet()) {
            Integer j = vocab.index.get(e.getKey());
            if (j != null) row[j] = Math.log(1.0 + e.getValue());
        }
    }

    private static int[] convertLabelsToInt01(String[] labels) {
        int[] y = new int[labels.length];
        for (int i = 0; i < labels.length; i++) {
            String s = labels[i].trim().toLowerCase();
            if (s.startsWith("pos")) y[i] = 1;
            else if (s.startsWith("neg")) y[i] = 0;
            else throw new IllegalArgumentException("Unknown label: " + labels[i]);
        }
        return y;
    }

    private static IndexSplit performTrainTestSplitIndices(int n, double testSize, long seed, int[] y) {
        List<Integer> pos = new ArrayList<>(), neg = new ArrayList<>();
        for (int i = 0; i < n; i++) (y[i]==1?pos:neg).add(i);
        Random rnd = new Random(seed);
        Collections.shuffle(pos, rnd); Collections.shuffle(neg, rnd);
        int tp = Math.max(1, (int)Math.round(pos.size()*testSize));
        int tn = Math.max(1, (int)Math.round(neg.size()*testSize));
        List<Integer> test = new ArrayList<>();
        test.addAll(pos.subList(0, Math.min(tp, pos.size())));
        test.addAll(neg.subList(0, Math.min(tn, neg.size())));
        boolean[] isTest = new boolean[n]; for (int i:test) isTest[i]=true;
        List<Integer> train = new ArrayList<>(); for (int i=0;i<n;i++) if (!isTest[i]) train.add(i);
        return new IndexSplit(train.stream().mapToInt(Integer::intValue).toArray(),
                              test.stream().mapToInt(Integer::intValue).toArray());
    }

    private static ModelTrainingResult trainModel(DataPreparationResult data) {
        int[] y01 = convertLabelsToInt01(data.labels);

        int vocabSize = 50000;
        IndexSplit splitIdx = performTrainTestSplitIndices(y01.length, 0.2, 42, y01);

        String[] trainTexts = new String[splitIdx.trainIdx.length];
        String[] testTexts  = new String[splitIdx.testIdx.length];
        int[] yTrain01 = new int[splitIdx.trainIdx.length];
        int[] yTest01  = new int[splitIdx.testIdx.length];

        for (int i = 0; i < splitIdx.trainIdx.length; i++) {
            trainTexts[i] = data.processedTexts[splitIdx.trainIdx[i]];
            yTrain01[i]   = y01[splitIdx.trainIdx[i]];
        }
        for (int i = 0; i < splitIdx.testIdx.length; i++) {
            testTexts[i] = data.processedTexts[splitIdx.testIdx[i]];
            yTest01[i]   = y01[splitIdx.testIdx[i]];
        }

        Vectorization vecTrain = buildVocabAndVectorizeTrain(trainTexts, vocabSize);
        double[][] Xtrain = vecTrain.X;
        double[][] Xtest  = vectorizeWithVocab(testTexts, vecTrain.vocab);

        // map {0,1} -> {-1,+1} for SVM
        int[] yTrain = new int[yTrain01.length];
        int[] yTest  = new int[yTest01.length];
        for (int i = 0; i < yTrain.length; i++) yTrain[i] = yTrain01[i] == 1 ? +1 : -1;
        for (int i = 0; i < yTest.length;  i++) yTest[i]  = yTest01[i]  == 1 ? +1 : -1;

        LinearSVM svm = trainLinearSVC(Xtrain, yTrain);

        int[] predsSVM = new int[Xtest.length];
        for (int i = 0; i < Xtest.length; i++) predsSVM[i] = svm.predict(Xtest[i]);

        // map back to {0,1}
        int[] preds01 = new int[predsSVM.length];
        for (int i = 0; i < predsSVM.length; i++) preds01[i] = predsSVM[i] == +1 ? 1 : 0;

        TrainTestSplitResult split = new TrainTestSplitResult(Xtrain, Xtest, yTrain01, yTest01);
        return new ModelTrainingResult(svm, split, preds01, data.labels, vecTrain.vocab);
    }

    private static LinearSVM trainLinearSVC(double[][] X, int[] y) {
        // Common defaults that work well on bag-of-words:
        double lambda = 1e-4;     // L2 regularization strength
        int epochs    = 20;       // passes over data
        double tol    = 1e-3;     // optimization tolerance
        // Hinge loss (SVM) by default; Smile’s LinearSVM uses {-1,+1} labels.
        return LinearSVM.fit(X, y, lambda, epochs, tol);
    }

    private static void evaluateModel(ModelTrainingResult model) {
        double acc = calculateAccuracy(model.split.testLabels, model.predictions01);
        System.out.println("   Overall Accuracy: " + String.format("%.3f", acc) + " (" + String.format("%.1f%%", acc * 100) + ")");

        ConfusionMatrix cm = new ConfusionMatrix(model.split.testLabels, model.predictions01);
        System.out.println(cm.toString());

        double precision = cm.precision();
        double recall    = cm.recall();
        double f1        = cm.f1();

        System.out.println("\n   Classification Report:");
        System.out.println("   Precision: " + String.format("%.3f", precision));
        System.out.println("   Recall   : " + String.format("%.3f", recall));
        System.out.println("   F1-Score : " + String.format("%.3f", f1));
    }

    private static void testSampleReviews(ModelTrainingResult model) {
        String[] samples = {
            "Great food, excellent service!",
            "Terrible food, bad service",
            "Amazing pizza, friendly staff"
        };
        for (String s : samples) {
            String sentiment = predictSentiment(model.svmModel, model.vocab, s);
            System.out.println("   '" + (s.length()>60? s.substring(0,60)+"..." : s) + "' -> " + sentiment);
        }
    }

    private static String predictSentiment(LinearSVM svm, Vocab vocab, String raw) {
        PorterStemmer stemmer = new PorterStemmer();
        Map<String,Integer> bag = bagOfWords(raw, stemmer);
        double[] x = new double[vocab.size()];
        fillRow(x, bag, vocab);
        int pred = svm.predict(x);         // {-1, +1}
        return (pred == +1) ? "positive" : "negative";
    }

    private static double[][] createBagOfWordsFeatures(String[] texts) { return new double[0][0]; } // not used

    private static double calculateAccuracy(int[] trueLabels01, int[] predLabels01) {
        int correct = 0;
        for (int i = 0; i < trueLabels01.length; i++) if (trueLabels01[i] == predLabels01[i]) correct++;
        return (double) correct / trueLabels01.length;
    }

    private static int[][] calculateConfusionMatrix(int[] trueLabels01, int[] predLabels01) {
        int[][] m = new int[2][2];
        for (int i = 0; i < trueLabels01.length; i++) m[trueLabels01[i]][predLabels01[i]]++;
        return m;
    }

    private static double safeDiv(int num, int den) { return den == 0 ? 0.0 : (double) num / den; }

    // Add this inside YelpSentimentAnalysisSmileML (as a static nested class)
    private static class ConfusionMatrix {
        final int[][] m = new int[2][2]; // [actual][pred], rows: 0=neg,1=pos; cols: 0=neg,1=pos

        ConfusionMatrix(int[] yTrue01, int[] yPred01) {
            for (int i = 0; i < yTrue01.length; i++) {
                m[yTrue01[i]][yPred01[i]]++;
            }
        }

        int tn() { return m[0][0]; }
        int fp() { return m[0][1]; }
        int fn() { return m[1][0]; }
        int tp() { return m[1][1]; }

        double accuracy() {
            int total = tn() + fp() + fn() + tp();
            return total == 0 ? 0.0 : (tn() + tp()) / (double) total;
        }
        double precision() {
            int den = fp() + tp();
            return den == 0 ? 0.0 : tp() / (double) den;
        }
        double recall() {
            int den = fn() + tp();
            return den == 0 ? 0.0 : tp() / (double) den;
        }
        double f1() {
            double p = precision(), r = recall();
            return (p + r) == 0 ? 0.0 : 2 * p * r / (p + r);
        }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n   Confusion Matrix:\n");
            sb.append("   Actual\\Predicted\tNegative\tPositive\n");
            sb.append("   Negative\t\t").append(tn()).append("\t\t").append(fp()).append("\n");
            sb.append("   Positive\t\t").append(fn()).append("\t\t").append(tp()).append("\n");
            return sb.toString();
        }
    }

    private static class TrainTestSplitResult {
        final double[][] trainFeatures, testFeatures;
        final int[] trainLabels, testLabels;
        TrainTestSplitResult(double[][] trX, double[][] teX, int[] trY, int[] teY) {
            this.trainFeatures = trX; this.testFeatures = teX; this.trainLabels = trY; this.testLabels = teY;
        }
    }
    private static class DataPreparationResult {
        final String[] originalTexts, processedTexts, labels;
        DataPreparationResult(String[] ot, String[] pt, String[] labels) {
            this.originalTexts = ot; this.processedTexts = pt; this.labels = labels;
        }
    }
    private static class ModelTrainingResult {
        final LinearSVM svmModel;
        final TrainTestSplitResult split;
        final int[] predictions01;     // {0,1}
        final String[] originalLabels;
        final Vocab vocab;
        ModelTrainingResult(LinearSVM svmModel, TrainTestSplitResult split, int[] preds01,
                            String[] originalLabels, Vocab vocab) {
            this.svmModel = svmModel; this.split = split; this.predictions01 = preds01;
            this.originalLabels = originalLabels; this.vocab = vocab;
        }
    }
}
