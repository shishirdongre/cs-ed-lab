#!/usr/bin/env python3
"""
Parse GOOGLE_FORM_QUESTIONS.md and create individual JSON files for each question
in the questions/ directory, numbered for ordering.
"""

import re
import json
from pathlib import Path

def parse_markdown_to_questions(md_file):
    """Parse markdown file and extract all questions and sections."""
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    items = []
    lines = content.split('\n')
    i = 0
    current_question = None
    
    while i < len(lines):
        line = lines[i].strip()
        
        # Section headers
        if line.startswith('## ') and not line.startswith('###'):
            section_title = line.replace('## ', '').strip()
            items.append({
                'type': 'section',
                'title': section_title
            })
        
        # Question headers (### Question X.X: or **Question X:**)
        elif line.startswith('### Question'):
            # Extract question number and title
            match = re.match(r'### Question (\d+(?:\.\d+[a-z]?)?):\s*(.+)', line)
            if match:
                question_num = match.group(1)
                question_title = match.group(2)
                
                # Look for code section and link
                code_section = None
                code_link = None
                if i + 1 < len(lines) and '**Code Section:**' in lines[i + 1]:
                    code_section = lines[i + 1].replace('**Code Section:**', '').strip()
                if i + 2 < len(lines) and '**Code Link:**' in lines[i + 2]:
                    code_link = lines[i + 2].replace('**Code Link:**', '').strip().strip('`')
                
                current_question = {
                    'number': question_num,
                    'title': question_title,
                    'code_section': code_section,
                    'code_link': code_link,
                    'type': None,
                    'predict': [],
                    'instruction': None,
                    'reveal': None,
                    'reflect': [],
                    'options': [],
                    'scale_min': None,
                    'scale_max': None,
                    'scale_labels': {}
                }
        
        # Pre-survey questions (format: **Question X:**)
        elif line.startswith('**Question') and ':**' in line and not line.startswith('###'):
            match = re.match(r'\*\*Question (\d+):\*\*\s*(.+)', line)
            if match:
                question_num = match.group(1)
                question_title = match.group(2)
                current_question = {
                    'number': question_num,
                    'title': question_title,
                    'type': None,
                    'options': [],
                    'scale_min': None,
                    'scale_max': None,
                    'scale_labels': {}
                }
        
        # Parse question content
        elif current_question:
            # Predict section
            if line.startswith('**Predict:**'):
                i += 1
                while i < len(lines) and not lines[i].strip().startswith('**'):
                    if lines[i].strip():
                        current_question['predict'].append(lines[i].strip())
                    i += 1
                i -= 1
            
            # Instruction section
            elif line.startswith('**Instruction:**'):
                i += 1
                instruction_lines = []
                while i < len(lines) and not lines[i].strip().startswith('**'):
                    if lines[i].strip():
                        instruction_lines.append(lines[i].strip())
                    i += 1
                current_question['instruction'] = '\n'.join(instruction_lines)
                i -= 1
            
            # Reveal section
            elif line.startswith('**Reveal:**'):
                i += 1
                reveal_lines = []
                while i < len(lines) and not lines[i].strip().startswith('**'):
                    if lines[i].strip():
                        reveal_lines.append(lines[i].strip())
                    i += 1
                current_question['reveal'] = '\n'.join(reveal_lines)
                i -= 1
            
            # Reflect section
            elif line.startswith('**Reflect:**'):
                i += 1
                while i < len(lines):
                    reflect_line = lines[i].strip()
                    if reflect_line.startswith('**On a scale'):
                        # Extract scale question
                        scale_match = re.search(r'(\d+).*?(\d+)', reflect_line)
                        if scale_match:
                            current_question['scale_min'] = int(scale_match.group(1))
                            current_question['scale_max'] = int(scale_match.group(2))
                            current_question['type'] = 'scale'
                            # Extract labels
                            if 'not confident' in reflect_line.lower():
                                current_question['scale_labels']['low'] = 'Not confident'
                            if 'very confident' in reflect_line.lower():
                                current_question['scale_labels']['high'] = 'Very confident'
                            if 'not interested' in reflect_line.lower():
                                current_question['scale_labels']['low'] = 'Not interested'
                            if 'very interested' in reflect_line.lower():
                                current_question['scale_labels']['high'] = 'Very interested'
                        current_question['reflect'].append(reflect_line)
                    elif reflect_line.startswith('**') and not reflect_line.startswith('**Reflect'):
                        break
                    elif reflect_line and not reflect_line.startswith('---'):
                        current_question['reflect'].append(reflect_line)
                    i += 1
                i -= 1
            
            # Options (for multiple choice/checkbox)
            elif line.startswith('- [ ]'):
                option_text = line.replace('- [ ]', '').strip()
                current_question['options'].append(option_text)
                if not current_question['type']:
                    # Check if it's checkbox (has "Check all that apply" in title)
                    if 'check all' in current_question.get('title', '').lower():
                        current_question['type'] = 'checkbox'
                    else:
                        current_question['type'] = 'multiple_choice'
            
            # Open text response
            elif '[Open text response' in line:
                if not current_question['type']:
                    # Check if it says "minimum X sentences"
                    if 'minimum' in line.lower():
                        current_question['type'] = 'paragraph'
                    else:
                        current_question['type'] = 'short_answer'
            
            # Check if question is complete (next question or section)
            elif (line.startswith('### Question') or line.startswith('## ') or 
                  (line.startswith('**Question') and ':**' in line)):
                if current_question.get('type'):
                    items.append(current_question)
                    current_question = None
                i -= 1  # Process this line in next iteration
                break
        
        i += 1
    
    # Add last question if exists
    if current_question and current_question.get('type'):
        items.append(current_question)
    
    return items

def write_question_files(items, output_dir):
    """Write each question/section to a separate JSON file."""
    output_path = Path(output_dir)
    output_path.mkdir(exist_ok=True)
    
    for idx, item in enumerate(items, start=1):
        filename = f"{idx:03d}_{item.get('number', 'section').replace('.', '_').replace(' ', '_')}.json"
        filepath = output_path / filename
        
        # Clean up the item for JSON
        json_item = {}
        for key, value in item.items():
            if value is not None and value != []:
                if key == 'predict' and isinstance(value, list):
                    json_item[key] = '\n'.join(value) if value else ''
                else:
                    json_item[key] = value
        
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(json_item, f, indent=2, ensure_ascii=False)
        
        print(f"Created: {filename}")

if __name__ == '__main__':
    md_file = 'GOOGLE_FORM_QUESTIONS.md'
    output_dir = 'questions'
    
    print(f"Parsing {md_file}...")
    items = parse_markdown_to_questions(md_file)
    print(f"Found {len(items)} items")
    
    print(f"\nWriting to {output_dir}/...")
    write_question_files(items, output_dir)
    print(f"\nDone! Created {len(items)} files in {output_dir}/")
