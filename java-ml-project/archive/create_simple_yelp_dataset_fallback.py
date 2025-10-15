#!/usr/bin/env python3
"""
Fallback script to create a simple Yelp dataset without external dependencies
Creates synthetic restaurant review data for sentiment analysis
"""

import csv
import random

def create_synthetic_reviews():
    """Create synthetic restaurant reviews for testing"""
    
    print("📊 Creating synthetic Yelp dataset...")
    
    # Sample positive reviews
    positive_reviews = [
        "Amazing food and great service! Will definitely come back.",
        "Best pizza in town, friendly staff and cozy atmosphere.",
        "Excellent quality, fresh ingredients, highly recommended.",
        "Outstanding service, delicious food, perfect for date night.",
        "Love this place! Great ambiance and wonderful staff.",
        "Fantastic experience, food was incredible and service was top-notch.",
        "Absolutely delicious! Best restaurant in the area.",
        "Wonderful atmosphere, amazing food, excellent value.",
        "Perfect meal, great service, will be back soon.",
        "Incredible food quality, friendly staff, great location.",
        "Outstanding restaurant, highly recommend to everyone.",
        "Best dining experience, food was perfect and service excellent.",
        "Amazing flavors, great presentation, wonderful staff.",
        "Excellent food, cozy atmosphere, perfect for families.",
        "Fantastic restaurant, great food and outstanding service.",
        "Love the food here, always fresh and delicious.",
        "Best place in town, amazing food and great staff.",
        "Perfect evening, delicious food and excellent service.",
        "Wonderful restaurant, highly recommend to all.",
        "Outstanding quality, great atmosphere, excellent value.",
        "Amazing experience, food was incredible and service perfect.",
        "Best meal ever, great staff and wonderful atmosphere.",
        "Excellent restaurant, delicious food and friendly service.",
        "Fantastic place, highly recommend to everyone.",
        "Perfect food, great service, wonderful experience.",
        "Love this restaurant, always amazing food and service.",
        "Best in the area, excellent quality and great staff.",
        "Outstanding meal, perfect atmosphere and wonderful service.",
        "Amazing food, great location, highly recommended.",
        "Excellent experience, delicious food and friendly staff.",
        "Fantastic restaurant, best food in town.",
        "Perfect evening, amazing food and excellent service.",
        "Wonderful place, great food and outstanding staff.",
        "Best dining experience, highly recommend to all.",
        "Outstanding quality, great service and wonderful atmosphere.",
        "Amazing restaurant, perfect food and excellent staff.",
        "Excellent food, cozy atmosphere, great value.",
        "Fantastic experience, delicious meal and wonderful service.",
        "Perfect place, amazing food and friendly staff.",
        "Love this restaurant, always excellent food and service.",
        "Best in town, outstanding quality and great atmosphere.",
        "Wonderful meal, perfect service and excellent food.",
        "Amazing place, highly recommend to everyone.",
        "Excellent restaurant, great food and wonderful staff.",
        "Fantastic food, perfect atmosphere and outstanding service.",
        "Best experience, delicious food and excellent staff.",
        "Outstanding restaurant, amazing food and great service.",
        "Perfect meal, wonderful staff and excellent atmosphere.",
        "Love this place, always fantastic food and service.",
        "Best restaurant, outstanding quality and great staff."
    ]
    
    # Sample negative reviews
    negative_reviews = [
        "Terrible food and awful service. Never coming back.",
        "Worst restaurant experience, cold food and rude staff.",
        "Disgusting food, overpriced and terrible service.",
        "Horrible experience, food was terrible and staff was rude.",
        "Waste of money, food was cold and service was slow.",
        "Terrible quality, bad atmosphere and poor service.",
        "Worst meal ever, disgusting food and awful staff.",
        "Horrible restaurant, never recommend to anyone.",
        "Terrible experience, food was inedible and service was bad.",
        "Worst place in town, disgusting food and rude staff.",
        "Horrible food, terrible service, waste of time.",
        "Disgusting experience, food was awful and staff was rude.",
        "Terrible restaurant, never coming back again.",
        "Worst food ever, bad service and horrible atmosphere.",
        "Disgusting meal, terrible staff and awful experience.",
        "Horrible place, food was inedible and service was bad.",
        "Terrible quality, worst restaurant experience ever.",
        "Disgusting food, rude staff and horrible atmosphere.",
        "Worst service, terrible food and awful experience.",
        "Horrible restaurant, disgusting food and bad staff.",
        "Terrible meal, worst place in town.",
        "Disgusting experience, awful food and terrible service.",
        "Horrible food, bad atmosphere and poor service.",
        "Worst restaurant, terrible quality and rude staff.",
        "Disgusting place, awful food and horrible service.",
        "Terrible experience, worst meal ever.",
        "Horrible food, disgusting quality and bad service.",
        "Worst service, terrible food and awful atmosphere.",
        "Disgusting restaurant, horrible food and rude staff.",
        "Terrible place, worst food and bad service.",
        "Horrible meal, disgusting food and awful experience.",
        "Worst food, terrible service and horrible atmosphere.",
        "Disgusting experience, awful food and bad staff.",
        "Terrible restaurant, horrible quality and rude service.",
        "Worst place, disgusting food and terrible staff.",
        "Horrible food, awful service and bad atmosphere.",
        "Disgusting meal, worst restaurant experience.",
        "Terrible quality, horrible food and awful service.",
        "Worst food, disgusting experience and bad staff.",
        "Horrible restaurant, terrible food and rude service.",
        "Disgusting place, awful food and horrible atmosphere.",
        "Terrible meal, worst service and bad experience.",
        "Horrible food, disgusting quality and awful staff.",
        "Worst restaurant, terrible food and bad service.",
        "Disgusting experience, horrible food and rude staff.",
        "Terrible place, awful food and bad atmosphere.",
        "Horrible meal, worst food and terrible service.",
        "Disgusting food, horrible experience and bad staff.",
        "Worst service, terrible food and awful atmosphere.",
        "Horrible restaurant, disgusting food and bad quality."
    ]
    
    # Create the dataset
    reviews = []
    
    # Add positive reviews
    for i, review in enumerate(positive_reviews):
        reviews.append({
            'text': review,
            'sentiment': 'positive'
        })
    
    # Add negative reviews
    for i, review in enumerate(negative_reviews):
        reviews.append({
            'text': review,
            'sentiment': 'negative'
        })
    
    # Shuffle the reviews
    random.shuffle(reviews)
    
    # Write to CSV
    output_file = 'simple_yelp_reviews.csv'
    with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['text', 'sentiment']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(reviews)
    
    print(f"   ✅ Synthetic dataset created successfully!")
    print(f"   File: {output_file}")
    print(f"   Total reviews: {len(reviews)}")
    print(f"   Positive reviews: {len([r for r in reviews if r['sentiment'] == 'positive'])}")
    print(f"   Negative reviews: {len([r for r in reviews if r['sentiment'] == 'negative'])}")
    
    # Show sample reviews
    print("\n   Sample reviews:")
    for i, review in enumerate(reviews[:3]):
        print(f"   {i+1}. ({review['sentiment']}) {review['text'][:80]}...")
    
    return True

if __name__ == "__main__":
    success = create_synthetic_reviews()
    if success:
        print("\n🎉 Synthetic Yelp dataset creation completed!")
    else:
        print("\n💥 Failed to create dataset!")
        exit(1)