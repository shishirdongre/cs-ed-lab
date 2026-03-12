/**
 * AWS Lambda handler for sentiment API.
 * Runs Java SentimentPredictorApp directly (no Docker).
 */

const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');

const APP_DIR = '/var/task';
const JAVA_CP = `${APP_DIR}:${APP_DIR}/target/classes:${APP_DIR}/target/dependency/*`;

function runPrediction(review) {
  return new Promise((resolve, reject) => {
    const outputFile = `/tmp/sentiment_${Date.now()}_${Math.random().toString(36).slice(2)}.json`;

    console.log('[DEBUG] About to spawn Java', { cwd: APP_DIR, outputFile, reviewLen: review.length });
    console.log('[DEBUG] Checking APP_DIR exists:', fs.existsSync(APP_DIR));
    console.log('[DEBUG] Checking target/classes:', fs.existsSync(`${APP_DIR}/target/classes`));
    console.log('[DEBUG] Checking saved_sentiment_model:', fs.existsSync(`${APP_DIR}/saved_sentiment_model`));

    const child = spawn('java', [
      '-Xmx1500m',
      '-DAWS_LAMBDA=1',
      '-Djava.io.tmpdir=/tmp',
      '-cp', JAVA_CP,
      'SentimentPredictorApp',
      review,
      outputFile,
    ], {
      cwd: APP_DIR,
      stdio: ['pipe', 'pipe', 'pipe'],
      env: { ...process.env, AWS_LAMBDA_FUNCTION_NAME: process.env.AWS_LAMBDA_FUNCTION_NAME || 'java-workshop-sentiment-api' },
    });

    console.log('[DEBUG] Java process spawned, pid:', child.pid);

    let stdout = '';
    let stderr = '';
    child.stdout.on('data', (chunk) => {
      const s = chunk.toString();
      stdout += s;
      console.log('[DEBUG] Java stdout:', s.trim());
    });
    child.stderr.on('data', (chunk) => {
      const s = chunk.toString();
      stderr += s;
      console.error('[DEBUG] Java stderr:', s.trim());
    });

    const timeout = 110000; // 110 seconds (Lambda timeout is 120)
    const timeoutId = setTimeout(() => {
      console.error('[DEBUG] Java timeout - killing');
      child.kill('SIGKILL');
      reject(new Error('Prediction timeout'));
    }, timeout);

    child.on('close', (code, signal) => {
      console.log('[DEBUG] Java process closed', { code, signal, outputFileExists: fs.existsSync(outputFile) });
      clearTimeout(timeoutId);
      try {
        if (fs.existsSync(outputFile)) {
          const data = fs.readFileSync(outputFile, 'utf8');
          fs.unlinkSync(outputFile);
          resolve(JSON.parse(data));
        } else {
          const errDetail = [stdout, stderr].filter(Boolean).join('\n').trim().slice(0, 1000);
          console.error('[DEBUG] Java exit', code, 'stdout:', stdout?.slice(-500));
          console.error('[DEBUG] Java stderr:', stderr?.slice(-500));
          reject(new Error(`Process exited with code ${code}${errDetail ? '\n' + errDetail : ''}`));
        }
      } catch (err) {
        if (fs.existsSync(outputFile)) fs.unlinkSync(outputFile);
        if (stdout) console.error('[DEBUG] Java stdout (full):', stdout);
        if (stderr) console.error('[DEBUG] Java stderr (full):', stderr);
        reject(err);
      }
    });

    child.on('error', (err) => {
      console.error('[DEBUG] Java spawn error:', err);
      clearTimeout(timeoutId);
      if (fs.existsSync(outputFile)) fs.unlinkSync(outputFile);
      reject(err);
    });
  });
}

function corsHeaders() {
  return {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'Content-Type,Authorization',
  };
}

exports.handler = async (event) => {
  const rawPath = event.requestContext?.http?.path || event.path || '';
  const reqPath = rawPath.startsWith('/') ? rawPath : `/${rawPath}`;
  const method = event.requestContext?.http?.method || event.httpMethod || '';

  if (method === 'GET' && (reqPath === '/health' || reqPath === '')) {
    return {
      statusCode: 200,
      headers: corsHeaders(),
      body: JSON.stringify({ status: 'ok' }),
    };
  }

  if (method === 'OPTIONS') {
    return {
      statusCode: 204,
      headers: {
        ...corsHeaders(),
        'Access-Control-Allow-Methods': 'POST, GET, OPTIONS',
        'Access-Control-Max-Age': '86400',
      },
      body: '',
    };
  }

  if (method === 'POST' && reqPath === '/predict') {
    console.log('[DEBUG] POST /predict received');
    let body;
    try {
      body = typeof event.body === 'string' ? JSON.parse(event.body) : event.body || {};
    } catch (e) {
      console.error('[DEBUG] JSON parse error:', e.message);
      return {
        statusCode: 400,
        headers: corsHeaders(),
        body: JSON.stringify({ error: 'Invalid JSON body' }),
      };
    }

    const review = body.review;
    if (!review || typeof review !== 'string') {
      console.error('[DEBUG] Invalid review:', typeof review);
      return {
        statusCode: 400,
        headers: corsHeaders(),
        body: JSON.stringify({ error: 'Missing or invalid "review" field in request body' }),
      };
    }

    console.log('[DEBUG] Calling runPrediction for review len:', review.length);
    try {
      const result = await runPrediction(review.trim());
      return {
        statusCode: 200,
        headers: corsHeaders(),
        body: JSON.stringify(result),
      };
    } catch (err) {
      console.error('Prediction error:', err);
      return {
        statusCode: 500,
        headers: corsHeaders(),
        body: JSON.stringify({
          error: 'Prediction failed',
          message: err.message,
        }),
      };
    }
  }

  return {
    statusCode: 404,
    headers: corsHeaders(),
    body: JSON.stringify({ error: 'Not found' }),
  };
};
