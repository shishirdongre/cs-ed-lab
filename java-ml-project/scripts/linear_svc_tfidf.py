try:
    import matplotlib
    matplotlib.use('Agg')  # Use non-interactive backend for headless runs
    import matplotlib.pyplot as plt
    import seaborn as sns
    HAS_MPL = True
except Exception:
    HAS_MPL = False

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix, classification_report, accuracy_score
import pandas as pd
from pathlib import Path

# Resolve CSV path relative to this script (project root has simple_yelp_reviews.csv)
SCRIPT_DIR = Path(__file__).resolve().parent
CSV_PATH = (SCRIPT_DIR.parent / 'simple_yelp_reviews.csv')

# Load dataset
print(f"Loading dataset from: {CSV_PATH}")
df = pd.read_csv(CSV_PATH)
# Handle possible column names: expect 'label' or 'sentiment'
label_col = 'label' if 'label' in df.columns else ('sentiment' if 'sentiment' in df.columns else None)
if label_col is None:
    raise ValueError("CSV must contain a 'label' or 'sentiment' column")

df['label_bin'] = df[label_col].astype(str).str.lower().str.startswith('pos').astype(int)

X_train, X_test, y_train, y_test = train_test_split(
    df['text'], df['label_bin'], test_size=0.2, stratify=df['label_bin'], random_state=42
)

# TF-IDF + LinearSVC pipeline
vectorizer = TfidfVectorizer(ngram_range=(1, 2), min_df=2, stop_words='english')
X_train_tfidf = vectorizer.fit_transform(X_train)
X_test_tfidf = vectorizer.transform(X_test)

clf = LinearSVC()
clf.fit(X_train_tfidf, y_train)

y_pred = clf.predict(X_test_tfidf)

# Metrics
acc = accuracy_score(y_test, y_pred)
print("Accuracy:", acc)
print(classification_report(y_test, y_pred, target_names=["negative", "positive"]))

# Confusion Matrix (optional plot)
cm = confusion_matrix(y_test, y_pred)
if HAS_MPL:
    import matplotlib.pyplot as plt  # noqa: E402
    import seaborn as sns  # noqa: E402
    plt.figure(figsize=(5, 4))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=["neg", "pos"], yticklabels=["neg", "pos"]) 
    plt.xlabel("Predicted")
    plt.ylabel("Actual")
    plt.title("Confusion Matrix - LinearSVC")

    out_path = SCRIPT_DIR / 'linear_svc_confusion_matrix.png'
    plt.tight_layout()
    plt.savefig(out_path, dpi=150)
    print(f"Saved confusion matrix plot to: {out_path}")
else:
    print("Confusion matrix:\n", cm)
