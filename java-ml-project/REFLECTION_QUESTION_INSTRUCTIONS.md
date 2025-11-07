# Reflection Question Writing Instructions

## Purpose
This document outlines the criteria and guidelines for writing effective reflection questions for code-based learning activities. These questions help students think deeply about code, make connections, and develop understanding through inquiry rather than direct instruction.

## Core Principles

### 1. **No Leading Questions**
- ❌ **BAD**: "Does this code initialize Spark?" (gives away the answer)
- ✅ **GOOD**: "What do you think this code is doing?" (open-ended)
- ❌ **BAD**: "This code reads a file, right?" (assumes answer)
- ✅ **GOOD**: "What do you think happens when this code runs?" (exploratory)

### 2. **Open-Ended Exploration**
- Use phrases like:
  - "What do you think..."
  - "How do you think..."
  - "What do you notice..."
  - "What comes to mind..."
  - "How are you making sense of..."
- Avoid yes/no questions unless for confidence scales
- Encourage multiple valid interpretations

### 3. **Focus on Thinking Process, Not Just Answers**
- Ask about reasoning: "Why do you think..."
- Ask about connections: "How is this similar/different..."
- Ask about understanding: "How are you thinking about..."
- Ask about patterns: "What do you notice about..."

### 4. **Metacognitive Questions**
- Include confidence scales: "How confident do you feel..."
- Ask about prior knowledge: "What does this remind you of..."
- Ask about sense-making: "How are you making sense of..."
- Ask about familiarity: "What looks familiar or unfamiliar..."

### 5. **Comparative and Transfer Questions**
- Compare to prior experience: "How is this similar/different from..."
- Connect to other contexts: "What does this remind you of..."
- Explore patterns: "What do you notice about how..."

### 6. **Question Types and Structure**

#### Predict Questions (Before seeing explanation)
- "What do you think this code is doing?"
- "What do you think might happen when..."
- "What do you think [X] might be/mean?"

#### Reflect Questions (After seeing explanation)
- "How confident do you feel that you understood..."
- "How is this similar/different from..."
- "What do you notice about..."
- "What does this remind you of..."

#### Conceptual Transfer Questions
- "What do you notice about how [pattern]..."
- "How does this way of [doing something] feel to you?"
- "What comes to mind when you see..."

### 7. **Language and Tone**
- Use accessible language
- Avoid jargon unless it's the learning target
- Be encouraging: "Don't worry about understanding all the details"
- Frame as exploration, not testing
- Use "you" to make it personal

### 8. **Question Format Guidelines**

#### Title Format
- Clear, descriptive: "Question X.Y: [Topic]"
- Include sub-questions with letters: "Question 1.2a:", "Question 1.2b:"

#### Description Format
- Start with context: "Look at this code snippet:"
- Provide GitHub link: "View the code here: [link]"
- Then ask the question
- Remove response markers like "[Open text response]"
- Remove markdown formatting (no backticks, no bold)

#### Question Phrasing
- Start with the question directly
- Avoid preamble that gives hints
- Keep questions concise but clear
- Some questions can specify minimum length: "minimum 2 sentences"

### 9. **Question Sequence Pattern**

For each code section, typically include:
1. **Predict**: "What do you think this code is doing?" (paragraph)
2. **Explore**: Specific aspects (short_answer)
3. **Confidence**: Scale question (1-5)
4. **Compare**: Similar/different from prior experience (paragraph)
5. **Reflect**: Metacognitive question about understanding (paragraph)

### 10. **Avoid These Patterns**
- ❌ Leading questions that assume an answer
- ❌ Questions that test recall of facts
- ❌ Questions with obvious single answers
- ❌ Questions that use technical jargon students haven't learned
- ❌ Questions that give away the concept in the question itself
- ❌ Questions that are too vague: "What is this?" (needs context)

### 11. **Good Question Examples**

✅ **Predict Question:**
"What do you think this code is doing? (Don't worry about understanding all the details - just focus on the overall purpose)"

✅ **Exploratory Question:**
"What do you think the `.option()` calls are for?"

✅ **Confidence Question:**
"How confident do you feel that you understood what this code did?"
- Scale: 1 (Not confident) to 5 (Very confident)

