#!/bin/bash

# Simple test script for SysML v2 API

API_URL="http://localhost:9000"

echo "🧪 Testing SysML v2 API..."

# Wait for API to be ready
echo "⏳ Waiting for API to be ready..."
timeout=60
count=0
while [ $count -lt $timeout ]; do
    if curl -s -f "${API_URL}/" > /dev/null 2>&1; then
        echo "✅ API is ready!"
        break
    fi
    echo "Waiting... ($count/$timeout)"
    sleep 2
    count=$((count + 2))
done

if [ $count -ge $timeout ]; then
    echo "❌ API failed to start within $timeout seconds"
    exit 1
fi

echo ""
echo "🔍 Running basic API tests..."

# Test 1: Root endpoint
echo "1️⃣ Testing root endpoint..."
if curl -s -f "${API_URL}/" > /dev/null; then
    echo "   ✅ Root endpoint accessible"
else
    echo "   ❌ Root endpoint failed"
fi

# Test 2: API documentation
echo "2️⃣ Testing API documentation..."
if curl -s -f "${API_URL}/docs/" > /dev/null; then
    echo "   ✅ API documentation accessible"
else
    echo "   ❌ API documentation failed"
fi

# Test 3: Projects endpoint
echo "3️⃣ Testing projects endpoint..."
if curl -s -f "${API_URL}/projects" > /dev/null; then
    echo "   ✅ Projects endpoint accessible"
else
    echo "   ❌ Projects endpoint failed"
fi

# Test 4: Meta endpoint
echo "4️⃣ Testing meta endpoint..."
if curl -s -f "${API_URL}/meta/datatypes" > /dev/null; then
    echo "   ✅ Meta endpoint accessible"
else
    echo "   ❌ Meta endpoint failed"
fi

echo ""
echo "🎉 Basic API tests completed!"
echo "📖 Visit ${API_URL}/docs/ for full API documentation"