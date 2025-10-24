#!/bin/bash

# Setup script for Codespaces - Install Java 21 and fix JAR compatibility

echo "=== Setting up Codespace for Java ML Project ==="
echo

# Update package list
echo "📦 Updating package list..."
apt-get update

# Install Java 21 JDK
echo "☕ Installing Java 21 JDK..."
apt-get install -y openjdk-21-jdk

# Set Java 21 as default
echo "🔧 Setting Java 21 as default..."
update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-21-openjdk-amd64/bin/java 1
update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-21-openjdk-amd64/bin/javac 1

# Set environment variables
echo "🌍 Setting environment variables..."
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"

# Add to bashrc for persistence
echo 'export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc

# Verify installation
echo "✅ Verifying Java installation..."
java -version
javac -version

echo
echo "🎉 Setup complete! Java 21 is now installed and configured."
echo "You can now run: ./run_with_local_jars.sh"