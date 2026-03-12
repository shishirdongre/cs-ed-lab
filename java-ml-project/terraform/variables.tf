variable "aws_region" {
  description = "AWS region (TF_VAR_aws_region or AWS_REGION)"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name (TF_VAR_environment)"
  type        = string
  default     = "prod"
}

variable "project_name" {
  description = "Project name used for resource naming (TF_VAR_project_name)"
  type        = string
  default     = "java-workshop"
}

variable "frontend_bucket_name" {
  description = "S3 bucket name for frontend (TF_VAR_frontend_bucket_name or FRONTEND_BUCKET_NAME via run.sh)"
  type        = string
}

variable "google_sheet_id" {
  description = "Google Sheet ID for reflection submissions (optional)"
  type        = string
  default     = ""
}

variable "google_service_account_json" {
  description = "Google service account JSON (raw or base64) for Sheets API (optional, sensitive)"
  type        = string
  default     = ""
  sensitive   = true
}
