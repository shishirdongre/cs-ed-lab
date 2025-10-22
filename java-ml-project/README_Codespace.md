# GitHub Codespace Setup

This repository is configured to work with GitHub Codespaces for Java and Python development.

## Quick Start

1. **Open in Codespace**: Click the "Code" button and select "Codespaces" → "Create codespace on main"
2. **Wait for setup**: The container will build automatically (takes 2-3 minutes)

## Environment Details

### Java Setup
- **Java Version**: OpenJDK 11
- **Location**: `/usr/lib/jvm/java-11-openjdk-amd64`
- **Compilation**: `javac -cp "lib/*" YelpSentimentAnalysisSmileML.java`
- **Execution**: `java -cp ".:lib/*" YelpSentimentAnalysisSmileML`

### Python Setup
- **Python Version**: 3.x
- **Virtual Environment**: `/workspace/venv` (always activated)
- **Activation**: `source venv/bin/activate` (already done in terminal)
- **Dependencies**: See `requirements.txt`

## Available Commands

### Java
```bash
# Compile
javac -cp "lib/*" YelpSentimentAnalysisSmileML.java

# Run
./run_java.sh
# or
java -cp ".:lib/*" YelpSentimentAnalysisSmileML

# Test
./test_java.sh
```

### Python
```bash
# Run Python scripts (venv auto-activated)
./run_python.sh script.py

# Test environment
./test_python.sh

# Interactive Python
python

# Install packages
pip install package_name

# Jupyter Notebook
./start_jupyter.sh
```

## Project Structure
```
/workspace/
├── .devcontainer/          # Codespace configuration
├── lib/                    # Java JAR files
├── scripts/                # Testing and utility scripts
│   ├── TestEnvironment.java
│   └── test_environment.py
├── venv/                   # Python virtual environment
├── *.java                  # Java source files
├── *.py                    # Python scripts
├── requirements.txt        # Python dependencies
└── run_*.sh               # Convenience scripts
```

## Features

- ✅ **Java 11** with OpenJDK
- ✅ **Python 3** with virtual environment
- ✅ **VS Code Extensions** for Java and Python
- ✅ **Jupyter Notebook** support
- ✅ **Auto-activation** of Python venv
- ✅ **Port forwarding** for web services
- ✅ **Git integration**

## Troubleshooting

### Java Issues
```bash
# Check Java version
java -version
javac -version

# Check classpath
echo $CLASSPATH
```

### Python Issues
```bash
# Check Python and venv
which python
python --version

# Reinstall dependencies
pip install -r requirements.txt
```

### Container Issues
- **Rebuild**: Delete codespace and create new one
- **Logs**: Check Codespace logs in GitHub interface
- **Restart**: Use "Rebuild Container" in VS Code command palette

## Development Workflow

1. **Edit code** in VS Code
2. **Compile Java**: `javac -cp "lib/*" *.java`
3. **Run Java**: `./run_java.sh`
4. **Run Python**: `./run_python.sh script.py`
5. **Jupyter**: `./start_jupyter.sh` (opens in browser)

## Notes

- Python virtual environment is **always active** in the terminal
- All Java dependencies are in the `lib/` folder
- The workspace is mounted at `/workspace`
- Port 8888 is forwarded for Jupyter notebooks
- Port 8080 is available for Java web applications