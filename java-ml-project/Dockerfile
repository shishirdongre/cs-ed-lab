# Dockerfile for YelpSentimentAnalysis (Spark MLlib)
# Build: docker build -t sentiment-spark .
# Run:   docker run --rm sentiment-spark "Amazing pizza, friendly staff"

FROM eclipse-temurin:11-jdk AS builder

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Maven config and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy Spark pipeline sources and dataset
COPY src/main/java/com/example/ml ./src/main/java/com/example/ml
COPY simple_yelp_reviews.csv .

# Compile and copy dependencies
RUN mvn clean compile dependency:copy-dependencies -DskipTests

# Train model and save (runs full pipeline once, saves to saved_sentiment_model/)
RUN java -cp ".:target/classes:target/dependency/*" com.example.ml.YelpSentimentAnalysis

# Runtime stage
FROM eclipse-temurin:11-jre

WORKDIR /app

COPY --from=builder /app/target/classes ./target/classes
COPY --from=builder /app/target/dependency ./target/dependency
COPY --from=builder /app/simple_yelp_reviews.csv .
COPY --from=builder /app/saved_sentiment_model ./saved_sentiment_model

# Use SentimentPredictorApp: loads saved model, predicts (fast - no training)
# Usage: docker run --rm sentiment-spark "Great food" /out/result.json
#        docker run --rm -v /tmp:/out sentiment-spark "Great food" /out/result.json
ENTRYPOINT ["java", "-cp", ".:target/classes:target/dependency/*", "com.example.ml.SentimentPredictorApp"]
CMD []
