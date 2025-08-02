#!/bin/bash

set -e

echo "Starting Course Pick Backend..."

# 1. Start MySQL using docker-compose (if not already running)
echo "🐳 Starting MySQL container..."
cd "$(dirname "$0")"
if ! docker compose -f docker-compose.local.yml ps mysql | grep -q "Up"; then
    docker compose -f docker-compose.local.yml up -d mysql
    echo "⏳ Waiting for MySQL to be ready..."
    sleep 10
else
    echo "✅ MySQL is already running"
fi

# 2. Build with Gradle
echo "🔨 Building with Gradle..."
cd ../
./gradlew build -x test

# 3. Run Spring Boot with local profile
echo "🚀 Starting Spring Boot application with local profile..."
./gradlew bootRun --args='--spring.profiles.active=local'
