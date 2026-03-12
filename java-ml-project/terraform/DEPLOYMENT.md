# Local Deployment Guide

Deploy the Java Workshop (frontend + Lambda sentiment API) to AWS from your machine.

---

## Prerequisites

Install the following before starting:

| Tool | Version | Install |
|------|---------|---------|
| **Docker** | Latest | [docker.com](https://docs.docker.com/get-docker/) |
| **Terraform** | >= 1.0 | `yay -S terraform` (Arch) or [terraform.io](https://developer.hashicorp.com/terraform/downloads) |
| **Node.js** | 18+ | [nodejs.org](https://nodejs.org/) or `nvm install 20` |
| **npm** | (bundled with Node) | — |
| **AWS CLI** | v2 | [aws.amazon.com/cli](https://aws.amazon.com/cli/) |

Verify:

```bash
docker --version
terraform --version
node --version
npm --version
aws --version
```

---

## 1. Clone and Set Up the Repo

```bash
git clone <repo-url>
cd cs-ed-lab
```

No extra setup needed. The deploy script runs from `java-ml-project/terraform/`.

---

## 2. Configure AWS Credentials

```bash
aws configure
```

Enter your **Access Key ID**, **Secret Access Key**, and default region (e.g. `us-west-1`).

Or set environment variables:

```bash
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_REGION="us-west-1"
```

---

## 3. First-Time Infrastructure (Terraform)

Create S3 bucket, CloudFront, Lambda, API Gateway, ECR.

### 3a. Set required variables (no fallbacks)

Copy the example env file and edit:

```bash
cd java-ml-project/terraform
cp .env.example .env
# Edit .env with your values (it's gitignored)
```

Or export manually:

```bash
export AWS_REGION="us-west-1"
export FRONTEND_BUCKET_NAME="java-workshop-yourname-$(date +%s)"
export ENVIRONMENT="prod"
export PROJECT_NAME="java-workshop"
```

`run.sh` sources `.env` automatically if present. Bucket name must be **globally unique**.

### 3b. Run Terraform

```bash
cd java-ml-project/terraform
./run.sh init
./run.sh plan
./run.sh apply
```

Type `yes` when prompted, or use `-auto-approve`:

```bash
./run.sh apply -auto-approve
```

Terraform will:

- Create S3 bucket, CloudFront, Lambda, API Gateway, ECR
- Push a placeholder image to ECR so Lambda can be created
- Write `terraform.tfstate` in this directory

### 3c. Save outputs for deploy-local.sh (required)

```bash
export AWS_REGION=$(terraform output -raw aws_region)
export FRONTEND_BUCKET_NAME=$(terraform output -raw frontend_bucket_name)
export CLOUDFRONT_ID=$(terraform output -raw cloudfront_id)
export API_URL=$(terraform output -raw api_url)
export ECR_REPOSITORY_URL=$(terraform output -raw ecr_repository_url)
```

---

## 4. Deploy Application (Build + Push)

After Terraform has been applied at least once:

```bash
cd java-ml-project/terraform
./deploy-local.sh
```

Set all required vars first (see 3c). No fallbacks.

The script will:

1. Build `sentiment-spark` (Java model)
2. Build Lambda image
3. Push to ECR
4. Update Lambda function
5. Build frontend (with API URL)
6. Sync frontend to S3
7. Invalidate CloudFront cache

---

## 5. Subsequent Deployments

For code changes only (no infra changes):

```bash
cd java-ml-project/terraform
./deploy-local.sh
```

Set all required vars (see 3c) before each run.

---

## Environment Variables Reference

| Variable | Required for | Used by |
|----------|---------------|---------|
| `AWS_REGION` | Both | `run.sh`, deploy |
| `FRONTEND_BUCKET_NAME` | Both | `run.sh`, deploy |
| `ENVIRONMENT` | `run.sh` | Terraform |
| `PROJECT_NAME` | `run.sh` | Terraform |
| `CLOUDFRONT_ID` | deploy | deploy-local.sh |
| `API_URL` | deploy | deploy-local.sh |
| `ECR_REPOSITORY_URL` | deploy | deploy-local.sh |
| `AWS_ACCESS_KEY_ID` | Both | AWS CLI |
| `AWS_SECRET_ACCESS_KEY` | Both | AWS CLI |

### Export commands (copy-paste)

```bash
# For run.sh (terraform)
export AWS_REGION="us-west-1"
export FRONTEND_BUCKET_NAME="java-workshop-yourname-$(date +%s)"
export ENVIRONMENT="prod"
export PROJECT_NAME="java-workshop"

# For deploy-local.sh (after terraform apply)
export AWS_REGION=$(terraform output -raw aws_region)
export FRONTEND_BUCKET_NAME=$(terraform output -raw frontend_bucket_name)
export CLOUDFRONT_ID=$(terraform output -raw cloudfront_id)
export API_URL=$(terraform output -raw api_url)
export ECR_REPOSITORY_URL=$(terraform output -raw ecr_repository_url)
```

**Alternative:** Create `terraform.tfvars` in this directory:

```
frontend_bucket_name = "java-workshop-yourname-1234567890"
aws_region          = "us-west-1"
```

Then run `terraform apply` (no need for `./run.sh` or env vars).

---

## Troubleshooting

| Error | Fix |
|-------|-----|
| `Missing required variables` | Set all vars listed in the error message (see 3a, 3c) |
| `Lambda function not found` | Run `./run.sh apply` first to create the Lambda |
| `InvalidClientTokenId` | Configure AWS credentials: `aws configure` |
| `Bucket already exists` | Choose a different `FRONTEND_BUCKET_NAME` (must be globally unique) |
| Docker build fails | Ensure Docker daemon is running |

---

## Optional: Add to ~/.zshrc

For quick access to deploy and fetch logs (uses zsh):

```zsh
# Java Workshop deployment (add to ~/.zshrc)
export CSED_TERRAFORM="$HOME/csed_lab/cs-ed-lab/java-ml-project/terraform"
alias jw-deploy="cd $CSED_TERRAFORM && ./deploy-local.sh"
alias jw-logs="cd $CSED_TERRAFORM && ./fetch-lambda-logs.sh"
alias jw-tf="cd $CSED_TERRAFORM && ./run.sh"
```

Adjust `CSED_TERRAFORM` to your repo path.

---

## Project Layout

```
cs-ed-lab/
├── java-ml-project/
│   ├── terraform/          # Run from here
│   │   ├── run.sh          # Terraform with env vars
│   │   ├── deploy-local.sh # Full deployment
│   │   └── DEPLOYMENT.md   # This file
│   ├── viewer/             # Frontend (Vite + React)
│   └── Dockerfile          # Java model (sentiment-spark)
├── sentiment-api/
│   ├── Dockerfile.lambda   # Lambda container
│   └── lambda/index.js    # Lambda handler
└── ...
```
