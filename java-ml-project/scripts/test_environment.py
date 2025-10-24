#!/usr/bin/env python3
"""
Test script to verify the Python environment is working correctly.
This script should be run with the virtual environment activated.
"""

import sys
import os
import numpy as np
import pandas as pd
from sklearn.naive_bayes import GaussianNB
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report

def test_environment():
    """Test that all required packages are available and working."""
    print("🐍 Testing Python Environment")
    print("=" * 40)
    
    # Check Python version and virtual environment
    print(f"Python version: {sys.version}")
    print(f"Python executable: {sys.executable}")
    print(f"Virtual env active: {'venv' in sys.executable}")
    print()
    
    # Test numpy
    print("📊 Testing NumPy...")
    arr = np.array([1, 2, 3, 4, 5])
    print(f"NumPy array: {arr}")
    print(f"NumPy version: {np.__version__}")
    print()
    
    # Test pandas
    print("📈 Testing Pandas...")
    df = pd.DataFrame({'A': [1, 2, 3], 'B': [4, 5, 6]})
    print("Pandas DataFrame:")
    print(df)
    print(f"Pandas version: {pd.__version__}")
    print()
    
    # Test scikit-learn
    print("🤖 Testing Scikit-learn...")
    # Create sample data
    X = np.random.randn(100, 4)
    y = np.random.randint(0, 2, 100)
    
    # Split data
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    # Train model
    model = GaussianNB()
    model.fit(X_train, y_train)
    
    # Make predictions
    y_pred = model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    
    print(f"Sample dataset: {X.shape[0]} samples, {X.shape[1]} features")
    print(f"Model accuracy: {accuracy:.3f}")
    print(f"Scikit-learn version: {model.__class__.__module__}")
    print()
    
    # Test matplotlib (just import, don't plot)
    print("📊 Testing Matplotlib...")
    try:
        import matplotlib
        print(f"Matplotlib version: {matplotlib.__version__}")
        print("✅ Matplotlib imported successfully")
    except ImportError as e:
        print(f"❌ Matplotlib import failed: {e}")
    print()
    
    print("✅ All tests passed! Environment is ready.")
    print()
    print("Available commands:")
    print("  python test_environment.py    - Run this test")
    print("  ./run_python.sh script.py     - Run Python scripts")
    print("  ./start_jupyter.sh            - Start Jupyter notebook")

if __name__ == "__main__":
    test_environment()