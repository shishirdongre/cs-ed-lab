#!/usr/bin/env python3
"""
Yelp Dataset Processor
Downloads and processes the real Yelp review dataset from Hugging Face.
Creates a clean CSV file for sentiment analysis.
"""

import csv
import os
from datasets import load_dataset

def create_yelp_dataset(num_reviews=1000, output_file="simple_yelp_reviews.csv"):
    """
    Download and process the real Yelp review dataset.
    
    Args:
        num_reviews (int): Number of reviews to sample from the dataset
        output_file (str): Output CSV file path
    """
    
    print("Loading Yelp dataset from Hugging Face...")
    try:
        # Load the dataset
        ds = load_dataset("Yelp/yelp_review_full")
        
        # Get the training split
        train_data = ds['train']
        
        print(f"Dataset loaded successfully. Total reviews: {len(train_data)}")
        
        # Sample reviews (stratified by label)
        reviews = []
        
        # Get reviews by label
        label_0 = [item for item in train_data if item['label'] == 0]  # 1-star
        label_1 = [item for item in train_data if item['label'] == 1]  # 2-star  
        label_2 = [item for item in train_data if item['label'] == 2]  # 3-star
        label_3 = [item for item in train_data if item['label'] == 3]  # 4-star
        label_4 = [item for item in train_data if item['label'] == 4]  # 5-star
        
        # Sample from each label group
        samples_per_label = num_reviews // 5
        remaining = num_reviews % 5
        
        for i, label_group in enumerate([label_0, label_1, label_2, label_3, label_4]):
            sample_size = samples_per_label + (1 if i < remaining else 0)
            
            # Sample from this label group
            sampled = label_group[:sample_size]
            
            for item in sampled:
                # Clean the text (remove newlines, extra spaces)
                text = item['text'].replace('\n', ' ').replace('\r', ' ').strip()
                # Remove extra spaces
                text = ' '.join(text.split())
                
                # Skip very short or very long reviews
                if len(text) < 10 or len(text) > 500:
                    continue
                
                # Convert 5-star rating to sentiment (1-2 stars = negative, 3 stars = neutral, 4-5 stars = positive)
                if item['label'] <= 1:
                    sentiment = 0  # Negative
                elif item['label'] == 2:
                    sentiment = 2  # Neutral
                else:
                    sentiment = 1  # Positive
                
                reviews.append({
                    'review_id': f"yelp_{item['label']}_{len(reviews)}",
                    'business': f"Business_{item['label']}_{len(reviews)}",
                    'text': text,
                    'sentiment': sentiment
                })
        
        print(f"Processed {len(reviews)} reviews")
        
        # Write to CSV
        with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
            fieldnames = ['review_id', 'business', 'text', 'sentiment']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            
            writer.writeheader()
            for review in reviews:
                writer.writerow(review)
        
        print(f"Dataset saved to: {output_file}")
        
        # Print statistics
        pos_count = sum(1 for r in reviews if r['sentiment'] == 1)
        neg_count = sum(1 for r in reviews if r['sentiment'] == 0)
        neu_count = sum(1 for r in reviews if r['sentiment'] == 2)
        
        print(f"Sentiment distribution:")
        print(f"  Positive: {pos_count} ({pos_count/len(reviews)*100:.1f}%)")
        print(f"  Negative: {neg_count} ({neg_count/len(reviews)*100:.1f}%)")
        print(f"  Neutral:  {neu_count} ({neu_count/len(reviews)*100:.1f}%)")
        
    except Exception as e:
        print(f"Error loading dataset: {e}")
        print("Make sure you have the 'datasets' library installed:")
        print("pip install datasets")
        raise

if __name__ == "__main__":
    # Create dataset
    create_yelp_dataset()