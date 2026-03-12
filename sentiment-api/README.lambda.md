# Lambda Deployment

The Lambda container uses the Java model built by `java-ml-project/Dockerfile`. Build in order:

```bash
# 1. Build the Java sentiment model (existing Dockerfile)
docker build -t sentiment-spark java-ml-project

# 2. Build the Lambda image (uses sentiment-spark as source)
docker build -f sentiment-api/Dockerfile.lambda -t sentiment-api-lambda .
```

The Lambda handler (`sentiment-api/lambda/index.js`) runs `SentimentPredictorApp` via Java directly—no Docker-in-Docker.
