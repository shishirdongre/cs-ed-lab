output "aws_region" {
  description = "AWS region"
  value       = var.aws_region
}

output "cloudfront_id" {
  description = "CloudFront distribution ID"
  value       = aws_cloudfront_distribution.frontend.id
}

output "cloudfront_domain" {
  description = "CloudFront distribution domain name"
  value       = aws_cloudfront_distribution.frontend.domain_name
}

output "cloudfront_url" {
  description = "CloudFront distribution URL (HTTPS)"
  value       = "https://${aws_cloudfront_distribution.frontend.domain_name}"
}

output "frontend_bucket_name" {
  description = "S3 bucket name for frontend"
  value       = aws_s3_bucket.frontend.id
}

output "api_url" {
  description = "Sentiment API base URL"
  value       = "${aws_apigatewayv2_stage.default.invoke_url}"
}

output "ecr_repository_url" {
  description = "ECR repository URL for sentiment API Lambda image"
  value       = aws_ecr_repository.sentiment_api.repository_url
}
