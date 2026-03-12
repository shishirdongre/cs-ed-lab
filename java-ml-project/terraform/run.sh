#!/bin/zsh
# Run Terraform with variables from environment.
# Sources .env if present (gitignored; copy from .env.example).

set -e
cd "$(dirname "$0")"
[[ -f .env ]] && set -a && source .env && set +a

REQUIRED_VARS=(
  AWS_REGION
  FRONTEND_BUCKET_NAME
  ENVIRONMENT
  PROJECT_NAME
)

MISSING=()
for v in $REQUIRED_VARS; do
  [[ -z "${(P)v}" ]] && MISSING+=($v)
done

if [[ ${#MISSING[@]} -gt 0 ]]; then
  echo "Error: Missing required variables:"
  for v in $MISSING; do
    echo "  export $v=\"...\""
  done
  exit 1
fi

export TF_VAR_aws_region="$AWS_REGION"
export TF_VAR_environment="$ENVIRONMENT"
export TF_VAR_project_name="$PROJECT_NAME"
export TF_VAR_frontend_bucket_name="$FRONTEND_BUCKET_NAME"

exec terraform "$@"
