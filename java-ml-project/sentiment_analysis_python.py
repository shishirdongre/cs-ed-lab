#!/usr/bin/env python3
"""
Multinomial Naive Bayes Sentiment Analysis
Clean Python implementation using standard libraries
"""

import csv
import re
import math
import random
from collections import Counter, defaultdict
from typing import List, Tuple, Dict, Set
import numpy as np


class MultinomialNaiveBayes:
    """Multinomial Naive Bayes classifier with Laplace smoothing"""
    
    def __init__(self, alpha: float = 1.0):
        self.alpha = alpha  # Laplace smoothing parameter
        self.vocab: Set[str] = set()
        self.class_counts: Dict[int, int] = {}
        self.word_counts: Dict[int, Dict[str, int]] = {}
        self.class_priors: Dict[int, float] = {}
        self.word_probs: Dict[int, Dict[str, float]] = {}
        self.classes: List[int] = []
        
    def _preprocess_text(self, text: str) -> List[str]:
        """Clean and tokenize text"""
        # Convert to lowercase and remove non-alphabetic characters
        text = re.sub(r'[^a-zA-Z\s]', ' ', text.lower())
        # Split into words and filter out empty strings
        words = [word for word in text.split() if len(word) > 2]
        return words
    
    def _build_vocabulary(self, texts: List[str]) -> Set[str]:
        """Build vocabulary from training texts"""
        vocab = set()
        for text in texts:
            words = self._preprocess_text(text)
            vocab.update(words)
        return vocab
    
    def _get_word_counts(self, text: str) -> Counter:
        """Get word counts for a text"""
        words = self._preprocess_text(text)
        return Counter(words)
    
    def fit(self, X: List[str], y: List[int]) -> None:
        """Train the classifier"""
        print(f"Training on {len(X)} samples...")
        
        # Build vocabulary from training data
        self.vocab = self._build_vocabulary(X)
        print(f"Vocabulary size: {len(self.vocab)}")
        
        # Get unique classes
        self.classes = sorted(list(set(y)))
        
        # Initialize counts
        self.class_counts = {cls: 0 for cls in self.classes}
        self.word_counts = {cls: defaultdict(int) for cls in self.classes}
        
        # Count words per class
        for text, label in zip(X, y):
            self.class_counts[label] += 1
            word_counts = self._get_word_counts(text)
            
            for word, count in word_counts.items():
                if word in self.vocab:  # Only count words in vocabulary
                    self.word_counts[label][word] += count
        
        # Calculate class priors
        total_samples = len(X)
        for cls in self.classes:
            self.class_priors[cls] = self.class_counts[cls] / total_samples
        
        # Calculate word probabilities with Laplace smoothing
        self.word_probs = {cls: {} for cls in self.classes}
        
        for cls in self.classes:
            total_words_in_class = sum(self.word_counts[cls].values())
            vocab_size = len(self.vocab)
            
            for word in self.vocab:
                word_count = self.word_counts[cls][word]
                # Laplace smoothing: (count + alpha) / (total + alpha * vocab_size)
                prob = (word_count + self.alpha) / (total_words_in_class + self.alpha * vocab_size)
                self.word_probs[cls][word] = prob
        
        print(f"Training completed. Classes: {self.classes}")
        print(f"Class distribution: {self.class_counts}")
    
    def _calculate_log_probability(self, text: str, cls: int) -> float:
        """Calculate log probability of text belonging to class"""
        word_counts = self._get_word_counts(text)
        log_prob = math.log(self.class_priors[cls])  # Prior probability
        
        for word, count in word_counts.items():
            if word in self.vocab:
                word_prob = self.word_probs[cls][word]
                # Add log probability for each occurrence of the word
                log_prob += count * math.log(word_prob)
        
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
    """Calculate accuracy, precision, recall, F1-score"""
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
    
    return {
        'accuracy': accuracy,
        'precision': precision,
        'recall': recall,
        'f1': f1,
        'confusion_matrix': {'tp': tp, 'tn': tn, 'fp': fp, 'fn': fn}
    }


def main():
    """Main function"""
    print("=== Python Multinomial Naive Bayes Sentiment Analysis ===")
    
    # Load data
    print("Loading data...")
    texts, labels = load_data('simple_yelp_reviews.csv')
    print(f"Loaded {len(texts)} samples")
    print(f"Positive: {sum(labels)}, Negative: {len(labels) - sum(labels)}")
    
    # Split data
    print("\nSplitting data...")
    X_train, X_test, y_train, y_test = train_test_split(texts, labels, test_size=0.2, random_state=42)
    print(f"Training: {len(X_train)} samples")
    print(f"Testing: {len(X_test)} samples")
    
    # Train model
    print("\nTraining Multinomial Naive Bayes...")
    model = MultinomialNaiveBayes(alpha=1.0)
    model.fit(X_train, y_train)
    
    # Make predictions
    print("\nMaking predictions...")
    y_pred = model.predict(X_test)
    
    # Calculate metrics
    metrics = calculate_metrics(y_test, y_pred)
    
    print(f"\n=== Results ===")
    print(f"Accuracy: {metrics['accuracy']:.3f}")
    print(f"Precision: {metrics['precision']:.3f}")
    print(f"Recall: {metrics['recall']:.3f}")
    print(f"F1-Score: {metrics['f1']:.3f}")
    
    cm = metrics['confusion_matrix']
    print(f"\nConfusion Matrix:")
    print(f"True\\Pred    Negative    Positive")
    print(f"Negative     {cm['tn']:8d}    {cm['fp']:8d}")
    print(f"Positive     {cm['fn']:8d}    {cm['tp']:8d}")
    
    # Test sample reviews
    print(f"\n=== Sample Predictions ===")
    sample_reviews = [
        "Great food, excellent service!",
        "Terrible food, bad service",
        "Amazing pizza, friendly staff",
        "This place is absolutely horrible",
        "Love this restaurant, will come back"
    ]
    
    for review in sample_reviews:
        prediction = model.predict_single(review)
        sentiment = "positive" if prediction == 1 else "negative"
        print(f"'{review}' -> {sentiment}")
    
    print("\n✅ Analysis completed!")


if __name__ == "__main__":
    main()