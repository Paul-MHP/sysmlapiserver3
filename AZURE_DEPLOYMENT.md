# Azure Deployment Guide for SysML v2 API

This guide provides step-by-step instructions for deploying the SysML v2 API application and PostgreSQL database to Azure using Azure Container Apps and Azure Database for PostgreSQL.

## Architecture Overview

**Recommended Azure Services:**
- **Application**: Azure Container Apps (serverless containers with auto-scaling)
- **Database**: Azure Database for PostgreSQL - Flexible Server
- **Secrets**: Azure Key Vault
- **Registry**: Azure Container Registry
- **Estimated Cost**: $150-300/month

## Phase 1: Prerequisites & Initial Setup

### 1. Install Required Tools
```bash
# Install Azure CLI
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Login to Azure
az login

# Install Docker (if not installed)
sudo apt-get update
sudo apt-get install docker.io
```

### 2. Set Environment Variables
```bash
export RESOURCE_GROUP="sysml-rg"
export LOCATION="eastus"
export ACR_NAME="sysmlacr$(date +%s)"
export DB_SERVER_NAME="sysml-postgres-$(date +%s)"
export KEYVAULT_NAME="sysml-kv-$(date +%s)"
export CONTAINER_ENV="sysml-env"
export CONTAINER_APP="sysml-api"
```

## Phase 2: Create Core Infrastructure

### 3. Create Resource Group
```bash
az group create --name $RESOURCE_GROUP --location $LOCATION
```

### 4. Create Azure Container Registry
```bash
az acr create --resource-group $RESOURCE_GROUP \
  --name $ACR_NAME \
  --sku Basic \
  --admin-enabled true
```

### 5. Create Key Vault
```bash
az keyvault create --resource-group $RESOURCE_GROUP \
  --name $KEYVAULT_NAME \
  --location $LOCATION \
  --enable-rbac-authorization false
```

### 6. Create PostgreSQL Database
```bash
# Create PostgreSQL server
az postgres flexible-server create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --location $LOCATION \
  --admin-user postgres \
  --admin-password "MySecure$(openssl rand -base64 12)" \
  --sku-name Standard_B2s \
  --tier Burstable \
  --version 13 \
  --storage-size 128 \
  --public-access 0.0.0.0

# Create database
az postgres flexible-server db create \
  --resource-group $RESOURCE_GROUP \
  --server-name $DB_SERVER_NAME \
  --database-name sysml2
```

## Phase 3: Store Secrets in Key Vault

### 7. Store Database Configuration
```bash
# Get database connection details
DB_HOST="$DB_SERVER_NAME.postgres.database.azure.com"
DB_PASSWORD="MySecure$(openssl rand -base64 12)"

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

## Phase 4: Build and Push Docker Image

### 8. Build and Push Application Image
```bash
# Login to ACR
az acr login --name $ACR_NAME

# Build and push the image
docker build -t $ACR_NAME.azurecr.io/sysml-api:latest .
docker push $ACR_NAME.azurecr.io/sysml-api:latest
```

## Phase 5: Deploy Container Apps

### 9. Create Container Apps Environment
```bash
az containerapp env create \
  --name $CONTAINER_ENV \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION
```

### 10. Deploy the Application
```bash
# Get ACR credentials
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

## Phase 6: Configure Security & Access

### 11. Enable System-Assigned Identity
```bash
az containerapp identity assign \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --system-assigned
```

### 12. Grant Key Vault Access
```bash
# Get the container app's managed identity
CONTAINER_APP_IDENTITY=$(az containerapp identity show \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --query principalId --output tsv)

# Grant access to Key Vault
az keyvault set-policy \
  --name $KEYVAULT_NAME \
  --object-id $CONTAINER_APP_IDENTITY \
  --secret-permissions get list
```

### 13. Configure Database Access
```bash
# Allow Container Apps to access PostgreSQL
az postgres flexible-server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --rule-name "AllowContainerApps" \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255
```

## Phase 7: Verification & Testing

### 14. Get Application URL and Test
```bash
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

### 15. Monitor Application Logs
```bash
# View application logs
az containerapp logs show \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP \
  --follow
```

## Phase 8: Production Hardening (Optional)

### 16. Configure Custom Domain (if needed)
```bash
# Add custom domain
az containerapp hostname add \
  --hostname your-domain.com \
  --name $CONTAINER_APP \
  --resource-group $RESOURCE_GROUP
```

### 17. Set up Monitoring
```bash
# Enable Application Insights
az monitor app-insights component create \
  --app sysml-insights \
  --location $LOCATION \
  --resource-group $RESOURCE_GROUP
```

## Deployment Summary

After successful deployment, you will have:

- **Application**: Azure Container Apps with auto-scaling (1-10 instances)
- **Database**: Azure Database for PostgreSQL - Flexible Server
- **Security**: Key Vault for secrets management
- **Registry**: Azure Container Registry for Docker images
- **Cost**: Estimated $150-300/month depending on usage

## Access Points

- **Main Application**: `https://{your-container-app-url}`
- **API Documentation**: `https://{your-container-app-url}/docs/`
- **Database**: Accessible only from the container app (private endpoint)

## Troubleshooting

### Common Issues

1. **Container App not starting**: Check logs with `az containerapp logs show`
2. **Database connection issues**: Verify firewall rules and Key Vault secrets
3. **Image pull errors**: Ensure ACR credentials are correct

### Useful Commands

```bash
# Check container app status
az containerapp show --name $CONTAINER_APP --resource-group $RESOURCE_GROUP

# Update environment variables
az containerapp update --name $CONTAINER_APP --resource-group $RESOURCE_GROUP \
  --set-env-vars "NEW_VAR=value"

# Scale the application
az containerapp update --name $CONTAINER_APP --resource-group $RESOURCE_GROUP \
  --min-replicas 2 --max-replicas 20
```

## Security Best Practices

- All secrets are stored in Azure Key Vault
- Database is accessible only from the application subnet
- Container registry uses managed identity for authentication
- HTTPS is enforced for all external communication
- Regular security updates should be applied to the container images

## Monitoring and Maintenance

- Monitor application performance through Azure Monitor
- Set up alerts for high CPU/memory usage
- Regularly update container images for security patches
- Review and rotate secrets periodically
- Monitor costs and optimize resource allocation as needed