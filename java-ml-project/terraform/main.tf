terraform {
  required_version = ">= 1.0"
  backend "local" {
    path = "terraform.tfstate"
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 3.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
  # Credentials from env: AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, or AWS_PROFILE
}

# -----------------------------------------------------------------------------
# Frontend: S3 + CloudFront
# -----------------------------------------------------------------------------

resource "aws_s3_bucket" "frontend" {
  bucket        = var.frontend_bucket_name
  force_destroy = true

  tags = {
    Name        = var.frontend_bucket_name
    Environment = var.environment
    Project     = "java-workshop"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  block_public_acls       = true
  block_public_policy      = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_cloudfront_origin_access_control" "frontend" {
  name                              = "${var.project_name}-frontend-oac"
  description                       = "OAC for ${var.project_name} frontend"
  origin_access_control_origin_type  = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_s3_bucket_policy" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowCloudFrontServicePrincipal"
        Effect = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action   = "s3:GetObject"
        Resource = "${aws_s3_bucket.frontend.arn}/*"
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.frontend.arn
          }
        }
      }
    ]
  })

  depends_on = [aws_cloudfront_distribution.frontend]
}

resource "aws_cloudfront_distribution" "frontend" {
  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"
  comment             = "${var.project_name} frontend"
  price_class         = "PriceClass_100"

  origin {
    domain_name              = aws_s3_bucket.frontend.bucket_regional_domain_name
    origin_id                = "S3-${aws_s3_bucket.frontend.id}"
    origin_access_control_id = aws_cloudfront_origin_access_control.frontend.id
  }

  default_cache_behavior {
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    target_origin_id       = "S3-${aws_s3_bucket.frontend.id}"
    compress               = true
    viewer_protocol_policy = "redirect-to-https"

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 3600
    max_ttl     = 86400
  }

  custom_error_response {
    error_code         = 404
    response_code      = 200
    response_page_path = "/index.html"
  }

  custom_error_response {
    error_code         = 403
    response_code      = 200
    response_page_path = "/index.html"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = {
    Name        = "${var.project_name}-frontend"
    Environment = var.environment
  }
}

# -----------------------------------------------------------------------------
# Lambda Backend: Sentiment API
# -----------------------------------------------------------------------------

resource "aws_ecr_repository" "sentiment_api" {
  name                 = "${var.project_name}-sentiment-api"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.project_name}-sentiment-api"
    Environment = var.environment
  }
}

# Push placeholder image so Lambda can be created (no chicken-and-egg)
resource "null_resource" "ecr_placeholder_image" {
  triggers = {
    ecr_url = aws_ecr_repository.sentiment_api.repository_url
  }

  provisioner "local-exec" {
    command = <<-EOT
      set -e
      REGISTRY="${split("/", aws_ecr_repository.sentiment_api.repository_url)[0]}"
      aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin "$${REGISTRY}"
      docker pull public.ecr.aws/lambda/nodejs:20
      docker tag public.ecr.aws/lambda/nodejs:20 ${aws_ecr_repository.sentiment_api.repository_url}:latest
      docker push ${aws_ecr_repository.sentiment_api.repository_url}:latest
    EOT
  }

  depends_on = [aws_ecr_repository.sentiment_api]
}

resource "aws_lambda_function" "sentiment_api" {
  function_name = "${var.project_name}-sentiment-api"
  role          = aws_iam_role.lambda_sentiment.arn
  package_type  = "Image"
  image_uri     = "${aws_ecr_repository.sentiment_api.repository_url}:latest"
  timeout       = 120
  memory_size   = 2048

  environment {
    variables = {
      GOOGLE_SHEET_ID             = var.google_sheet_id
      GOOGLE_SERVICE_ACCOUNT_JSON = var.google_service_account_json
    }
  }

  tags = {
    Name        = "${var.project_name}-sentiment-api"
    Environment = var.environment
  }

  depends_on = [null_resource.ecr_placeholder_image]

  lifecycle {
    ignore_changes = [image_uri]
  }
}

resource "aws_iam_role" "lambda_sentiment" {
  name = "${var.project_name}-lambda-sentiment-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_sentiment.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# API Gateway HTTP API for Lambda
resource "aws_apigatewayv2_api" "sentiment" {
  name          = "${var.project_name}-sentiment-api"
  protocol_type = "HTTP"
  description   = "Sentiment API for Java Workshop"

  cors_configuration {
    allow_origins = ["*"]
    allow_methods = ["GET", "POST", "OPTIONS"]
    allow_headers = ["*"]
    allow_credentials = false
    max_age         = 86400
  }

  tags = {
    Name        = "${var.project_name}-sentiment-api"
    Environment = var.environment
  }
}

resource "aws_apigatewayv2_integration" "sentiment" {
  api_id                 = aws_apigatewayv2_api.sentiment.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.sentiment_api.invoke_arn
  integration_method     = "POST"
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_route" "predict" {
  api_id    = aws_apigatewayv2_api.sentiment.id
  route_key = "POST /predict"
  target    = "integrations/${aws_apigatewayv2_integration.sentiment.id}"
}

resource "aws_apigatewayv2_route" "health" {
  api_id    = aws_apigatewayv2_api.sentiment.id
  route_key = "GET /health"
  target    = "integrations/${aws_apigatewayv2_integration.sentiment.id}"
}

resource "aws_apigatewayv2_route" "save_user" {
  api_id    = aws_apigatewayv2_api.sentiment.id
  route_key = "POST /save-user"
  target    = "integrations/${aws_apigatewayv2_integration.sentiment.id}"
}

resource "aws_apigatewayv2_route" "reflect" {
  api_id    = aws_apigatewayv2_api.sentiment.id
  route_key = "POST /reflect"
  target    = "integrations/${aws_apigatewayv2_integration.sentiment.id}"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.sentiment.id
  name        = "$default"
  auto_deploy = true
}

resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.sentiment_api.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.sentiment.execution_arn}/*/*"
}
