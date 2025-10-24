FROM mcr.microsoft.com/devcontainers/base:ubuntu

# Install Java 21 via apt
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Install Python and pip (using default Ubuntu Python version)
RUN apt-get update && \
    apt-get install -y python3 python3-venv python3-pip python3-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create symlinks for python and pip
RUN ln -s /usr/bin/python3 /usr/bin/python && \
    ln -s /usr/bin/python3 /usr/local/bin/python

# Install additional tools
RUN apt-get update && \
    apt-get install -y wget curl git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# VS Code will handle workspace mounting automatically

# Set up Python virtual environment and install packages
RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Install Python packages
RUN pip install --upgrade pip && \
    pip install numpy pandas scikit-learn matplotlib seaborn jupyter datasets

# Verify installations
RUN java -version && \
    python --version && \
    pip --version