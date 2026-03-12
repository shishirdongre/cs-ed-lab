# Lambda Log Analysis

## Logs fetched (2026-03-12)

```
START RequestId: ea6af206-b4ae-4ffd-b52d-59ac76956d2b
ERROR Prediction error: Error: Process exited with code 1
END RequestId: ea6af206-b4ae-4ffd-b52d-59ac76956d2b
REPORT Duration: 25472 ms | Memory Used: 279 MB | Init: 561 ms
```

## Analysis

**What happened:** The Java process (SentimentPredictorApp) ran for ~25 seconds then exited with code 1. No output file was written, so the failure occurred before the prediction completed.

**Likely causes:**
1. **Spark temp dir** – Spark may be writing to a read-only path. Lambda only allows writes to `/tmp`.
2. **Model load or transform failure** – Exception in `PipelineModel.load()` or `model.transform()`.
3. **Java stderr not captured** – Deployed Lambda doesn’t have the updated handler that logs Java stderr.

**Next steps:**
1. Redeploy Lambda with the handler that captures Java stderr.
2. Configure Spark to use `/tmp` in Lambda.
3. Tail logs again after a prediction to see the Java stack trace.
