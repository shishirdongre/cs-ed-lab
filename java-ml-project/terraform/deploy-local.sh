#!/bin/zsh
# Run full deployment locally. Matches .github/workflows/deploy.yml.
# Run from java-ml-project/terraform/
# Sources .env, reads Terraform outputs.

set -e
TERRAFORM_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$TERRAFORM_DIR/../.." && pwd)"
cd "$TERRAFORM_DIR"

# Source .env (required vars + optional Google Sheets)
[[ -f .env ]] && set -a && source .env && set +a

# Read from Terraform outputs (or .env overrides)
AWS_REGION="${AWS_REGION:-$(terraform output -raw aws_region 2>/dev/null)}"
API_URL="${API_URL:-$(terraform output -raw api_url 2>/dev/null)}"
CF_DIST_ID="${CLOUDFRONT_DISTRIBUTION_ID:-$(terraform output -raw cloudfront_id 2>/dev/null)}"
CLOUDFRONT_URL="${CLOUDFRONT_URL:-$(terraform output -raw cloudfront_url 2>/dev/null)}"
ECR_FULL="$(terraform output -raw ecr_repository_url 2>/dev/null)"
ECR_REGISTRY="${ECR_FULL%%/*}"
ECR_REPOSITORY_NAME="${ECR_REPOSITORY_NAME:-java-workshop-sentiment-api}"
LAMBDA_FUNCTION_NAME="${LAMBDA_FUNCTION_NAME:-java-workshop-sentiment-api}"
FRONTEND_BUCKET_BASE="${FRONTEND_BUCKET_BASE:-java-workshop-frontend}"

REQUIRED=(AWS_REGION API_URL CF_DIST_ID ECR_REGISTRY)
MISSING=()
for v in $REQUIRED; do
  [[ -z "${(P)v}" ]] && MISSING+=($v)
