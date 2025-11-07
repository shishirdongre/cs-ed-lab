#!/usr/bin/env python3
"""Remove all outputs from Jupyter notebook files."""

import json
import sys
from pathlib import Path

def remove_outputs(notebook_path):
    """Remove all outputs from a Jupyter notebook while keeping cell structure."""
    print(f"Processing: {notebook_path}")
    
    # Read the notebook
    with open(notebook_path, 'r', encoding='utf-8') as f:
        notebook = json.load(f)
    
    # Count outputs before removal
    total_outputs = 0
    cells_with_outputs = 0
    
    # Remove outputs from all cells
    for cell in notebook.get('cells', []):
        if 'outputs' in cell:
            if cell['outputs']:
                total_outputs += len(cell['outputs'])
                cells_with_outputs += 1
            cell['outputs'] = []
        
        # Also remove execution_count if present
        if 'execution_count' in cell:
            cell['execution_count'] = None
    
    # Remove metadata outputs if present
    if 'metadata' in notebook:
        if 'execution' in notebook['metadata']:
            notebook['metadata']['execution'] = {}
    
    # Write the cleaned notebook
    with open(notebook_path, 'w', encoding='utf-8') as f:
        json.dump(notebook, f, indent=1, ensure_ascii=False)
    
    print(f"  Removed {total_outputs} outputs from {cells_with_outputs} cells")
    return total_outputs, cells_with_outputs

if __name__ == '__main__':
    notebooks = [
        'colab/Yelp_Sentiment_Workshop.ipynb',
        'colab/workshop_python.ipynb'
    ]
    
    for notebook_path in notebooks:
        if Path(notebook_path).exists():
            remove_outputs(notebook_path)
        else:
            print(f"Warning: {notebook_path} not found")
    
    print("\nDone! All outputs removed from notebooks.")
