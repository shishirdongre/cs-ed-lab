#!/bin/bash

# Setup script for GitHub Codespace
echo "🚀 Setting up Java ML Project environment..."

# Ensure we're in the workspace directory
cd /workspace

# Activate Python virtual environment
source venv/bin/activate

# Install Python dependencies
echo "📦 Installing Python dependencies..."
pip install -r requirements.txt

# Verify Java installation
echo "☕ Verifying Java installation..."
java -version
javac -version

# Verify Python virtual environment
echo "🐍 Verifying Python virtual environment..."
which python
python --version

# Compile Java project
echo "🔨 Compiling Java project..."
if [ -f "YelpSentimentAnalysisSmileML.java" ]; then
    javac -cp "lib/*" YelpSentimentAnalysisSmileML.java
    echo "✅ Java compilation successful"
else
    echo "⚠️  YelpSentimentAnalysisSmileML.java not found"
fi

# Create convenience scripts
echo "📝 Creating convenience scripts..."

# Java run script
cat > run_java.sh << 'EOF'
#!/bin/bash
cd /workspace
java -cp ".:lib/*" YelpSentimentAnalysisSmileML
EOF
chmod +x run_java.sh

# Python script runner
cat > run_python.sh << 'EOF'
#!/bin/bash
cd /workspace
source venv/bin/activate
python "$@"
EOF
chmod +x run_python.sh

# Jupyter notebook launcher
cat > start_jupyter.sh << 'EOF'
#!/bin/bash
cd /workspace
source venv/bin/activate
jupyter notebook --ip=0.0.0.0 --port=8888 --no-browser --allow-root
EOF
chmod +x start_jupyter.sh

# Test scripts
cat > test_java.sh << 'EOF'
#!/bin/bash
cd /workspace
echo "☕ Testing Java Environment..."
javac -cp "lib/*" scripts/TestEnvironment.java
java -cp ".:lib/*" TestEnvironment
EOF
chmod +x test_java.sh

cat > test_python.sh << 'EOF'
#!/bin/bash
cd /workspace
echo "🐍 Testing Python Environment..."
source venv/bin/activate
python scripts/test_environment.py
EOF
chmod +x test_python.sh

echo "✅ Setup complete!"
echo ""
echo "Available commands:"
echo "  ./run_java.sh                    - Run the Java ML application"
echo "  ./run_python.sh script.py        - Run Python scripts with venv"
echo "  ./start_jupyter.sh               - Start Jupyter notebook server"
echo "  ./test_java.sh                   - Test Java environment"
echo "  ./test_python.sh                 - Test Python environment"
echo "  source venv/bin/activate         - Activate Python virtual environment"
echo ""
echo "Environment ready! 🎉"