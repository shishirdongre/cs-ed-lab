#!/usr/bin/env python3
"""
Multinomial Naive Bayes Sentiment Analysis with TF-IDF
Python implementation with parity to Spark ML Java code
"""

import csv
import re
import math
import random
import hashlib
from collections import Counter, defaultdict
from typing import List, Tuple, Dict, Set
import numpy as np


class TextPreprocessor:
    """Text preprocessing pipeline similar to Spark ML"""
    
    # Common English stop words (similar to Spark's StopWordsRemover)
    STOP_WORDS = {
        'a', 'an', 'and', 'are', 'as', 'at', 'be', 'by', 'for', 'from', 'has', 'he', 'in', 'is', 'it', 'its',
        'of', 'on', 'that', 'the', 'to', 'was', 'will', 'with', 'i', 'you', 'we', 'they', 'she', 'him', 'her',
        'his', 'their', 'them', 'this', 'these', 'those', 'have', 'had', 'has', 'having', 'do', 'does', 'did',
        'doing', 'can', 'could', 'should', 'would', 'may', 'might', 'must', 'shall', 'will', 'am', 'are', 'is',
        'was', 'were', 'been', 'being', 'or', 'but', 'if', 'then', 'else', 'when', 'where', 'why', 'how', 'all',
        'any', 'both', 'each', 'few', 'more', 'most', 'other', 'some', 'such', 'no', 'nor', 'not', 'only', 'own',
        'same', 'so', 'than', 'too', 'very', 'just', 'now', 'here', 'there', 'up', 'down', 'out', 'off', 'over',
        'under', 'again', 'further', 'then', 'once'
    }
    
    @staticmethod
    def tokenize(text: str) -> List[str]:
        """Tokenize text into words (similar to Spark's Tokenizer)"""
        if not text:
            return []
        
        # Convert to lowercase and remove non-alphabetic characters
        text = re.sub(r'[^a-zA-Z\s]', ' ', text.lower())
        # Split into words and filter out empty strings
        words = [word for word in text.split() if len(word) > 2]
        return words
    
    @staticmethod
    def remove_stop_words(words: List[str]) -> List[str]:
        """Remove stop words (similar to Spark's StopWordsRemover)"""
        return [word for word in words if word not in TextPreprocessor.STOP_WORDS]


class HashingTF:
    """HashingTF implementation similar to Spark ML's HashingTF"""
    
    def __init__(self, num_features: int = 10000):
        self.num_features = num_features
    
    def transform(self, documents: List[List[str]]) -> np.ndarray:
        """Transform documents to feature vectors using hashing"""
        features = np.zeros((len(documents), self.num_features))
        
        for i, doc in enumerate(documents):
            for word in doc:
                # Hash the word to get feature index
                feature_idx = self._hash_word(word) % self.num_features
                features[i, feature_idx] += 1
        
        return features
    
    def _hash_word(self, word: str) -> int:
        """Hash word to integer (similar to Spark's hashing)"""
        return int(hashlib.md5(word.encode('utf-8')).hexdigest(), 16)


class IDF:
    """IDF implementation similar to Spark ML's IDF"""
    
    def __init__(self):
        self.idf_scores = None
        self.num_docs = 0
    
    def fit(self, features: np.ndarray) -> 'IDF':
        """Calculate IDF scores from raw features"""
        self.num_docs = features.shape[0]
        num_features = features.shape[1]
        
        # Count documents containing each feature
        doc_counts = np.sum(features > 0, axis=0)
        
        # Calculate IDF: log(num_docs / doc_count) + 1 (add 1 to avoid log(0))
        self.idf_scores = np.log(self.num_docs / (doc_counts + 1)) + 1
        
        return self
    
    def transform(self, features: np.ndarray) -> np.ndarray:
        """Apply IDF weighting to features"""
        if self.idf_scores is None:
            raise ValueError("IDF must be fitted before transform")
        
        return features * self.idf_scores


