# **Docker Compose Guide**
[![Release](https://github.com/PCalvoGarcia/MTG/actions/workflows/release.yml/badge.svg)](https://github.com/PCalvoGarcia/MTG/actions/workflows/release.yml)
[![Test](https://github.com/PCalvoGarcia/MTG/actions/workflows/test.yml/badge.svg)](https://github.com/PCalvoGarcia/MTG/actions/workflows/test.yml)
[![Build](https://github.com/PCalvoGarcia/MTG/actions/workflows/build.yml/badge.svg)](https://github.com/PCalvoGarcia/MTG/actions/workflows/build.yml)
[![Docker Hub](https://img.shields.io/docker/v/pcalvo/mtg)](https://hub.docker.com/r/pcalvo/mtg)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)

This project includes multiple Docker Compose configurations for different purposes. Each configuration serves a specific use case and has different characteristics.

## **Available Configurations**

### **1. Production Environment** (`docker-compose-v2.yml`)

**Purpose**: Run the complete MTG application with database for production or development.

**Command**:
```bash

docker compose -f docker-compose-v2.yml up --build
```

**What it does**:
- Builds the Spring Boot application from `Dockerfile-v2`
- Starts a MySQL 8.0 database with persistent storage
- Exposes the application on port 8080
- Exposes the database on port 3307 (mapped from 3306)
- Uses `restart: unless-stopped` policy for high availability
- Includes health checks for both services
- Creates a custom network for service communication
- Integrates with Cloudinary for image storage
- Configures email notifications
ñ
### **2. Testing Environment** (`docker-compose-test.yml`)

**Purpose**: Run tests using the official Maven Docker image without building a custom image.

**Command**:
```bash

docker compose -f docker-compose-test.yml up --build
```

**What it does**:
- Uses the official `maven:3.9.11-eclipse-temurin-21` image
- Mounts the source code as a bind volume
- Runs `mvn clean test --no-transfer-progress` with test profile
- Uses H2 in-memory database for testing (no MySQL required)
- Automatically removes containers after execution
- Forces Spring profile to `test` mode


## **Quick Reference**

| Configuration | Use Case | Database Port | App Port | Restart Policy | Database Type |
|---------------|----------|---------------|----------|----------------|---------------|
| `docker-compose-v2.yml` | Production/Development | 3307 | 8080 | unless-stopped | MySQL 8.0 |
| `docker-compose-test.yml` | Testing | None | None | no | H2 in-memory |

## **Environment Variables**

### Production Configuration (`.env` file required):
```env
# Database Configuration
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=mtgbackend
MYSQL_USER=your_user
MYSQL_PASSWORD=your_password

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Email Configuration
EMAIL=your_email@example.com
EMAIL_PASSWORD=your_email_password
```

### Testing Configuration:
- `SPRING_PROFILES_ACTIVE=test`
- `DB_URL=""` (cleared, uses H2)
- `DB_USERNAME=""` (cleared, uses H2)
- `DB_PASSWORD=""` (cleared, uses H2)

## 🔄 **CI/CD Pipeline**

This project implements a complete CI/CD pipeline with GitHub Actions including:

### **1. Build Pipeline** (`.github/workflows/build.yml`)
- ✅ Checkout source code
- ✅ Setup Java 21 environment
- ✅ Cache Maven dependencies
- ✅ Compile and build project
- ✅ Generate JAR artifact

### **2. Test Pipeline** (`.github/workflows/test.yml`)
- ✅ Run unit tests
- ✅ Run integration tests
- ✅ Generate coverage reports
- ✅ H2 in-memory database for testing
- ✅ Parallel test execution

### **3. Release Pipeline** (`.github/workflows/release.yml`)
- ✅ Multi-stage Docker image build
- ✅ Automatic version tagging
- ✅ Push to Docker Hub
- ✅ Automated deployment

## 🐳 **Docker Hub**

The Docker image for this project is publicly available at:

**Docker Hub**: [pcalvo/mtg](https://hub.docker.com/r/pcalvo/mtg)

### Direct image usage:
```bash

docker pull pcalvo/mtg:latest
docker run -p 8080:8080 pcalvo/mtg:latest
```

## 📋 **Prerequisites**

- [Docker](https://www.docker.com/get-started) (version 20.10+)
- [Docker Compose](https://docs.docker.com/compose/install/) (version 2.0+)
- [Git](https://git-scm.com/)

## ⚙️ **Installation & Setup**

### 1. **Clone the repository:**
```bash

git clone https://github.com/PCalvoGarcia/MTG.git
cd MTG
git switch feature/devops-rael
```

### 2. **Run the application:**
```bash

# Production environment
docker compose -f docker-compose-v2.yml up -d

# Check services status
docker compose -f docker-compose-v2.yml ps
```

### 3. **Run tests:**
```bash

# Execute test suite
docker compose -f docker-compose-test.yml up --build
```

The application will be available at `http://localhost:8080`
