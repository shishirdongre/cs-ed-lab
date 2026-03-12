const express = require('express');
const cors = require('cors');
const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 3000;

const JAVA_PROJECT_DIR = path.join(__dirname, '..', 'java-ml-project');
const OUTPUT_DIR = path.join(JAVA_PROJECT_DIR, 'output');
const DOCKER_IMAGE = 'sentiment-spark';

app.use(cors());
app.use(express.json());

app.post('/predict', async (req, res) => {
  const review = req.body?.review;

  if (!review || typeof review !== 'string') {
    return res.status(400).json({ error: 'Missing or invalid "review" field in request body' });
  }

  const outputFilename = `sentiment_${Date.now()}_${Math.random().toString(36).slice(2)}.json`;
  const outputFile = path.join(OUTPUT_DIR, outputFilename);

  // Ensure output dir exists
  if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR, { recursive: true });

  const child = spawn('docker', [
    'run', '--rm',
    '-v', `${OUTPUT_DIR}:/out`,
    DOCKER_IMAGE,
    review,
    `/out/${outputFilename}`,
  ], { stdio: ['pipe', 'pipe', 'pipe'] });

  const timeout = 120000; // 2 minutes for Spark training + prediction
  const timeoutId = setTimeout(() => {
    child.kill('SIGKILL');
  }, timeout);

  child.on('close', (code) => {
    clearTimeout(timeoutId);

    try {
      const data = fs.readFileSync(outputFile, 'utf8');
      fs.unlinkSync(outputFile);
      const json = JSON.parse(data);
      return res.json(json);
    } catch (err) {
      if (fs.existsSync(outputFile)) {
        try {
          const content = fs.readFileSync(outputFile, 'utf8');
          fs.unlinkSync(outputFile);
          return res.status(500).json(JSON.parse(content));
        } catch {
          fs.unlinkSync(outputFile);
        }
      }
      return res.status(500).json({
        error: 'Prediction failed',
        code,
        message: err.message,
      });
    }
  });

  child.on('error', (err) => {
    clearTimeout(timeoutId);
    if (fs.existsSync(outputFile)) fs.unlinkSync(outputFile);
    res.status(500).json({ error: 'Failed to run prediction', message: err.message });
  });
});

app.get('/health', (req, res) => {
  res.json({ status: 'ok' });
});

app.listen(PORT, () => {
  console.log(`Sentiment API running on http://localhost:${PORT}`);
  console.log('POST /predict with { "review": "your review text" }');
});
