#!/bin/bash

# Docker deployment script for SysML v2 API Services

set -e

echo "🐳 Starting SysML v2 API Services with Docker Compose..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop first."
    exit 1
fi

# Create logs directory if it doesn't exist
mkdir -p logs

# Build and start the services
echo "📦 Building and starting services..."
docker-compose up --build -d

echo "⏳ Waiting for services to start..."
sleep 10

# Check if services are running
if docker-compose ps | grep -q "Up"; then
    echo "✅ Services are running!"
    echo ""
    echo "🌐 Application URLs:"
    echo "   - API: http://localhost:9000"
    echo "   - API Documentation: http://localhost:9000/docs/"
    echo "   - PostgreSQL: localhost:5432"
    echo ""
    echo "📊 Service Status:"
    docker-compose ps
    echo ""
    echo "📝 To view logs: docker-compose logs -f"
    echo "🛑 To stop: docker-compose down"
else
    echo "❌ Services failed to start. Checking logs..."
    docker-compose logs
fi