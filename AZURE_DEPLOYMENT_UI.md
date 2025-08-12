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
  --name "db-user" --value "postgres"
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
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
cd YOUR_REPOSITORY

# Login to your Azure Container Registry
az acr login --name $ACR_NAME

# Build and push directly from GitHub using ACR Build
az acr build --registry $ACR_NAME \
  --image sysml-api:latest \
  --file Dockerfile .

# Alternatively, if you want to build from a GitHub repo directly:
# az acr build --registry $ACR_NAME \
#   --image sysml-api:latest \
#   https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
```

## Phase 5: Create Container Apps Environment via UI

### 9. Create Container Apps Environment
1. Go to **Azure Portal** → **Container Apps** → **Create**
2. On **Basics** tab:
   - **Resource group**: `287013_Scalable_AI_Applications`
   - **Container app name**: `sysml-api`
   - **Region**: West Europe
3. On **Container Apps Environment**:
   - Select **Create new**
   - **Environment name**: `sysml-env`
4. Continue to **App settings** tab without completing the container app creation yet

## Phase 6: Deploy Container App via Cloud Shell

### 10. Deploy the Application via Cloud Shell
```bash
# Replace these with your actual resource names
RESOURCE_GROUP="287013_Scalable_AI_Applications"
ACR_NAME="sysmlacr2024"  # Your Container Registry name
KEYVAULT_NAME="sysml-kv-2024"  # Your Key Vault name
CONTAINER_ENV="sysml-env"
CONTAINER_APP="sysml-api"

# Get ACR details
ACR_SERVER=$(az acr show --name $ACR_NAME --query loginServer --output tsv)
ACR_USERNAME=$(az acr credential show --name $ACR_NAME --query username --output tsv)
ACR_PASSWORD=$(az acr credential show --name $ACR_NAME --query passwords[0].value --output tsv)

# Create the container app
az containerapp create \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --environment $CONTAINER_ENV \
  --image $ACR_SERVER/sysml-api:latest \
  --registry-server $ACR_SERVER \
  --registry-username $ACR_USERNAME \
  --registry-password $ACR_PASSWORD \
  --target-port 9000 \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 10 \
  --cpu 1.0 \
  --memory 2Gi \
  --secrets \
    "db-host=keyvaultref:https://$KEYVAULT_NAME.vault.azure.net/secrets/db-host,identityref:system" \
    "db-port=keyvaultref:https://$KEYVAULT_NAME.vault.azure.net/secrets/db-port,identityref:system" \
    "db-name=keyvaultref:https://$KEYVAULT_NAME.vault.azure.net/secrets/db-name,identityref:system" \
    "db-user=keyvaultref:https://$KEYVAULT_NAME.vault.azure.net/secrets/db-user,identityref:system" \
    "db-password=keyvaultref:https://$KEYVAULT_NAME.vault.azure.net/secrets/db-password,identityref:system" \
    "play-secret=keyvaultref:https://$KEYVAULT_NAME.vault.azure.net/secrets/play-http-secret-key,identityref:system" \
  --env-vars \
    "DB_HOST=secretref:db-host" \
    "DB_PORT=secretref:db-port" \
    "DB_NAME=secretref:db-name" \
    "DB_USER=secretref:db-user" \
    "DB_PASSWORD=secretref:db-password" \
    "PLAY_HTTP_SECRET_KEY=secretref:play-secret" \
    "JAVA_OPTS=-Xmx1024m -Xms512m"
```

## Phase 7: Configure Security

### 11. Enable System-Assigned Identity via Cloud Shell
```bash
# Replace these with your actual resource names
RESOURCE_GROUP="287013_Scalable_AI_Applications"
CONTAINER_APP="sysml-api"
KEYVAULT_NAME="sysml-kv-2024"  # Your Key Vault name

# Enable managed identity
az containerapp identity assign \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --system-assigned

# Get the managed identity
CONTAINER_APP_IDENTITY=$(az containerapp identity show \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --query principalId --output tsv)

# Grant Key Vault access
az keyvault set-policy \
  --name $KEYVAULT_NAME \
  --object-id $CONTAINER_APP_IDENTITY \
  --secret-permissions get list
```

## Phase 8: Verification & Testing

### 12. Get Application URL and Test
```bash
# Replace these with your actual resource names
RESOURCE_GROUP="287013_Scalable_AI_Applications"
CONTAINER_APP="sysml-api"

# Get the application URL
APP_URL=$(az containerapp show \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --query properties.latestRevisionFqdn \
  --output tsv)

echo "Application URL: https://$APP_URL"
echo "API Documentation: https://$APP_URL/docs/"

# Test the application
curl -f "https://$APP_URL/" || echo "Application starting up..."
```

### 13. Monitor Application Logs
```bash
# Replace these with your actual resource names
RESOURCE_GROUP="287013_Scalable_AI_Applications"
CONTAINER_APP="sysml-api"

# View application logs
az containerapp logs show \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --follow
```

## Summary

This approach combines the convenience of Azure Portal UI for resource creation with the power of Azure Cloud Shell for configuration and deployment. Key benefits:

- **UI Creation**: Easy visual setup of core resources
- **Cloud Shell**: Powerful scripting for complex configurations
- **GitHub Integration**: Direct builds from your repository
- **Existing Resource Group**: Uses your existing `287013_Scalable_AI_Applications` group

## Next Steps

1. Access your application at the URL provided by the Cloud Shell
2. Test the API endpoints at `/docs/`
3. Monitor performance and adjust scaling as needed
4. Set up CI/CD for automatic deployments from GitHub

## Troubleshooting

- Check Container Apps logs in Azure Portal under **Log stream**
- Verify Key Vault access policies if secrets aren't accessible
- Ensure PostgreSQL firewall rules allow Container Apps access
- Monitor costs in Azure Cost Management