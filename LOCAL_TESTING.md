# Local Testing

## Quick start

```bash
# Terminal 1: Start the API (sentiment + reflection)
cd sentiment-api
cp .env.example .env   # optional: add GOOGLE_SHEET_ID, GOOGLE_SERVICE_ACCOUNT_JSON for Sheets
npm install
npm run dev

# Terminal 2: Start the frontend
cd java-ml-project/viewer
# .env should have VITE_SENTIMENT_API_URL=http://localhost:3000
npm install
npm run dev
```

Open http://localhost:5173 (Vite default).

## Prerequisites

- **Sentiment**: Docker + `sentiment-spark` image. Build with:
  ```bash
  docker build -t sentiment-spark java-ml-project
  ```
- **Reflection (optional)**: Fill `sentiment-api/.env` with `GOOGLE_SHEET_ID` and `GOOGLE_SERVICE_ACCOUNT_JSON` to test Sheets. Without them, save-user and reflect return 200 but do nothing.

## Using deployed API instead

Set `VITE_SENTIMENT_API_URL` in `java-ml-project/viewer/.env` to your Lambda URL:
```
VITE_SENTIMENT_API_URL=https://zi8srtikb0.execute-api.us-west-1.amazonaws.com
```

Then run only the viewer: `cd java-ml-project/viewer && npm run dev`
