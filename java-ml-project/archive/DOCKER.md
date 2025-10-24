# Docker Setup for Yelp Sentiment Analysis

This guide explains how to run the Yelp Sentiment Analysis using Docker containers.

## Quick Start

### Option 1: One-time Analysis
```bash
# Build and run the analysis
docker-compose up yelp-analysis-run
```

### Option 2: Interactive Container
```bash
# Build and start interactive container
docker-compose up yelp-sentiment-analysis

# Inside the container, run:
./run_analysis.sh
```

### Option 3: Manual Docker Commands
```bash
# Build the image
docker build -t yelp-sentiment-analysis .

# Run the analysis
docker run --rm yelp-sentiment-analysis

# Run interactively
docker run -it --rm yelp-sentiment-analysis /bin/bash
```

## Docker Files

### `Dockerfile`
Multi-stage Dockerfile that:
- **Stage 1 (Builder)**: Installs Java 11, Maven, Python 3, compiles the project, creates dataset
- **Stage 2 (Runtime)**: Creates minimal runtime image with only necessary dependencies

### `docker-compose.yml`
Defines two services:
- `yelp-sentiment-analysis`: Interactive container for development
- `yelp-analysis-run`: One-time analysis execution

### `.dockerignore`
Excludes unnecessary files from Docker build context for faster builds.

## Container Features

### Pre-installed Dependencies
- **Java 11** (OpenJDK)
- **Maven 3.6+**
- **Python 3.7+**
- **All Maven dependencies** (OpenCSV, Apache Commons, Smile ML)
- **Python packages** (datasets, pandas, numpy)

### Pre-generated Dataset
- **1000 Yelp reviews** (500 positive, 499 negative)
- **Clean, processed data** ready for analysis
- **No internet required** for analysis

### Ready-to-Run Code
- **Compiled Java classes**
- **All dependencies resolved**
- **One-command execution**

## Usage Examples

### 1. Run Complete Analysis
```bash
# Using docker-compose (recommended)
docker-compose up yelp-analysis-run

# Using docker directly
docker run --rm yelp-sentiment-analysis
```

### 2. Interactive Development
```bash
# Start interactive container
docker-compose up -d yelp-sentiment-analysis

# Connect to container
docker exec -it yelp-sentiment-analysis /bin/bash

# Inside container:
./run_analysis.sh
# or
java -cp ".:target/classes:target/dependency/*" YelpSentimentAnalysisSmileML
```

### 3. Custom Java Options
```bash
# Run with custom memory settings
docker run --rm -e JAVA_OPTS="-Xmx4g" yelp-sentiment-analysis
```

### 4. Mount Local Directory
```bash
# Mount local directory for development
docker run -it --rm -v $(pwd):/app/local yelp-sentiment-analysis /bin/bash
```

## Performance

### Container Size
- **Builder stage**: ~800MB
- **Runtime stage**: ~400MB
- **Total image size**: ~400MB (optimized with multi-stage build)

### Build Time
- **First build**: ~5-10 minutes (downloads dependencies)
- **Subsequent builds**: ~2-3 minutes (cached layers)

### Runtime Performance
- **Analysis execution**: ~30-60 seconds
- **Memory usage**: ~200-500MB
- **CPU usage**: Single-threaded

## Troubleshooting

### Common Issues

1. **Out of Memory**
   ```bash
   # Increase Docker memory limit
   docker run --memory=2g --rm yelp-sentiment-analysis
   ```

2. **Build Failures**
   ```bash
   # Clean build (no cache)
   docker build --no-cache -t yelp-sentiment-analysis .
   ```

3. **Permission Issues**
   ```bash
   # Fix file permissions
   chmod +x run_analysis.sh
   ```

4. **Network Issues (Dataset Download)**
   ```bash
   # Check if dataset creation worked
   docker run --rm yelp-sentiment-analysis ls -la *.csv
   ```

### Debug Commands

```bash
# Check container logs
docker-compose logs yelp-sentiment-analysis

# Inspect container
docker inspect yelp-sentiment-analysis

# Check container processes
docker exec yelp-sentiment-analysis ps aux

# Check Java version
docker exec yelp-sentiment-analysis java -version

# Check Maven dependencies
docker exec yelp-sentiment-analysis ls -la target/dependency/
```

## Development Workflow

### 1. Local Development
```bash
# Make changes to Java files
# Rebuild container
docker-compose build yelp-sentiment-analysis

# Test changes
docker-compose up yelp-sentiment-analysis
```

### 2. Testing Changes
```bash
# Run tests inside container
docker exec yelp-sentiment-analysis mvn test

# Compile changes
docker exec yelp-sentiment-analysis mvn compile
```

### 3. Data Updates
```bash
# Recreate dataset
docker exec yelp-sentiment-analysis python3 create_simple_yelp_dataset.py

# Run analysis with new data
docker exec yelp-sentiment-analysis ./run_analysis.sh
```

## Production Deployment

### Docker Swarm
```yaml
version: '3.8'
services:
  yelp-analysis:
    image: yelp-sentiment-analysis:latest
    deploy:
      replicas: 1
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M
```

### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: yelp-sentiment-analysis
spec:
  replicas: 1
  selector:
    matchLabels:
      app: yelp-sentiment-analysis
  template:
    metadata:
      labels:
        app: yelp-sentiment-analysis
    spec:
      containers:
      - name: yelp-analysis
        image: yelp-sentiment-analysis:latest
        resources:
          limits:
            memory: "1Gi"
          requests:
            memory: "512Mi"
```

## Security Considerations

- **Non-root user**: Container runs as root (consider adding USER directive)
- **Minimal dependencies**: Only necessary packages installed
- **No secrets**: No sensitive data in container
- **Read-only filesystem**: Consider using read-only root filesystem

## Monitoring

### Health Checks
```dockerfile
# Add to Dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD java -cp ".:target/classes:target/dependency/*" YelpSentimentAnalysisSmileML || exit 1
```

### Logging
```bash
# View logs
docker logs yelp-sentiment-analysis

# Follow logs
docker logs -f yelp-sentiment-analysis
```

## Best Practices

1. **Use multi-stage builds** (already implemented)
2. **Minimize image size** (using slim base images)
3. **Cache dependencies** (Maven dependencies cached)
4. **Use .dockerignore** (excludes unnecessary files)
5. **Pin versions** (specific Java/Python versions)
6. **Security scanning** (regular vulnerability scans)

## Support

For issues with Docker setup:
1. Check Docker logs: `docker logs <container-name>`
2. Verify system requirements
3. Check available disk space
4. Ensure Docker daemon is running