class MultinomialNaiveBayes:
    """Multinomial Naive Bayes classifier with TF-IDF features (parity with Spark ML)"""
    
    def __init__(self, alpha: float = 1.0, num_features: int = 10000):
        self.alpha = alpha  # Laplace smoothing parameter
        self.num_features = num_features
        self.class_counts: Dict[int, int] = {}
        self.class_priors: Dict[int, float] = {}
        self.feature_probs: Dict[int, np.ndarray] = {}
        self.classes: List[int] = []
        self.hashing_tf = HashingTF(num_features)
        self.idf = IDF()
        
    def _preprocess_text(self, text: str) -> List[str]:
        """Preprocess text using the same pipeline as Spark ML"""
        # Tokenize
        words = TextPreprocessor.tokenize(text)
        # Remove stop words
        words = TextPreprocessor.remove_stop_words(words)
        return words
    
    def fit(self, X: List[str], y: List[int]) -> None:
        """Train the classifier with TF-IDF features (similar to Spark ML pipeline)"""
        print(f"Training on {len(X)} samples...")
        
        # Preprocess texts (tokenize + remove stop words)
        processed_texts = [self._preprocess_text(text) for text in X]
        print(f"Text preprocessing completed")
        
        # Transform to raw features using HashingTF
        raw_features = self.hashing_tf.transform(processed_texts)
        print(f"Raw features shape: {raw_features.shape}")
        
        # Apply IDF transformation
        tfidf_features = self.idf.fit(raw_features).transform(raw_features)
        print(f"TF-IDF features shape: {tfidf_features.shape}")
        
        # Get unique classes
        self.classes = sorted(list(set(y)))
        
        # Initialize counts
        self.class_counts = {cls: 0 for cls in self.classes}
        self.feature_probs = {cls: np.zeros(self.num_features) for cls in self.classes}
        
        # Count features per class
        for i, (features, label) in enumerate(zip(tfidf_features, y)):
            self.class_counts[label] += 1
            self.feature_probs[label] += features
        
        # Calculate class priors
        total_samples = len(X)
        for cls in self.classes:
            self.class_priors[cls] = self.class_counts[cls] / total_samples
        
        # Calculate feature probabilities with Laplace smoothing
        for cls in self.classes:
            total_features_in_class = np.sum(self.feature_probs[cls])
            # Laplace smoothing: (count + alpha) / (total + alpha * num_features)
            self.feature_probs[cls] = (self.feature_probs[cls] + self.alpha) / \
                                    (total_features_in_class + self.alpha * self.num_features)
        
        print(f"Training completed. Classes: {self.classes}")
        print(f"Class distribution: {self.class_counts}")
        print(f"Feature space size: {self.num_features}")
    
    def _calculate_log_probability(self, text: str, cls: int) -> float:
        """Calculate log probability of text belonging to class using TF-IDF features"""
        # Preprocess text
        processed_words = self._preprocess_text(text)
        
        # Transform to raw features using HashingTF
        raw_features = self.hashing_tf.transform([processed_words])[0]
        
        # Apply IDF transformation
        tfidf_features = self.idf.transform(raw_features.reshape(1, -1))[0]
        
        # Calculate log probability
        log_prob = math.log(self.class_priors[cls])  # Prior probability
        
        # Add log probability for each feature
        for i, feature_value in enumerate(tfidf_features):
            if feature_value > 0:  # Only consider non-zero features
                feature_prob = self.feature_probs[cls][i]
                log_prob += feature_value * math.log(feature_prob)
        
        return log_prob
    
    def predict(self, X: List[str]) -> List[int]:
        """Predict classes for texts"""
        predictions = []
        
        for text in X:
            class_probs = {}
            for cls in self.classes:
                class_probs[cls] = self._calculate_log_probability(text, cls)
            
            # Return class with highest log probability
            predicted_class = max(class_probs, key=class_probs.get)
            predictions.append(predicted_class)
        
        return predictions
    
    def predict_single(self, text: str) -> int:
        """Predict class for a single text"""
        return self.predict([text])[0]


def load_data(filename: str) -> Tuple[List[str], List[int]]:
    """Load data from CSV file"""
    texts = []
    labels = []
    
    with open(filename, 'r', encoding='utf-8') as file:
        reader = csv.reader(file)
        next(reader)  # Skip header
        
        for row in reader:
            if len(row) >= 2:
                texts.append(row[0])
                labels.append(1 if row[1].strip().lower().startswith('pos') else 0)
    
    return texts, labels


def train_test_split(X: List[str], y: List[int], test_size: float = 0.2, random_state: int = 42) -> Tuple[List[str], List[str], List[int], List[int]]:
    """Stratified train-test split"""
    random.seed(random_state)
    
    # Separate by class
    pos_indices = [i for i, label in enumerate(y) if label == 1]
    neg_indices = [i for i, label in enumerate(y) if label == 0]
    
    # Shuffle indices
    random.shuffle(pos_indices)
    random.shuffle(neg_indices)
    
    # Calculate split sizes
    pos_test_size = int(len(pos_indices) * test_size)
    neg_test_size = int(len(neg_indices) * test_size)
    
    # Split indices
    pos_test = pos_indices[:pos_test_size]
    pos_train = pos_indices[pos_test_size:]
    neg_test = neg_indices[:neg_test_size]
    neg_train = neg_indices[neg_test_size:]
    
    # Combine and sort
    train_indices = sorted(pos_train + neg_train)
    test_indices = sorted(pos_test + neg_test)
    
    # Create splits
    X_train = [X[i] for i in train_indices]
    X_test = [X[i] for i in test_indices]
    y_train = [y[i] for i in train_indices]
    y_test = [y[i] for i in test_indices]
    
    return X_train, X_test, y_train, y_test


