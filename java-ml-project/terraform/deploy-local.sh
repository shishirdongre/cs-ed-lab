#!/bin/zsh
# Run full deployment locally.
# Run from java-ml-project/terraform/
# Reads Terraform outputs automatically. No .zshrc, no fallbacks.

set -e
TERRAFORM_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$TERRAFORM_DIR/../.." && pwd)"
cd "$TERRAFORM_DIR"

# Ignore inherited env - use Terraform outputs only
unset AWS_REGION FRONTEND_BUCKET_NAME CLOUDFRONT_ID API_URL ECR_REPOSITORY_URL 2>/dev/null

# Read from Terraform outputs (source of truth)
AWS_REGION="$(terraform output -raw aws_region 2>/dev/null)"
FRONTEND_BUCKET_NAME="$(terraform output -raw frontend_bucket_name 2>/dev/null)"
CLOUDFRONT_ID="$(terraform output -raw cloudfront_id 2>/dev/null)"
API_URL="$(terraform output -raw api_url 2>/dev/null)"
ECR_FULL="$(terraform output -raw ecr_repository_url 2>/dev/null)"
ECR_REGISTRY="${ECR_FULL%%/*}"
ECR_REPOSITORY_URL="${ECR_REGISTRY}/java-workshop-sentiment-api"

REQUIRED_VARS=(AWS_REGION FRONTEND_BUCKET_NAME CLOUDFRONT_ID API_URL ECR_REPOSITORY_URL)
MISSING=()
for v in $REQUIRED_VARS; do
  [[ -z "${(P)v}" ]] && MISSING+=($v)
done

if [[ ${#MISSING[@]} -gt 0 ]]; then
  echo "Error: Terraform outputs missing. Run 'terraform apply' first."
  echo "Missing: ${MISSING[*]}"
  exit 1
fi

echo "Deploying from $REPO_ROOT"
echo "  Region: $AWS_REGION"
echo "  Bucket: $FRONTEND_BUCKET_NAME"
echo "  API: $API_URL"
echo "  ECR: $ECR_REPOSITORY_URL"
echo ""

# 1. Build sentiment-spark (Java model)
echo "Building sentiment-spark..."
docker build -t sentiment-spark "$REPO_ROOT/java-ml-project"

# 2. Build Lambda image
echo "Building Lambda image..."
docker build -f "$REPO_ROOT/sentiment-api/Dockerfile.lambda" -t sentiment-api-lambda "$REPO_ROOT"

# 3. Login to ECR and push
ECR_PUSH_TARGET="${ECR_REGISTRY}/java-workshop-sentiment-api"
echo "Pushing to ECR: $ECR_PUSH_TARGET"
export AWS_DEFAULT_REGION="$AWS_REGION"
ECR_PASSWORD=$(aws ecr get-login-password)
echo "$ECR_PASSWORD" | docker login --username AWS --password-stdin "$ECR_REGISTRY"
docker tag sentiment-api-lambda:latest "${ECR_PUSH_TARGET}:latest"
docker push "${ECR_PUSH_TARGET}:latest"

# 4. Update Lambda function
echo "Updating Lambda..."
aws lambda update-function-code --region "$AWS_REGION" \
  --function-name java-workshop-sentiment-api \
  --image-uri "${ECR_PUSH_TARGET}:latest"
aws lambda wait function-updated --function-name java-workshop-sentiment-api --region "$AWS_REGION"

# 5. Build frontend
echo "Building frontend..."
cd "$REPO_ROOT/java-ml-project/viewer"
npm install
VITE_SENTIMENT_API_URL="$API_URL" npm run build

# 6. Deploy frontend to S3
echo "Deploying frontend to S3..."
aws s3 sync "$REPO_ROOT/java-ml-project/viewer/dist" "s3://$FRONTEND_BUCKET_NAME/" --delete

# 7. Invalidate CloudFront
echo "Invalidating CloudFront..."
aws cloudfront create-invalidation --distribution-id "$CLOUDFRONT_ID" --paths "/*"

echo ""
echo "Deployment complete."
echo "API: $API_URL"
