# Lambda Deployment

The Lambda container uses the Java model built by `java-ml-project/Dockerfile`. Build in order:

```bash
# 1. Build the Java sentiment model (existing Dockerfile)
docker build -t sentiment-spark java-ml-project

# 2. Build the Lambda image (uses sentiment-spark as source)
docker build -f sentiment-api/Dockerfile.lambda -t sentiment-api-lambda .
```

The Lambda handler (`sentiment-api/lambda/index.js`) runs `SentimentPredictorApp` via Java directly—no Docker-in-Docker.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /health | Health check |
| POST | /predict | Sentiment prediction |
| POST | /save-user | Save Research ID, create sheet if needed |
| POST | /reflect | Submit reflection rows to Google Sheet |

## Reflection → Google Sheets

Set Lambda env vars to enable reflection submissions:

- `GOOGLE_SHEET_ID` – Spreadsheet ID
- `GOOGLE_SERVICE_ACCOUNT_JSON` – Service account JSON (raw or base64)

If not set, `/save-user` and `/reflect` return 200 but do nothing.