✅ **Comparative Question:**
"How is this different from or similar to how you might read a file in other programming you've done?"

✅ **Metacognitive Question:**
"When you see these Spark imports, what comes to mind from your previous Java experience? How are you making sense of these imports?"

✅ **Pattern Recognition Question:**
"What do you notice about how these methods are called one after another? How does this way of writing code feel to you?"

### 12. **Question Grouping**
- Group related questions together
- Use "group" type for multi-part questions
- Each sub-question should be independent
- Questions should build on each other but not depend on previous answers

### 13. **Code Snippet Guidelines**
- Use GitHub links instead of inline code
- Format: "View the code here: [GitHub link with line numbers]"
- Provide context about where the code appears
- Keep snippets focused on specific concepts

### 14. **Instruction and Reveal Sections**
- **Instruction**: Brief, non-leading context that sets the stage
- **Reveal**: Explanation that comes after student reflection (not shown in form)
- Both should avoid giving away answers to reflection questions

## Checklist for Writing Reflection Questions

- [ ] Question is open-ended (not yes/no)
- [ ] Question doesn't lead to a specific answer
- [ ] Question encourages thinking and reasoning
- [ ] Question connects to prior knowledge or experience
- [ ] Question uses accessible language
- [ ] Question is clear and specific
- [ ] Question format follows guidelines
- [ ] Code reference uses GitHub link
- [ ] No markdown formatting in descriptions
- [ ] No response markers in descriptions

## Example Question Structure

```json
{
  "type": "paragraph",
  "title": "Question X.Y: What do you think this code is doing?",
  "description": "Look at this code snippet:\n\nView the code here: https://github.com/...\n\nWhat do you think this code is doing?"
}
```

## Work Pad for Notebook Questions

Use this section to draft questions before adding them to the notebook:

### Section: [Code Section Name]
- **Code Location**: [Lines X-Y]
- **Key Concepts**: [List concepts]
- **Questions to Add**:
  1. Predict: ...
  2. Explore: ...
  3. Confidence: ...
  4. Compare: ...
  5. Reflect: ...

---

## Notes
- Questions should feel like exploration, not assessment
- Multiple valid answers should be possible
- Questions should help students build understanding through reflection
- Focus on process and thinking, not just correct answers

---

## Work Pad for Notebook Questions

Use this section to draft questions before adding them to the notebook:

### Section: Data Loading
- **Code Location**: Load Yelp data cell
- **Key Concepts**: Understanding dataset structure, features, labels
- **Questions to Add**:
  1. Predict: "What do you notice about this dataset? What do you think the 'text' and 'sentiment' columns represent?"
  2. Explore: "How many features (columns) are there in this dataset?"
  3. Confidence: "How confident do you feel that you understand what this dataset contains?"
  4. Compare: "How is this dataset similar to or different from other data you've worked with?"
  5. Reflect: "What comes to mind when you see this dataset? How are you making sense of it?"

### Section: Data Cleaning
- **Code Location**: Clean dataset cell
- **Key Concepts**: Data preprocessing, normalization, label mapping
- **Questions to Add**:
  1. Predict: "What do you think this cleaning code is doing?"
  2. Explore: "Why do you think we need to normalize labels (e.g., 'pos' → 'positive')?"
  3. Confidence: "How confident do you feel that you understood what the cleaning code did?"
  4. Compare: "How is this data cleaning similar to or different from data processing you've done before?"
  5. Reflect: "What do you notice about the cleaning process? How does it feel to transform data this way?"

### Section: Train/Test Split
- **Code Location**: Train/test split cell
- **Key Concepts**: Data splitting, training vs testing, stratification
- **Questions to Add**:
  1. Predict: "What do you think this code is doing? Why do you think we split the data?"
  2. Explore: "What do you think 'stratify=y' means? Why might it be important?"
  3. Confidence: "How confident do you feel that you understood what the split does?"
  4. Compare: "How is splitting data for machine learning similar to or different from other ways you've divided data?"
  5. Reflect: "What do you think about using separate data for training and testing? How are you making sense of this approach?"