done
if [[ ${#MISSING[@]} -gt 0 ]]; then
  echo "Error: Missing required vars. Set in .env or run 'terraform apply'."
  echo "Missing: ${MISSING[*]}"
  exit 1
fi

echo "Deploying from $REPO_ROOT"
echo "  Region: $AWS_REGION"
echo "  API: $API_URL"
echo "  ECR: $ECR_REGISTRY/$ECR_REPOSITORY_NAME"
echo ""

# 1. Build sentiment-spark (Java model)
echo "Building sentiment-spark..."
docker build -t sentiment-spark "$REPO_ROOT/java-ml-project"

# 2. Build Lambda image
echo "Building Lambda image..."
docker build -f "$REPO_ROOT/sentiment-api/Dockerfile.lambda" -t sentiment-api-lambda "$REPO_ROOT"

# 3. Login to ECR and push
ECR_PUSH_TARGET="${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}"
echo "Pushing to ECR: $ECR_PUSH_TARGET"
export AWS_DEFAULT_REGION="$AWS_REGION"
ECR_PASSWORD=$(aws ecr get-login-password)
echo "$ECR_PASSWORD" | docker login --username AWS --password-stdin "$ECR_REGISTRY"
docker tag sentiment-api-lambda:latest "${ECR_PUSH_TARGET}:latest"
docker push "${ECR_PUSH_TARGET}:latest"

# 4. Update Lambda function
echo "Updating Lambda..."
aws lambda update-function-code --region "$AWS_REGION" \
  --function-name "$LAMBDA_FUNCTION_NAME" \
  --image-uri "${ECR_PUSH_TARGET}:latest"
aws lambda wait function-updated --function-name "$LAMBDA_FUNCTION_NAME" --region "$AWS_REGION"

# 4b. Update Lambda env (Google Sheets) - matches pipeline when GOOGLE_SHEET_ID set
GOOGLE_SHEET_ID="${TF_VAR_google_sheet_id:-$(terraform output -raw google_sheet_id 2>/dev/null)}"
GOOGLE_SERVICE_ACCOUNT_JSON="${TF_VAR_google_service_account_json:-}"
[[ -z "$GOOGLE_SERVICE_ACCOUNT_JSON" && -f service-account.json ]] && GOOGLE_SERVICE_ACCOUNT_JSON=$(cat service-account.json | jq -c . 2>/dev/null)
if [[ -n "$GOOGLE_SHEET_ID" ]]; then
  echo "Updating Lambda env (Google Sheets)..."
  CURRENT=$(aws lambda get-function-configuration --function-name "$LAMBDA_FUNCTION_NAME" --region "$AWS_REGION" --query 'Environment.Variables' --output json 2>/dev/null || echo '{}')
  ENV_JSON=$(echo "$CURRENT" | jq --arg sheet "$GOOGLE_SHEET_ID" --arg sjson "${GOOGLE_SERVICE_ACCOUNT_JSON:-}" \
    '.GOOGLE_SHEET_ID = $sheet | .GOOGLE_SERVICE_ACCOUNT_JSON = $sjson | {Variables: .}')
  aws lambda update-function-configuration --region "$AWS_REGION" \
    --function-name "$LAMBDA_FUNCTION_NAME" \
    --environment "$ENV_JSON"
else
  echo "Skipping Lambda env update (set TF_VAR_google_sheet_id in .env)"
fi

# 5. Build frontend
echo "Building frontend..."
cd "$REPO_ROOT/java-ml-project/viewer"
npm install
VITE_SENTIMENT_API_URL="$API_URL" npm run build

# 6. Create deployment bucket and deploy (matches pipeline)
SUFFIX=$(date +%s)
BUCKET="${FRONTEND_BUCKET_BASE}-${SUFFIX}"
echo "Creating bucket and deploying: s3://$BUCKET"
aws s3 mb "s3://${BUCKET}" --region "$AWS_REGION"
ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
CF_ARN="arn:aws:cloudfront::${ACCOUNT}:distribution/${CF_DIST_ID}"
aws s3api put-bucket-policy --bucket "$BUCKET" --policy "{
  \"Version\": \"2012-10-17\",
  \"Statement\": [{
    \"Sid\": \"AllowCloudFront\",
    \"Effect\": \"Allow\",
    \"Principal\": {\"Service\": \"cloudfront.amazonaws.com\"},
    \"Action\": \"s3:GetObject\",
    \"Resource\": \"arn:aws:s3:::${BUCKET}/*\",
    \"Condition\": {\"StringEquals\": {\"AWS:SourceArn\": \"${CF_ARN}\"}}
  }]
}"
aws s3 sync "$REPO_ROOT/java-ml-project/viewer/dist" "s3://${BUCKET}/" --delete

# 7. Update CloudFront origin to new bucket
echo "Updating CloudFront origin..."
ETAG=$(aws cloudfront get-distribution-config --id "$CF_DIST_ID" --query ETag --output text)
CONFIG=$(aws cloudfront get-distribution-config --id "$CF_DIST_ID" --output json)
ORIGIN_ID="S3-${BUCKET}"
DOMAIN="${BUCKET}.s3.${AWS_REGION}.amazonaws.com"
echo "$CONFIG" | jq --arg domain "$DOMAIN" --arg id "$ORIGIN_ID" \
  '.DistributionConfig.Origins.Items[0].DomainName = $domain |
   .DistributionConfig.Origins.Items[0].Id = $id |
   .DistributionConfig.DefaultCacheBehavior.TargetOriginId = $id |
   .DistributionConfig' | jq -c '.' > /tmp/cf-config.json
aws cloudfront update-distribution --id "$CF_DIST_ID" --if-match "$ETAG" \
  --distribution-config file:///tmp/cf-config.json

# 8. Invalidate CloudFront
echo "Invalidating CloudFront..."
aws cloudfront create-invalidation --distribution-id "$CF_DIST_ID" --paths "/*"

echo ""
echo "Deployment complete."
echo "Frontend: $CLOUDFRONT_URL"
echo "API: $API_URL"
