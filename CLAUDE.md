# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the SysML v2 REST/HTTP API and Services pilot implementation, a Java web application built with the Play Framework. It provides a REST API for managing SysML v2 (Systems Modeling Language) projects, elements, relationships, and metamodel data.

## Common Development Commands

### Build and Run
- `sbt clean` - Clean the project
- `sbt run` - Compile and run the application (starts on port 9000)
- `sbt test` - Run tests
- `sbt compile` - Compile the project

### Database Setup
The application requires PostgreSQL. To set up with Docker:
```bash
docker run --name sysml2-postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=sysml2 -d postgres
```

### Cleaning Generated Files
- `./clean.sh` - Removes generated metamodel files and cleans up generated directories

### Application Access
- **Local Development**: `http://localhost:9000`
- **Local API Documentation**: `http://localhost:9000/docs/`
- **Azure Production**: `https://sysml-api-webapp-2024.azurewebsites.net/`
- **Azure API Documentation**: `https://sysml-api-webapp-2024.azurewebsites.net/docs/`

## Architecture Overview

### Core Components

**Controllers** (`app/controllers/`):
- REST API endpoints following the layered architecture pattern
- All controllers extend `BaseController` which provides pagination utilities
- Key controllers: ProjectController, ElementController, CommitController, BranchController

**Services** (`app/services/`):
- Business logic layer between controllers and data access
- All services extend `BaseService<I, D>` providing standard CRUD operations

**Data Access Layer** (`app/dao/`):
- Repository pattern implementation with JPA/Hibernate
- All DAOs implement the generic `Dao<E>` interface
- JPA implementations in `dao/impl/jpa/` package

**Domain Model** (`app/org/omg/sysml/`):
- Comprehensive SysML v2 metamodel implementation
- Four main packages:
  - `metamodel/` - Core SysML v2 metamodel classes (200+ classes)
  - `lifecycle/` - Project lifecycle management (Project, Branch, Commit, etc.)
  - `data/` - External data and relationship handling
  - `query/` - Query constraint system

### Technology Stack

- **Framework**: Play Framework 2.7.1
- **Database**: PostgreSQL with Hibernate ORM
- **Java Version**: JDK 11
- **Build Tool**: sbt
- **API Documentation**: Swagger/OpenAPI
- **JSON Processing**: Jackson with Hibernate integration
- **Dependency Injection**: Google Guice

### Key Configuration Files

- `conf/application.conf` - Play framework configuration
- `conf/routes` - API route definitions
- `conf/META-INF/persistence.xml` - JPA/Hibernate configuration
- `app/Module.java` - Dependency injection bindings

### Database Schema

The application uses Hibernate's `create-drop` mode, recreating the database schema on each startup. All SysML v2 metamodel classes are mapped as JPA entities with extensive class hierarchy.

### Generated Code

The project includes generated content in:
- `generated/` - Generated JPA metamodel classes
- `conf/json/schema/` - JSON schemas for API validation
- `public/jsonld/` - JSON-LD context files

### API Structure

The REST API follows these patterns:
- Project-based resource hierarchy: `/projects/{projectId}/...`
- Commit-based versioning: `/projects/{projectId}/commits/{commitId}/elements`
- Pagination support with cursor-based navigation
- JSON-LD support for semantic data exchange

### Development Notes

- The application implements the full SysML v2 metamodel specification
- Extensive use of JPA inheritance mapping for the metamodel hierarchy
- Jackson custom serializers handle JPA entity serialization complexities
- CORS and CSRF protection disabled for API access
- Swagger integration provides interactive API documentation

## Azure Deployment

### Production Environment
The application is successfully deployed on Azure Web Apps with PostgreSQL:
- **Web App**: `sysml-api-webapp-2024.azurewebsites.net`
- **Database**: Azure Database for PostgreSQL - Flexible Server (`sysml-postgres-2024`)
- **Schema**: 8,207 tables (complete SysML v2 metamodel)
- **Memory**: 2GB RAM allocation (Basic B2 App Service Plan)
- **Startup Time**: ~10.5 minutes (due to extensive schema creation)

### Azure Configuration Requirements
- **Container Timeout**: 900 seconds (15 minutes)
- **JVM Options**: `-Xmx2048m -Xms1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=100`
- **Database SSL**: Required (`sslmode=require`)
- **Environment Variables**: All database connection parameters via Azure App Settings

### Common Azure Deployment Issues
1. **Startup Timeouts**: SysML v2 metamodel requires extended startup time due to 8,207+ table creation
2. **Memory Constraints**: Default 1GB RAM insufficient; requires 2GB+ for full metamodel
3. **Host Configuration**: Play Framework requires allowed hosts configuration for Azure hostname
4. **SSL Connectivity**: PostgreSQL must use SSL mode for Azure Database connections

### Azure Monitoring
```bash
# Check application status
az webapp show --name sysml-api-webapp-2024 --resource-group "287013_Scalable_AI_Applications"

# Monitor logs
az webapp log download --name sysml-api-webapp-2024 --resource-group "287013_Scalable_AI_Applications"
```