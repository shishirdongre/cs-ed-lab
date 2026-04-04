#!/bin/bash
# Copy secrets to the deploy environment so others can run the pipeline.
# Run with env vars set (e.g. source .env or export from zshrc).
# Usage: ./scripts/setup-env-secrets.sh

set -e
cd "$(dirname "$0")/.."

echo "Setting deploy environment secrets (requires repo admin)..."

[ -z "$AWS_ACCESS_KEY_ID" ] && echo "Warning: AWS_ACCESS_KEY_ID not set"
[ -z "$AWS_SECRET_ACCESS_KEY" ] && echo "Warning: AWS_SECRET_ACCESS_KEY not set"

gh secret set AWS_ACCESS_KEY_ID --env deploy --body "${AWS_ACCESS_KEY_ID:?Set AWS_ACCESS_KEY_ID}"
gh secret set AWS_SECRET_ACCESS_KEY --env deploy --body "${AWS_SECRET_ACCESS_KEY:?Set AWS_SECRET_ACCESS_KEY}"

if [ -n "$GOOGLE_SERVICE_ACCOUNT_JSON" ]; then
  gh secret set GOOGLE_SERVICE_ACCOUNT_JSON --env deploy --body "$GOOGLE_SERVICE_ACCOUNT_JSON"
  echo "Set GOOGLE_SERVICE_ACCOUNT_JSON"
else
  echo "Skipping GOOGLE_SERVICE_ACCOUNT_JSON (optional - set var to add)"
fi

echo "Done. Deploy environment secrets are set."
