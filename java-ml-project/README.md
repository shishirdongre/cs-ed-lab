# Java Machine Learning Project

A sentiment analysis project using Naive Bayes classification on Yelp reviews, implemented in Java with the Smile library and supporting Python analysis tools.

## 🚀 Quick Start with GitHub Codespaces

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new)

1. **Click the badge above** or go to "Code" → "Codespaces" → "Create codespace on main"
2. **Wait for container build** (2-3 minutes)
3. **Test environment**: 
   - Java: `./test_java.sh`
   - Python: `./test_python.sh`

## 📁 Project Structure

```
java-ml-project/
├── .devcontainer/              # GitHub Codespace configuration
│   ├── devcontainer.json      # VS Code settings
│   └── docker-compose.yml     # Container orchestration
├── lib/                       # Java dependencies (JAR files)
│   ├── smile-core-3.0.1.jar  # Machine learning library
│   ├── smile-base-3.0.1.jar  # Statistical distributions
│   └── opencsv-5.7.1.jar     # CSV file handling
├── scripts/                   # Testing and utility scripts
│   ├── TestEnvironment.java   # Java environment test
│   └── test_environment.py    # Python environment test
├── venv/                      # Python virtual environment
├── *.java                     # Java source files
├── *.py                       # Python scripts
├── requirements.txt           # Python dependencies
└── README_Codespace.md        # Detailed Codespace documentation
```

## 🛠️ Environment Setup

### Prerequisites
- GitHub account
- Access to GitHub Codespaces (free tier available)

### Local Development (Alternative)
```bash
# Clone repository
git clone <repository-url>
cd java-ml-project

# Setup Python virtual environment
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt

# Compile Java
javac -cp "lib/*" *.java

# Run
java -cp ".:lib/*" YelpSentimentAnalysisSmileML
```

## ☕ Java Development

### Compilation and Execution
```bash
# Compile all Java files
javac -cp "lib/*" *.java

# Run main application
java -cp ".:lib/*" YelpSentimentAnalysisSmileML

# Run test
./test_java.sh

# Using convenience script
./run_java.sh
```

### Key Java Files
- `YelpSentimentAnalysisSmileML.java` - Main sentiment analysis application
- `scripts/TestEnvironment.java` - Environment verification script
- `Workshop_Teaching_Outline.md` - Comprehensive teaching guide

## 🐍 Python Development

### Virtual Environment
The Python virtual environment is automatically activated in Codespaces.

```bash
# Check if venv is active
which python

# Install new packages
pip install package_name

# Run Python scripts
python script.py
# or
./run_python.sh script.py

# Start Jupyter notebook
./start_jupyter.sh
```

### Python Scripts
- `scripts/test_environment.py` - Environment verification
- `create_simple_yelp_dataset.py` - Dataset creation utility

## 📊 Machine Learning Pipeline

### Data Processing
1. **Load CSV data** using OpenCSV library
2. **Text preprocessing** (lowercase, punctuation removal, normalization)
3. **Feature extraction** (bag-of-words with hashing)
4. **Label encoding** (string to integer conversion)

### Model Training
1. **Train-test split** (80/20 with random seed)
2. **Naive Bayes training** using Smile library
3. **Gaussian distribution fitting** for each feature
4. **Prior probability calculation**

### Evaluation
- **Accuracy**: Overall classification accuracy
- **Confusion Matrix**: True vs predicted labels
- **Precision, Recall, F1-Score**: Detailed performance metrics

## 🎓 Educational Content

### Workshop Materials
- **Complete teaching outline** in `Workshop_Teaching_Outline.md`
- **Design patterns** explanation (DTO pattern, container classes)
- **Step-by-step implementation** guide
- **Progressive exercises** (beginner to advanced)

### Key Learning Objectives
- Java machine learning implementation
- External library integration (Smile)
- Text preprocessing and feature engineering
- Model evaluation and metrics
- Software engineering practices in ML

## 🔧 Troubleshooting

### Common Issues

#### Java Compilation Errors
```bash
# Check classpath
echo $CLASSPATH

# Verify JAR files
ls -la lib/

# Recompile
javac -cp "lib/*" *.java
```

#### Python Environment Issues
```bash
# Check virtual environment
which python
python --version

# Reactivate environment
source venv/bin/activate

# Reinstall dependencies
pip install -r requirements.txt
```

#### Codespace Issues
- **Container rebuild**: Delete and recreate codespace
- **Port forwarding**: Check VS Code port panel
- **Extensions**: Verify Java and Python extensions are installed

## 📚 Dependencies

### Java Dependencies
- **OpenJDK 11**: Java runtime and compiler
- **Smile Core 3.0.1**: Machine learning algorithms
- **Smile Base 3.0.1**: Statistical distributions
- **OpenCSV 5.7.1**: CSV file handling
- **Apache Commons Lang3**: String utilities

### Python Dependencies
- **NumPy**: Numerical computing
- **Pandas**: Data manipulation
- **Scikit-learn**: Machine learning algorithms
- **Matplotlib**: Data visualization
- **Jupyter**: Interactive notebooks

## 🚀 Getting Started

1. **Open in Codespace** using the badge above
3. **Test environment**: 
   - `./test_java.sh`
   - `python test_environment.py`
4. **Run main application**: `./run_java.sh`
5. **Explore the code** and follow the teaching outline

## 📖 Additional Resources

- [Smile Library Documentation](https://haifengl.github.io/smile/)
- [OpenCSV Documentation](http://opencsv.sourceforge.net/)
- [Java Machine Learning Tutorial](https://www.baeldung.com/java-machine-learning)
- [GitHub Codespaces Documentation](https://docs.github.com/en/codespaces)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is for educational purposes. Please check individual library licenses for commercial use.