### Section: TF-IDF Vectorization
- **Code Location**: TF-IDF vectorization cells
- **Key Concepts**: Text to numbers, feature extraction, TF-IDF
- **Questions to Add**:
  1. Predict: "What do you think TF-IDF is doing? What do you think 'Term Frequency' and 'Inverse Document Frequency' might mean?"
  2. Explore: "What do you think happens when we convert text to numbers? Why do you think this is necessary?"
  3. Confidence: "How confident do you feel that you understood what TF-IDF does?"
  4. Compare: "How is converting text to numbers similar to or different from other data transformations you've seen?"
  5. Reflect: "What do you notice about the process of turning words into numbers? How does this way of representing text feel to you?"

### Section: Model Training
- **Code Location**: Fit classifiers cell
- **Key Concepts**: Model training, learning from data, LinearSVC
- **Questions to Add**:
  1. Predict: "What do you think happens when we call `.fit()`? What do you think the model is learning?"
  2. Explore: "What do you think a 'classifier' does? What do you think LinearSVC might stand for?"
  3. Confidence: "How confident do you feel that you understood what training does?"
  4. Compare: "How is training a model different from writing explicit rules (like if-else statements) to classify text?"
  5. Reflect: "What does 'training' or 'learning' from data remind you of? How are you thinking about what happens during training?"

### Section: Model Evaluation
- **Code Location**: Evaluation cell
- **Key Concepts**: Accuracy, precision, recall, confusion matrix
- **Questions to Add**:
  1. Predict: "What do you think these metrics (accuracy, precision, recall) are measuring?"
  2. Explore: "What do you think the confusion matrix shows? Why might it be useful?"
  3. Confidence: "How confident do you feel that you understood what these evaluation metrics mean?"
  4. Compare: "How is evaluating a machine learning model similar to or different from testing other programs you've written?"
  5. Reflect: "What do you notice about the evaluation results? How are you making sense of how well the model performed?"

### Section: Making Predictions
- **Code Location**: Predict your own review cell
- **Key Concepts**: Using trained model, making predictions on new data
- **Questions to Add**:
  1. Predict: "What do you think happens when you type a new review and click 'Predict'?"
  2. Explore: "How do you think the model uses what it learned to make a prediction on new text?"
  3. Confidence: "How confident do you feel that you understood how predictions are made?"
  4. Compare: "How is using a trained model to make predictions different from writing explicit rules?"
  5. Reflect: "What do you think about the model's predictions? How does it feel to see the model classify text you wrote?"

---

## Drafted Questions for Notebook

### After Data Loading:
- "What do you notice about this dataset? What do you think the 'text' and 'sentiment' columns represent?"
- "How many features (columns) are there in this dataset? What do you think 'features' means in machine learning?"
- "If you had to classify a review as positive or negative yourself, what words or phrases would you look for?"

### After Data Cleaning:
- "What do you think this cleaning code is doing?"
- "Why do you think we need to normalize labels and remove duplicates?"
- "How is this data cleaning similar to or different from data processing you've done before?"

### After Train/Test Split:
- "What do you think this code is doing? Why do you think we split the data into two groups?"
- "What do you think 'stratify=y' means? Why might it be important?"
- "Why do you think we don't use the same data for both training and testing?"

### After TF-IDF Vectorization:
- "What do you think TF-IDF is doing? What do you think 'Term Frequency' and 'Inverse Document Frequency' might mean?"
- "What do you think happens when we convert text to numbers? Why do you think this is necessary?"
- "How is converting text to numbers similar to or different from other data transformations you've seen?"

### After Model Training:
- "What do you think happens when we call `.fit()`? What do you think the model is learning?"
- "What do you think a 'classifier' does?"
- "How is training a model different from writing explicit rules to classify text?"

### After Model Evaluation:
- "What do you think these metrics (accuracy, precision, recall) are measuring?"
- "What do you think the confusion matrix shows? Why might it be useful?"
- "Why might accuracy alone not be enough to understand how well a model works?"

### After Making Predictions:
- "What do you think happens when you type a new review and click 'Predict'?"
- "How do you think the model uses what it learned to make a prediction on new text?"
- "What do you think about the model's predictions? How does it feel to see the model classify text you wrote?"
