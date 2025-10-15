# Multi-stage Dockerfile for Yelp Sentiment Analysis
# Stage 1: Build environment
FROM openjdk:11-jdk-slim as builder

# Install system dependencies
RUN apt-get update && apt-get install -y \
    maven \
    python3 \
    python3-pip \
    python3-venv \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .

# Download Maven dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY . .

# Create Python virtual environment and install dependencies
RUN python3 -m venv venv
RUN . venv/bin/activate && pip install --no-cache-dir -r requirements.txt

# Create dataset (try real dataset first, fallback to synthetic)
RUN (. venv/bin/activate && python3 create_simple_yelp_dataset.py) || python3 create_simple_yelp_dataset_fallback.py

# Compile Java project and copy dependencies
RUN mvn clean compile dependency:copy-dependencies

# Stage 2: Runtime environment
FROM openjdk:11-jre-slim

# Install Python runtime
RUN apt-get update && apt-get install -y \
    python3 \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy compiled classes and dependencies from builder stage
COPY --from=builder /app/target/classes ./target/classes
COPY --from=builder /app/target/dependency ./target/dependency
COPY --from=builder /app/simple_yelp_reviews.csv .

# Copy Python virtual environment
COPY --from=builder /app/venv ./venv

# Copy source files for reference
COPY --from=builder /app/YelpSentimentAnalysisSmileML.java .

# Create a simple run script
RUN echo '#!/bin/bash\n\
echo "=== Yelp Sentiment Analysis Docker Container ==="\n\
echo\n\
echo "🔍 Checking environment..."\n\
java -version\n\
echo\n\
echo "📊 Dataset available:"\n\
ls -la *.csv\n\
echo\n\
echo "🚀 Running sentiment analysis..."\n\
echo "=================================="\n\
\n\
# Get classpath\n\
CLASSPATH=".:target/classes:target/dependency/*"\n\
\n\
# Run the analysis\n\
java -cp "$CLASSPATH" YelpSentimentAnalysisSmileML\n\
\n\
echo\n\
echo "=================================="\n\
echo "✅ Analysis completed!"\n\
' > run_analysis.sh && chmod +x run_analysis.sh

# Expose port for potential web interface
EXPOSE 8080

# Set default command
CMD ["./run_analysis.sh"]