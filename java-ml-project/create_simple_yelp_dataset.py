#!/usr/bin/env python3
"""
Create a simpler Yelp dataset with shorter reviews for Java Naive Bayes
"""

from datasets import load_dataset
import pandas as pd
import csv
import random

def create_simple_yelp_dataset():
    print("Loading Yelp dataset...")
    
    # Load the dataset
    dataset = load_dataset("Yelp/yelp_review_full")
    
    # Convert to pandas
    train_df = dataset['train'].to_pandas()
    
    # Convert 5-star ratings to binary sentiment
    def convert_to_binary_sentiment(rating):
        if rating <= 2:
            return "negative"
        else:
            return "positive"
    
    train_df['sentiment'] = train_df['label'].apply(convert_to_binary_sentiment)
    
    # Filter for shorter reviews (less than 200 characters)
    train_df['text_length'] = train_df['text'].str.len()
    short_reviews = train_df[train_df['text_length'] <= 200].copy()
    
    print(f"Found {len(short_reviews)} short reviews")
    
    # Sample balanced dataset
    pos_reviews = short_reviews[short_reviews['sentiment'] == 'positive'].sample(n=5000, random_state=42)
    neg_reviews = short_reviews[short_reviews['sentiment'] == 'negative'].sample(n=5000, random_state=42)
    
    # Combine and shuffle
    simple_dataset = pd.concat([pos_reviews, neg_reviews]).sample(frac=1, random_state=42).reset_index(drop=True)
    
    print(f"Created simple dataset with {len(simple_dataset)} reviews")
    print(f"Positive: {len(simple_dataset[simple_dataset['sentiment'] == 'positive'])}")
    print(f"Negative: {len(simple_dataset[simple_dataset['sentiment'] == 'negative'])}")
    
    # Clean text - replace ALL newlines, carriage returns, and multiple spaces with single spaces
    import re
    simple_dataset['text'] = simple_dataset['text'].apply(lambda x: re.sub(r'[\n\r]+', ' ', str(x)))
    simple_dataset['text'] = simple_dataset['text'].apply(lambda x: re.sub(r'[ \t]+', ' ', str(x)))
    simple_dataset['text'] = simple_dataset['text'].str.strip()
    
    # Replace quotes to avoid CSV parsing issues
    simple_dataset['text'] = simple_dataset['text'].str.replace('"', "'")
    
    # Save as CSV
    simple_dataset[['text', 'sentiment']].to_csv('simple_yelp_reviews.csv', index=False)
    
    print("\nSample reviews:")
    for i in range(5):
        print(f"{i+1}. ({simple_dataset.iloc[i]['sentiment']}) {simple_dataset.iloc[i]['text']}")
    
    print(f"\n✅ Simple dataset saved as 'simple_yelp_reviews.csv'")

if __name__ == "__main__":
    create_simple_yelp_dataset()