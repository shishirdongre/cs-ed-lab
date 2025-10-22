# Scripts Directory

This directory contains testing and utility scripts for the Java ML project.

## Available Scripts

### Java Testing
- **`TestEnvironment.java`** - Comprehensive Java environment test
  - Tests Java version and basic functionality
  - Verifies Smile library integration
  - Checks file system access and JAR files
  - Usage: `./test_java.sh` or `java -cp ".:lib/*" TestEnvironment`

### Python Testing
- **`test_environment.py`** - Python environment verification
  - Tests Python version and virtual environment
  - Verifies all required packages (numpy, pandas, scikit-learn, etc.)
  - Runs sample machine learning operations
  - Usage: `./test_python.sh` or `python test_environment.py`

## Running the Tests

### From Project Root
```bash
# Test Java environment
./test_java.sh

# Test Python environment
./test_python.sh
```

### Direct Execution
```bash
# Java test
cd /workspace
javac -cp "lib/*" scripts/TestEnvironment.java
java -cp ".:lib/*" TestEnvironment

# Python test
cd /workspace
source venv/bin/activate
python scripts/test_environment.py
```

## What the Tests Check

### Java Test (`TestEnvironment.java`)
- ✅ Java version and vendor information
- ✅ Basic Java operations (arrays, strings)
- ✅ Smile library class loading
- ✅ Gaussian distribution fitting
- ✅ File system access (lib/ directory, JAR files)
- ✅ Main application file presence

### Python Test (`test_environment.py`)
- ✅ Python version and virtual environment
- ✅ NumPy array operations
- ✅ Pandas DataFrame creation
- ✅ Scikit-learn model training and prediction
- ✅ Matplotlib import verification
- ✅ Package version information

## Troubleshooting

If tests fail:
1. **Java issues**: Check classpath and JAR files in `lib/`
2. **Python issues**: Verify virtual environment activation
3. **Permission issues**: Ensure scripts are executable (`chmod +x`)
4. **Path issues**: Run from project root directory

## Adding New Scripts

When adding new utility scripts:
1. Place them in this `scripts/` directory
2. Make them executable: `chmod +x script_name`
3. Update this README with usage instructions
4. Consider adding convenience scripts in project root