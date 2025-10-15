#!/usr/bin/env python3
"""
Create a simple Yelp dataset for sentiment analysis
Downloads a subset of the Yelp review dataset and processes it
"""

import os
import pandas as pd
from datasets import load_dataset
import random

def create_simple_yelp_dataset():
    """Create a simple Yelp dataset with 1000 reviews"""
    
    print("📊 Creating simple Yelp dataset...")
    
    try:
        # Load the Yelp review dataset
        print("   Downloading Yelp dataset from Hugging Face...")
        dataset = load_dataset("yelp_review_full", split="train")
        
        # Convert to pandas DataFrame for easier manipulation
        df = pd.DataFrame(dataset)
        
        print(f"   Original dataset size: {len(df)} reviews")
        
        # Filter for shorter reviews (easier to process)
        df['text_length'] = df['text'].str.len()
        df_short = df[df['text_length'] <= 200].copy()
        
        print(f"   Reviews <= 200 characters: {len(df_short)}")
        
        # Sample equal numbers of positive and negative reviews
        positive_reviews = df_short[df_short['label'] == 1].sample(n=500, random_state=42)
        negative_reviews = df_short[df_short['label'] == 0].sample(n=500, random_state=42)
        
        # Combine and shuffle
        combined_df = pd.concat([positive_reviews, negative_reviews])
        combined_df = combined_df.sample(frac=1, random_state=42).reset_index(drop=True)
        
        # Clean the text data
        combined_df['text'] = combined_df['text'].str.replace('\n', ' ').str.replace('\r', ' ')
        combined_df['text'] = combined_df['text'].str.strip()
        
        # Create sentiment labels (0 = negative, 1 = positive)
        combined_df['sentiment'] = combined_df['label'].map({0: 'negative', 1: 'positive'})
        
        # Select only the columns we need
        final_df = combined_df[['text', 'sentiment']].copy()
        
        # Save to CSV
        output_file = 'simple_yelp_reviews.csv'
        final_df.to_csv(output_file, index=False)
        
        print(f"   ✅ Dataset created successfully!")
        print(f"   File: {output_file}")
        print(f"   Total reviews: {len(final_df)}")
        print(f"   Positive reviews: {len(final_df[final_df['sentiment'] == 'positive'])}")
        print(f"   Negative reviews: {len(final_df[final_df['sentiment'] == 'negative'])}")
        
        # Show sample reviews
        print("\n   Sample reviews:")
        for i, row in final_df.head(3).iterrows():
            print(f"   {i+1}. ({row['sentiment']}) {row['text'][:80]}...")
        
        return True
        
    except Exception as e:
        print(f"   ❌ Error creating dataset: {e}")
        return False

if __name__ == "__main__":
    success = create_simple_yelp_dataset()
    if success:
        print("\n🎉 Simple Yelp dataset creation completed!")
    else:
        print("\n💥 Failed to create dataset!")
        exit(1)