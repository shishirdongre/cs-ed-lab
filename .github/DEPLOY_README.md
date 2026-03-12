# Deploy Workflow

Triggers on push to `java_workshop` or via **Run workflow** (workflow_dispatch). Builds and deploys:

1. **Lambda** – Builds `sentiment-spark` (Java model) then the Lambda image
2. **Frontend** – Builds viewer, uploads to S3, invalidates CloudFront

## Required GitHub configuration

### Secrets
- `AWS_ROLE_ARN` – IAM role for OIDC (recommended), or
- `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` – if not using OIDC

### Variables (Settings → Secrets and variables → Actions → Variables)
- `ECR_REPOSITORY_NAME` – e.g. `java-workshop-sentiment-api`
- `LAMBDA_FUNCTION_NAME` – e.g. `java-workshop-sentiment-api`
- `FRONTEND_BUCKET_NAME` – S3 bucket for frontend
- `CLOUDFRONT_DISTRIBUTION_ID` – CloudFront distribution ID
- `API_URL` – Sentiment API base URL (e.g. `https://xxx.execute-api.us-east-1.amazonaws.com`)

Values come from Terraform outputs after `terraform apply`. Run `./deploy-local.sh` from `java-ml-project/terraform/` for local deployment.

### Pushing workflow files

If `git push` fails with "refusing to allow a Personal Access Token to create or update workflow...without `workflow` scope":

1. Create a new PAT at https://github.com/settings/tokens with **workflow** scope (classic) or **Workflows** permission (fine-grained).
2. Update your remote: `git remote set-url origin https://<PAT>@github.com/shishirdongre/cs-ed-lab.git`
