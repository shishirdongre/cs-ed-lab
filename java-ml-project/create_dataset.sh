#!/bin/bash

echo "=== Creating Yelp Sentiment Dataset ==="
echo ""

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 is not installed. Please install Python 3 first."
    exit 1
fi

# Check if the dataset already exists
if [ -f "simple_yelp_reviews.csv" ]; then
    echo "📊 Dataset already exists: simple_yelp_reviews.csv"
    echo "   Size: $(du -h simple_yelp_reviews.csv | cut -f1)"
    echo "   Lines: $(wc -l < simple_yelp_reviews.csv)"
    echo ""
    read -p "Do you want to recreate it? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "✅ Keeping existing dataset"
        exit 0
    fi
fi

echo "🐍 Running Python script to create dataset..."
python3 create_simple_yelp_dataset.py

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Dataset created successfully!"
    echo "📊 File: simple_yelp_reviews.csv"
    echo "   Size: $(du -h simple_yelp_reviews.csv | cut -f1)"
    echo "   Lines: $(wc -l < simple_yelp_reviews.csv)"
    echo ""
    echo "📋 Dataset contains:"
    echo "   - 10,000 positive reviews"
    echo "   - 10,000 negative reviews"
    echo "   - Text and sentiment columns"
else
    echo "❌ Failed to create dataset"
    exit 1
fi