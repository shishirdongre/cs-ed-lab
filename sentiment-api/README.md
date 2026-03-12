# Sentiment API

Node.js backend that runs the Spark Java sentiment analysis and returns predictions via HTTP.

## Setup

1. Install Node dependencies: `npm install`
2. Ensure the Java project builds: `cd ../java-ml-project && mvn compile`
3. Ensure `run_sentiment.sh` is executable: `chmod +x ../java-ml-project/run_sentiment.sh`

## Run

```bash
npm start
```

Server runs on http://localhost:3000 (or `PORT` env var).

## API

### POST /predict

Request body:
```json
{ "review": "Great food and excellent service!" }
```

Response:
```json
{ "review": "Great food and excellent service!", "sentiment": "positive", "label": 1 }
```

### GET /health

Returns `{ "status": "ok" }`.

## Usage

```bash
curl -X POST http://localhost:3000/predict \
  -H "Content-Type: application/json" \
  -d '{"review": "Amazing pizza, friendly staff"}'
```