def calculate_metrics(y_true: List[int], y_pred: List[int]) -> Dict[str, float]:
    """Calculate accuracy, precision, recall, F1-score (matching Spark ML output)"""
    # Confusion matrix
    tp = sum(1 for true, pred in zip(y_true, y_pred) if true == 1 and pred == 1)
    tn = sum(1 for true, pred in zip(y_true, y_pred) if true == 0 and pred == 0)
    fp = sum(1 for true, pred in zip(y_true, y_pred) if true == 0 and pred == 1)
    fn = sum(1 for true, pred in zip(y_true, y_pred) if true == 1 and pred == 0)
    
    # Calculate metrics
    accuracy = (tp + tn) / (tp + tn + fp + fn)
    precision = tp / (tp + fp) if (tp + fp) > 0 else 0
    recall = tp / (tp + fn) if (tp + fn) > 0 else 0
    f1 = 2 * (precision * recall) / (precision + recall) if (precision + recall) > 0 else 0
    
    # Calculate additional metrics like Spark ML
    sensitivity = tp / (tp + fn) if (tp + fn) > 0 else 0
    specificity = tn / (tn + fp) if (tn + fp) > 0 else 0
    
    return {
        'accuracy': accuracy,
        'precision': precision,
        'recall': recall,
        'f1': f1,
        'sensitivity': sensitivity,
        'specificity': specificity,
        'confusion_matrix': {'tp': tp, 'tn': tn, 'fp': fp, 'fn': fn}
    }


