# Docker Setup for Yelp Sentiment Analysis

## Quick Start

### Option 1: Using Docker Compose (Recommended)
```bash
# Build and run the container
docker-compose up --build

# Run in detached mode
docker-compose up -d --build
```

### Option 2: Using Docker directly
```bash
# Build the image
docker build -t yelp-sentiment-analysis .

# Run the container
docker run -p 8080:8080 yelp-sentiment-analysis
```

## What's Fixed

### Java Version Compatibility
- **Problem**: JAR files were compiled with Java 21, but Dockerfile was using Java 11
- **Solution**: Updated Dockerfile to use `openjdk:21-jdk-slim` and `openjdk:21-jre-slim`

### Local JAR Dependencies
- Uses the local JAR files from the `lib/` directory
- No need to download dependencies during build
- Ensures consistent versions across environments

### Codespace Setup
If running in GitHub Codespaces, run the setup script first:
```bash
sudo ./setup_codespace.sh
```

## Features

- ✅ Java 21 compatibility with JAR files
- ✅ Python environment for data generation
- ✅ Pre-compiled Java classes
- ✅ Local JAR dependencies
- ✅ Automatic dataset generation
- ✅ Ready-to-run sentiment analysis

## Troubleshooting

### Java Version Issues
If you see "class file has wrong version" errors:
1. Ensure you're using Java 21
2. Run `java -version` to verify
3. In Codespaces, run `sudo ./setup_codespace.sh`

### Missing Dependencies
The Dockerfile uses local JAR files, so make sure the `lib/` directory contains all required JAR files.

## File Structure
```
java-ml-project/
├── Dockerfile              # Multi-stage build with Java 21
├── docker-compose.yml      # Easy deployment
├── setup_codespace.sh      # Codespace setup script
├── run_with_local_jars.sh  # Local execution script
├── lib/                    # JAR dependencies (Java 21)
└── YelpSentimentAnalysisSmileML.java
```