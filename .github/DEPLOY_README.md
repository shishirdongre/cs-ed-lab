# Deploy Workflow

Triggers on push to `java_workshop` or via **Run workflow** (workflow_dispatch). Builds and deploys:

1. **Lambda** – Builds `sentiment-spark` (Java model) then the Lambda image
2. **Frontend** – Builds viewer, uploads to S3, invalidates CloudFront

## Configuration: `deploy` environment

All vars and secrets live in the **deploy** environment so collaborators can access them.

**Settings → Environments → deploy** (or https://github.com/shishirdongre/cs-ed-lab/settings/environments)

### Environment variables (already set)
- `ECR_REPOSITORY_NAME` – e.g. `java-workshop-sentiment-api`
- `LAMBDA_FUNCTION_NAME` – e.g. `java-workshop-sentiment-api`
- `FRONTEND_BUCKET_BASE` – Base name for frontend buckets (each deploy creates `{base}-{unix_seconds}`)
- `CLOUDFRONT_DISTRIBUTION_ID` – CloudFront distribution ID
- `API_URL` – Sentiment API base URL (e.g. `https://xxx.execute-api.us-east-1.amazonaws.com`)
- `GOOGLE_SHEET_ID` – Google Sheet ID for reflection submissions (optional; pipeline updates Lambda env)

### Environment secrets (add via Settings → Environments → deploy → Environment secrets)
- `AWS_ACCESS_KEY_ID` – AWS access key
- `AWS_SECRET_ACCESS_KEY` – AWS secret key
- `GOOGLE_SERVICE_ACCOUNT_JSON` – Service account JSON for Sheets (optional)

**To add secrets:** Run `./scripts/setup-env-secrets.sh` with AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY in your env, or add manually in the GitHub UI.

Values come from Terraform outputs after `terraform apply`. Run `./deploy-local.sh` from `java-ml-project/terraform/` for local deployment.

### Pushing workflow files

If `git push` fails with "refusing to allow a Personal Access Token to create or update workflow...without `workflow` scope":

1. Create a new PAT at https://github.com/settings/tokens with **workflow** scope (classic) or **Workflows** permission (fine-grained).
2. Update your remote: `git remote set-url origin https://<PAT>@github.com/shishirdongre/cs-ed-lab.git`