def main():
    """Main function with enhanced output matching Spark ML format"""
    print("\n" + "="*70)
    print("🤖 PYTHON SENTIMENT ANALYSIS WITH TF-IDF (SPARK ML PARITY)")
    print("   Multinomial Naive Bayes Text Classification")
    print("="*70)
    
    # Load data
    print("\n📊 DATA LOADING & PREPARATION")
    print("-"*40)
    print("Loading dataset from: simple_yelp_reviews.csv")
    texts, labels = load_data('simple_yelp_reviews.csv')
    print(f"✅ Loaded {len(texts)} samples")
    
    # Show class distribution
    pos_count = sum(labels)
    neg_count = len(labels) - pos_count
    print(f"\n📈 CLASS DISTRIBUTION:")
    print(f"   Positive: {pos_count}")
    print(f"   Negative: {neg_count}")
    
    # Split data
    print("\n✂️  DATA SPLITTING")
    print("-"*40)
    X_train, X_test, y_train, y_test = train_test_split(texts, labels, test_size=0.2, random_state=42)
    train_count = len(X_train)
    test_count = len(X_test)
    total_count = len(texts)
    print(f"Training samples: {train_count} ({train_count/total_count*100:.1f}%)")
    print(f"Test samples:    {test_count} ({test_count/total_count*100:.1f}%)")
    
    # Build ML pipeline
    print("\n🔧 MACHINE LEARNING PIPELINE")
    print("-"*40)
    print("Building pipeline with:")
    print("  • TextPreprocessor (text → words)")
    print("  • StopWordsRemover (remove common words)")
    print("  • HashingTF (words → features)")
    print("  • IDF (inverse document frequency)")
    print("  • Multinomial Naive Bayes (classifier)")
    
    # Train model
    print("\n🚀 MODEL TRAINING")
    print("-"*40)
    print("Training Multinomial Naive Bayes classifier...")
    import time
    start_time = time.time()
    model = MultinomialNaiveBayes(alpha=1.0, num_features=10000)
    model.fit(X_train, y_train)
    training_time = time.time() - start_time
    print(f"✅ Training completed in {training_time:.2f} seconds")
    
    # Make predictions
    print("\n🔮 MAKING PREDICTIONS")
    print("-"*40)
    print("Running predictions on test set...")
    pred_start_time = time.time()
    y_pred = model.predict(X_test)
    pred_time = time.time() - pred_start_time
    print(f"✅ Predictions completed in {pred_time:.2f} seconds")
    
    # Calculate metrics
    metrics = calculate_metrics(y_test, y_pred)
    
    # Display results in Spark ML format
    print("\n" + "="*60)
    print("📊 MODEL EVALUATION RESULTS")
    print("="*60)
    
    print("\n🎯 CLASSIFICATION METRICS:")
    print("┌─────────────────┬─────────────┬─────────────┐")
    print("│ Metric          │ Value       │ Percentage  │")
    print("├─────────────────┼─────────────┼─────────────┤")
    print(f"│ Accuracy        │ {metrics['accuracy']:<11.4f} │ {metrics['accuracy']*100:<11.2f}% │")
    print(f"│ Precision       │ {metrics['precision']:<11.4f} │ {metrics['precision']*100:<11.2f}% │")
    print(f"│ Recall          │ {metrics['recall']:<11.4f} │ {metrics['recall']*100:<11.2f}% │")
    print(f"│ F1-Score        │ {metrics['f1']:<11.4f} │ {metrics['f1']*100:<11.2f}% │")
    print("└─────────────────┴─────────────┴─────────────┘")
    
    # Performance interpretation
    print("\n📈 PERFORMANCE INTERPRETATION:")
    if metrics['accuracy'] >= 0.8:
        print("   🟢 EXCELLENT: Model performs very well!")
    elif metrics['accuracy'] >= 0.7:
        print("   🟡 GOOD: Model performs reasonably well")
    elif metrics['accuracy'] >= 0.6:
        print("   🟠 FAIR: Model needs improvement")
    else:
        print("   🔴 POOR: Model needs significant improvement")
    
    # Confusion Matrix
    cm = metrics['confusion_matrix']
    print("\n🔢 CONFUSION MATRIX:")
    print("   (Rows = Actual, Columns = Predicted)")
    print("   ┌─────────────┬─────────────┬─────────────┐")
    print("   │             │ Negative    │ Positive    │")
    print("   ├─────────────┼─────────────┼─────────────┤")
    print(f"   │ Negative     │ {cm['tn']:<11d} │ {cm['fp']:<11d} │")
    print(f"   │ Positive     │ {cm['fn']:<11d} │ {cm['tp']:<11d} │")
    print("   └─────────────┴─────────────┴─────────────┘")
    
    print("\n📋 DETAILED METRICS:")
    print(f"   • True Positives:  {cm['tp']}")
    print(f"   • True Negatives:  {cm['tn']}")
    print(f"   • False Positives: {cm['fp']}")
    print(f"   • False Negatives: {cm['fn']}")
    print(f"   • Sensitivity:     {metrics['sensitivity']:.4f} ({metrics['sensitivity']*100:.2f}%)")
    print(f"   • Specificity:     {metrics['specificity']:.4f} ({metrics['specificity']*100:.2f}%)")
    
    print("\n" + "="*60)
    
    # Test sample reviews
    print("\n🧪 SAMPLE PREDICTIONS")
    print("-"*40)
    sample_reviews = [
        ("Great food, excellent service!", "positive"),
        ("Terrible food, bad service", "negative"),
        ("Amazing pizza, friendly staff", "positive"),
        ("This place is absolutely horrible", "negative"),
        ("Love this restaurant, will come back", "positive"),
        ("Worst experience ever", "negative"),
        ("Outstanding quality and service", "positive"),
        ("Complete waste of money", "negative"),
        ("Perfect ambiance and delicious food", "positive"),
        ("Overpriced and disappointing", "negative")
    ]
    
    print("Testing model on sample reviews:\n")
    print("┌─────┬─────────────────────────────────────┬──────────┬──────────┐")
    print("│ #   │ Review Text                         │ Expected │ Predicted│")
    print("├─────┼─────────────────────────────────────┼──────────┼──────────┤")
    
    correct = 0
    for i, (review, expected) in enumerate(sample_reviews):
        prediction = model.predict_single(review)
        predicted = "positive" if prediction == 1 else "negative"
        status = "✅" if expected == predicted else "❌"
        if expected == predicted:
            correct += 1
        
        # Truncate long reviews for table display
        display_review = review[:35] + "..." if len(review) > 35 else review
        print(f"│ {i+1:<3d} │ {display_review:<35s} │ {expected:<8s} │ {predicted:<8s} │ {status}")
    
    print("└─────┴─────────────────────────────────────┴──────────┴──────────┘")
    print(f"\n📊 Sample Accuracy: {correct}/{len(sample_reviews)} ({correct/len(sample_reviews)*100:.1f}%)")
    
    print("\n" + "="*70)
    print("🎉 ANALYSIS COMPLETED SUCCESSFULLY!")
    print("="*70)


if __name__ == "__main__":
    main()