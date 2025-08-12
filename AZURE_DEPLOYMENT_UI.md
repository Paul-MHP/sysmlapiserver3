# Azure Deployment Guide for SysML v2 API (UI + Cloud Shell)

This guide provides instructions for deploying the SysML v2 API application using Azure Portal UI for resource creation and Azure Cloud Shell for configuration and Docker builds from GitHub.

## Architecture Overview

**Azure Services:**
- **Application**: Azure Container Apps (serverless containers with auto-scaling)
- **Database**: Azure Database for PostgreSQL - Flexible Server
- **Secrets**: Azure Key Vault
- **Registry**: Azure Container Registry
- **Estimated Cost**: $150-300/month

## Prerequisites

- Existing Azure Resource Group: `287013_Scalable_AI_Applications`
- GitHub repository with the SysML v2 API code
- Azure account with appropriate permissions

## Phase 1: Azure Cloud Shell Setup

### 1. Open Azure Cloud Shell
1. Navigate to [Azure Portal](https://portal.azure.com)
2. Click the Cloud Shell icon (>_) in the top navigation bar
3. Choose Bash environment

### 2. Prepare Resource Names
You'll need to create unique names for your Azure resources. Use these naming patterns:
- **Container Registry**: `sysmlacr[unique-suffix]` (e.g., `sysmlacr2024`)
- **Database Server**: `sysml-postgres-[unique-suffix]` (e.g., `sysml-postgres-2024`)
- **Key Vault**: `sysml-kv-[unique-suffix]` (e.g., `sysml-kv-2024`)
- **Container Environment**: `sysml-env`
- **Container App**: `sysml-api`

Choose your unique suffix and remember it for all resources.

## Phase 2: Create Resources via Azure Portal UI

### 3. Create Azure Container Registry via UI
1. Go to **Azure Portal** → **Container registries** → **Create**
2. Fill in the details:
   - **Resource group**: `287013_Scalable_AI_Applications`
   - **Registry name**: Your chosen ACR name (e.g., `sysmlacr2024`)
   - **Location**: West Europe
   - **SKU**: Basic
3. Click **Review + create** → **Create**

### 4. Create Key Vault via UI
1. Go to **Azure Portal** → **Key vaults** → **Create**
2. Fill in the details:
   - **Resource group**: `287013_Scalable_AI_Applications`
   - **Key vault name**: Your chosen Key Vault name (e.g., `sysml-kv-2024`)
   - **Region**: West Europe
   - **Pricing tier**: Standard
3. Click **Review + create** → **Create**
   - Note: The default access configuration will work for our setup

### 5. Create PostgreSQL Database via UI
1. Go to **Azure Portal** → **Azure Database for PostgreSQL** → **Create**
2. Choose **Flexible server**
3. Fill in the details:
   - **Resource group**: `287013_Scalable_AI_Applications`
   - **Server name**: Your chosen database server name (e.g., `sysml-postgres-2024`)
   - **Region**: West Europe
   - **PostgreSQL version**: 13
   - **Workload type**: Development
   - **Compute + storage**: Burstable, Standard_B2s, 128 GB storage
   - **Admin username**: `paulschiel`
   - **Password**: `postgre1!`
4. On **Networking** tab:
   - **Connectivity method**: Public access (selected IP addresses)
   - **Add current client IP address**: Check this box
   - **Allow public access from any Azure service**: Check this box
5. Click **Review + create** → **Create**

### 6. Create Database in PostgreSQL Server
1. After PostgreSQL server is created, go to the server resource
2. Go to **Databases** in the left menu
3. Click **Add** and create a database named `sysml2`

## Phase 3: Configure Secrets in Cloud Shell

### 7. Store Database Configuration in Key Vault
```bash
# Replace these placeholders with your actual resource names and values
KEYVAULT_NAME="sysml-kv-2024"  # Your Key Vault name
DB_SERVER_NAME="sysml-postgres-2024"  # Your PostgreSQL server name
DB_PASSWORD="postgre1!"  # The password you created in the UI

# Set the database host
DB_HOST="$DB_SERVER_NAME.postgres.database.azure.com"

# Store secrets in Key Vault
az keyvault secret set --vault-name $KEYVAULT_NAME \
  --name "db-host" --value "$DB_HOST"
az keyvault secret set --vault-name $KEYVAULT_NAME \
  --name "db-port" --value "5432"
az keyvault secret set --vault-name $KEYVAULT_NAME \
  --name "db-name" --value "sysml2"
az keyvault secret set --vault-name $KEYVAULT_NAME \
  --name "db-user" --value "paulschiel"
az keyvault secret set --vault-name $KEYVAULT_NAME \
  --name "db-password" --value "$DB_PASSWORD"
az keyvault secret set --vault-name $KEYVAULT_NAME \
  --name "play-http-secret-key" --value "$(openssl rand -base64 32)"
```

## Phase 4: Build Docker Image from GitHub

### 8. Clone Repository and Build in Cloud Shell
```bash
# Replace with your actual resource names
ACR_NAME="sysmlacr2024"  # Your Container Registry name

# Clone your GitHub repository
git clone https://github.com/Paul-MHP/sysmlapiserver3.git
cd sysmlapiserver3

# Login to your Azure Container Registry
az acr login --name $ACR_NAME

# Build and push directly from GitHub using ACR Build
az acr build --registry $ACR_NAME \
  --image sysml-api:latest \
  --file Dockerfile .

# Alternatively, if you want to build from a GitHub repo directly:
# az acr build --registry $ACR_NAME \
#   --image sysml-api:latest \
#   https://github.com/Paul-MHP/sysmlapiserver3.git
```

## Phase 5: Deploy with Azure Container Instances (ACI)

### 9. Deploy Container Instance via Cloud Shell
Due to permission restrictions with Container Apps, we'll use Azure Container Instances (ACI) instead, which provides simpler deployment without autoscaling.

**Step 1: Basic Container Deployment**
```bash
# Create basic container instance (save to aci-deploy.txt for easy copying)
az container create --resource-group "287013_Scalable_AI_Applications" --name "sysml-api-aci" --image "sysmlacr2024.azurecr.io/sysml-api:latest" --registry-login-server "sysmlacr2024.azurecr.io" --registry-username "sysmlacr2024" --registry-password "YOUR_ACR_PASSWORD" --dns-name-label "sysml-api-test" --ports 9000 --cpu 0.25 --memory 0.5 --os-type Linux
```

**Step 2: Configure PostgreSQL Firewall**
```bash
# Allow Azure services to connect to PostgreSQL
az postgres flexible-server firewall-rule create --resource-group "287013_Scalable_AI_Applications" --name "sysml-postgres-2024" --rule-name "AllowAzureServices" --start-ip-address "0.0.0.0" --end-ip-address "255.255.255.255"
```

**Step 3: Create Container with Database Connectivity**
```bash
# Get the Play Framework secret key from Key Vault first
az keyvault secret show --vault-name "sysml-kv-2024" --name "play-http-secret-key" --query "value" --output tsv

# Create container with database environment variables (save to aci-ssl.txt for easy copying)
az container create --resource-group "287013_Scalable_AI_Applications" --name "sysml-api-ssl" --image "sysmlacr2024.azurecr.io/sysml-api:latest" --registry-login-server "sysmlacr2024.azurecr.io" --registry-username "sysmlacr2024" --registry-password "YOUR_ACR_PASSWORD" --dns-name-label "sysml-api-ssl" --ports 9000 --cpu 0.25 --memory 0.5 --os-type Linux --environment-variables DB_HOST="sysml-postgres-2024.postgres.database.azure.com" DB_PORT="5432" DB_NAME="sysml2" DB_USER="paulschiel" DB_PASSWORD="TestPW123!" PLAY_HTTP_SECRET_KEY="YOUR_PLAY_SECRET_KEY" JAVA_OPTS="-Xmx256m -Xms128m" DATABASE_URL="postgres://paulschiel:TestPW123%21@sysml-postgres-2024.postgres.database.azure.com:5432/sysml2?sslmode=require"
```

## Phase 6: Verification & Testing

### 10. Get Container Instance URL and Test
```bash
# Get the container instance details
az container show \
  --resource-group "287013_Scalable_AI_Applications" \
  --name "sysml-api-ssl" \
  --query "{FQDN:ipAddress.fqdn,ProvisioningState:provisioningState,RestartCount:containers[0].instanceView.restartCount}" \
  --output table

# The FQDN will be something like: sysml-api-ssl.westeurope.azurecontainer.io
```

**Access your application:**
- **Main Application**: `http://sysml-api-ssl.westeurope.azurecontainer.io:9000/`
- **API Documentation**: `http://sysml-api-ssl.westeurope.azurecontainer.io:9000/docs/`

### 11. Monitor Container Logs and Status
```bash
# View container logs
az container logs \
  --resource-group "287013_Scalable_AI_Applications" \
  --name "sysml-api-ssl"

# Check restart count (should be 0 after successful deployment)
az container show --name "sysml-api-ssl" --resource-group "287013_Scalable_AI_Applications" --query "{State:instanceView.state,RestartCount:containers[0].instanceView.restartCount,ExitCode:containers[0].instanceView.currentState.exitCode}"
```

## Summary

This approach combines Azure Portal UI for infrastructure setup with Azure Container Instances for simple, cost-effective deployment:

- **UI Creation**: Easy visual setup of PostgreSQL, Key Vault, and Container Registry
- **ACI Deployment**: Simple container hosting without complex orchestration
- **GitHub Integration**: Direct builds from your repository via Azure Container Registry
- **Cost-Effective**: Minimal resource allocation (0.25 vCPU, 0.5GB RAM)
- **No Auto-Scaling**: Perfect for testing and low-traffic scenarios

## Current Deployment Status

**Successfully Deployed Resources:**
- ✅ **Container Registry**: `sysmlacr2024` with Docker image
- ✅ **PostgreSQL Database**: `sysml-postgres-2024` with `sysml2` database
- ✅ **Key Vault**: `sysml-kv-2024` with database credentials and secrets
- ✅ **Container Instance**: `sysml-api-ssl` with database connectivity

**Application Access:**
- **Main Application**: `http://sysml-api-ssl.westeurope.azurecontainer.io:9000/`
- **Swagger API Documentation**: `http://sysml-api-ssl.westeurope.azurecontainer.io:9000/docs/`

## Troubleshooting

**Common Issues Resolved:**
1. **Container restart loops**: Fixed by adding PostgreSQL firewall rule and proper environment variables
2. **Database connectivity**: Resolved with SSL connection string and proper credentials
3. **Bash special characters**: Fixed by URL-encoding `!` character in DATABASE_URL (`%21`)
4. **Secret key consistency**: Using fixed Key Vault secret instead of random generation

**Monitoring Commands:**
```bash
# Check container status
az container show --name "sysml-api-ssl" --resource-group "287013_Scalable_AI_Applications" --query "{State:instanceView.state,RestartCount:containers[0].instanceView.restartCount}"

# View logs
az container logs --name "sysml-api-ssl" --resource-group "287013_Scalable_AI_Applications"
```