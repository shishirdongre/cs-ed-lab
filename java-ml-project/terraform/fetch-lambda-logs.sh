#!/bin/zsh
# Fetch Lambda logs and save to lambda-logs.txt
# Runs via zsh -i so ~/.zshrc is loaded (AWS credentials, etc.)

TERRAFORM_DIR="$(cd "$(dirname "$0")" && pwd)"
zsh -i -c "
  cd '$TERRAFORM_DIR'
  aws logs tail /aws/lambda/java-workshop-sentiment-api \
    --region us-west-1 \
    --since 24h \
    --format short \
    2>&1 | tee lambda-logs.txt
  echo ''
  echo 'Logs saved to lambda-logs.txt'
"
