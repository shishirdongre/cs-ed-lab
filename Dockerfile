# Use Ubuntu as base image
FROM ubuntu:22.04

# Avoid prompts from apt
ENV DEBIAN_FRONTEND=noninteractive

# Install system dependencies
RUN apt-get update && apt-get install -y \
    openjdk-11-jdk \
    python3 \
    python3-pip \
    python3-venv \
    git \
    curl \
    wget \
    vim \
    && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Create workspace directory
WORKDIR /workspace

# Copy project files
COPY . /workspace/

# Create Python virtual environment
RUN python3 -m venv /workspace/venv

# Activate virtual environment and install Python dependencies
RUN /workspace/venv/bin/pip install --upgrade pip

# Install common Python ML libraries
RUN /workspace/venv/bin/pip install \
    numpy \
    pandas \
    scikit-learn \
    matplotlib \
    seaborn \
    jupyter

# Create a shell script to always use the virtual environment
RUN echo '#!/bin/bash\nsource /workspace/venv/bin/activate\nexec "$@"' > /usr/local/bin/python-venv && \
    chmod +x /usr/local/bin/python-venv

# Create aliases for convenience
RUN echo 'alias python="/workspace/venv/bin/python"' >> /root/.bashrc && \
    echo 'alias pip="/workspace/venv/bin/pip"' >> /root/.bashrc && \
    echo 'alias jupyter="/workspace/venv/bin/jupyter"' >> /root/.bashrc

# Set default command
CMD ["/bin/bash"]