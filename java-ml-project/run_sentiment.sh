#!/bin/bash
# Wrapper script to run sentiment analysis on a single review from Node.js backend.
# Matches dev container Dockerfile config: java -cp with pre-built classes.
# Usage: ./run_sentiment.sh "review text" [output_file]

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Set JAVA_HOME if unset (dev container / Maven requirement)
if [ -z "$JAVA_HOME" ]; then
  JAVA_BIN=$(command -v java 2>/dev/null || true)
  if [ -n "$JAVA_BIN" ]; then
    JAVA_HOME=$(dirname "$(dirname "$(readlink -f "$JAVA_BIN" 2>/dev/null || echo "$JAVA_BIN")")")
    export JAVA_HOME
  fi
fi

REVIEW="${1:-}"
OUTPUT_FILE="${2:-sentiment_output.json}"

if [ -z "$REVIEW" ]; then
  echo '{"error":"Missing review argument"}' > "$OUTPUT_FILE"
  exit 1
fi

# Build if needed (same as Dockerfile: mvn clean compile dependency:copy-dependencies)
if [ ! -d "target/dependency" ] || [ ! -d "target/classes" ]; then
  mvn -q clean compile dependency:copy-dependencies -DskipTests 2>/dev/null || {
    echo "{\"error\":\"Build failed\"}" > "$OUTPUT_FILE"
    exit 1
  }
fi

# Run with java -cp (Dockerfile pattern: no Maven at runtime)
CLASSPATH=".:target/classes:target/dependency/*"
java -cp "$CLASSPATH" com.example.ml.YelpSentimentAnalysis "$REVIEW" "$OUTPUT_FILE" 2>/dev/null || {
  echo "{\"error\":\"Prediction failed\"}" > "$OUTPUT_FILE"
  exit 1
}
