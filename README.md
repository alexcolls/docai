# 🤖 DocAI - Smart Document Processing Platform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Nuxt](https://img.shields.io/badge/Nuxt-3.12.1-00DC82?logo=nuxt.js)](https://nuxt.com/)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.4.27-4FC08D?logo=vue.js)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-latest-336791?logo=postgresql)](https://www.postgresql.org/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://openjdk.org/)
[![Bun](https://img.shields.io/badge/Bun-latest-000000?logo=bun)](https://bun.sh/)
[![OpenAI](https://img.shields.io/badge/OpenAI-GPT--4%20Vision-412991?logo=openai)](https://openai.com/)

A powerful, enterprise-grade document processing platform that leverages OpenAI's GPT-4 Vision API to intelligently extract, analyze, and structure information from various document formats. DocAI combines a robust Kotlin/Spring Boot backend with a modern Nuxt 3 frontend to deliver a seamless document intelligence experience.

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Development](#-development)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### Document Processing
- 📄 **Multi-Format Support**: Process PDF, PPT, PPTX, JPG, PNG, WEBP, and GIF documents
- 🔍 **AI-Powered Extraction**: Leverage GPT-4 Vision API for intelligent document analysis
- 📊 **Structured Output**: Extract data in JSON format with customizable schemas
- 🎯 **Configurable Document Types**: Define custom templates with specific prompts for different document categories
- 📝 **Sample-Based Training**: Improve accuracy by providing sample documents and expected outputs

### System Capabilities
- 🔄 **Background Processing**: Asynchronous job processing with JobRunr for scalable document handling
- 📈 **Progress Tracking**: Real-time status updates for document processing tasks
- 🌐 **Multi-Language Support**: Built-in internationalization (English, Spanish, French)
- 🔐 **Authentication**: Secure access control with Spring Security
- 🎨 **Modern UI**: Responsive design with dark/light theme support
- 📦 **File Management**: Advanced file upload with Uppy integration
- 🔄 **Auto-Retry Logic**: Intelligent handling of long responses with continuation support

### Developer Experience
- 📚 **API Documentation**: Interactive Swagger UI for easy API exploration
- 🐳 **Docker Ready**: Containerized deployment for both backend and frontend
- ☸️ **Kubernetes Support**: Production-ready K8s manifests and Helm charts
- 🔧 **CI/CD Pipelines**: Azure DevOps integration for automated builds and deployments
- 🧪 **Testing**: Comprehensive unit and integration test suites

## 🏗️ Architecture

DocAI follows a modern microservices architecture with clear separation of concerns:

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   Nuxt 3 SPA    │────────>│  Spring Boot API │────────>│   PostgreSQL    │
│  (Frontend)     │  REST   │    (Backend)     │  JPA    │   (Database)    │
└─────────────────┘         └──────────────────┘         └─────────────────┘
        │                            │
        │                            │
        v                            v
  ┌──────────┐              ┌─────────────────┐
  │  Bun.js  │              │  OpenAI GPT-4   │
  │ Runtime  │              │  Vision API     │
  └──────────┘              └─────────────────┘
                                     │
                                     v
                            ┌─────────────────┐
                            │     JobRunr     │
                            │  (Job Queue)    │
                            └─────────────────┘
```

### Key Components

#### Backend (SmartDoc API)
- **Document Service**: Handles file upload, storage, and retrieval
- **Vision Processor**: Converts documents to images and sends to OpenAI API
- **JSON Curator**: Extracts and validates JSON responses from AI outputs
- **Response Merger**: Intelligently combines multi-part AI responses
- **Result Manager**: Tracks processing status and stores results

#### Frontend (Smart Doc AI)
- **Upload Interface**: Drag-and-drop file upload with real-time preview
- **Document Type Manager**: Configure and manage document processing templates
- **Result Viewer**: Display processed results with JSON visualization
- **Sample Manager**: Add training samples to improve extraction accuracy

## 🛠️ Tech Stack

### Backend
- **Language**: Kotlin 2.0.20
- **Framework**: Spring Boot 3.3.1
- **Java**: OpenJDK 21 (Eclipse Temurin)
- **Database**: PostgreSQL (with Flyway migrations)
- **ORM**: Spring Data JPA / Hibernate
- **Job Processing**: JobRunr 7.3.2
- **AI Integration**: OpenAI Client 3.7.2
- **Document Processing**: Apache PDFBox 2.0.24, Apache POI 5.2.3
- **API Documentation**: SpringDoc OpenAPI 2.5.0
- **Build Tool**: Gradle (Kotlin DSL)
- **Testing**: JUnit 5, Spring Boot Test

### Frontend
- **Framework**: Nuxt 3.12.1
- **UI Library**: Vue 3.4.27
- **Language**: TypeScript
- **Runtime**: Bun.js
- **UI Components**: Nuxt UI 2.21.0 (built on Tailwind CSS)
- **State Management**: Pinia with persistence
- **File Upload**: Uppy 3.x
- **Internationalization**: @nuxtjs/i18n 8.3.1
- **HTTP Client**: Built-in Fetch API
- **Icons**: Iconify with multiple icon sets

### Infrastructure
- **Container Runtime**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: Azure DevOps Pipelines
- **Reverse Proxy**: Nginx (via K8s Ingress)
- **Monitoring**: Spring Boot Actuator

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

### Required
- **Java 21** (OpenJDK or Oracle JDK)
- **Node.js** 18+ or **Bun** 1.0+
- **PostgreSQL** 14+
- **Git**
- **OpenAI API Key** (for GPT-4 Vision access)

### Optional
- **Docker** 24+ and **Docker Compose** 2.0+ (for containerized deployment)
- **Kubernetes** 1.28+ (for production deployment)
- **kubectl** (for K8s management)

## 📦 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/docai.git
cd docai
```

### 2. Backend Setup

#### Database Setup

Create a PostgreSQL database for the application:

```bash
# Pull the latest PostgreSQL image
docker pull postgres:latest

# Run PostgreSQL container
docker run -itd \
  -e POSTGRES_USER=smartdoc \
  -e POSTGRES_PASSWORD=smartdoc \
  -p 5432:5432 \
  --name postgresql \
  postgres

# Create the database
docker exec -it postgresql psql -U smartdoc -c "CREATE DATABASE dbinnocvsmartdocdev ENCODING 'UTF8'"
```

#### Environment Configuration

Create an `.env` file in the `backend/` directory (or use `.env.sample` as a template):

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dbinnocvsmartdocdev
SPRING_DATASOURCE_USERNAME=smartdoc
SPRING_DATASOURCE_PASSWORD=smartdoc

# OpenAI Configuration
OPENAI_API_KEY=sk-your-openai-api-key-here
SMART_DOC_OPENAI_MODEL_ID=gpt-4-vision-preview

# Document Storage
DOCUMENT_FOLDER=/path/to/document/storage
HOME=/path/to/document/storage

# Spring Profile
SPRING_PROFILES_ACTIVE=local
```

#### Build and Run

```bash
cd backend

# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run the application
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

The backend API will be available at `http://localhost:8080/smartdoc-api`

### 3. Frontend Setup

#### Install Dependencies

```bash
cd frontend

# Using Bun (recommended)
bun install

# Or using npm
npm install
```

#### Environment Configuration

Create a `.env.dev` file in the `frontend/` directory (or use the existing one as template):

```env
VERSION=1.0.0
API_URL=http://localhost:8080/smartdoc-api/
MAX_FILE_SIZE=1073741824
REFRESH_INTERVAL=10000

# Demo credentials (for development only)
USER=demo
PASS=your-password-here
```

#### Run Development Server

```bash
# Using Bun
bun run dev

# Or using npm
npm run dev
```

The frontend will be available at `http://localhost:3000`

## ⚙️ Configuration

### Backend Configuration

Key configuration properties (in `application.yml` or environment variables):

| Property | Description | Default |
|----------|-------------|--------|
| `smart-doc.openai.api-key` | OpenAI API key for GPT-4 Vision | Required |
| `smart-doc.openai.model-id` | OpenAI model to use | `gpt-4-vision-preview` |
| `document.folder` | Path to store uploaded documents | Required |
| `spring.datasource.url` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/...` |
| `spring.datasource.username` | Database username | `smartdoc` |
| `spring.datasource.password` | Database password | `smartdoc` |
| `server.port` | Backend server port | `8080` |

### Frontend Configuration

Environment variables (in `.env.dev` or `.env`):

| Variable | Description | Default |
|----------|-------------|--------|
| `API_URL` | Backend API base URL | `https://apps.innocv.com/smartdoc-api/` |
| `MAX_FILE_SIZE` | Maximum upload file size (bytes) | `1073741824` (1GB) |
| `REFRESH_INTERVAL` | Result polling interval (ms) | `10000` (10s) |
| `USER` | Demo username | `demo` |
| `PASS` | Demo password | - |

## 🚀 Running the Application

### Development Mode

#### Terminal 1 - Backend
```bash
cd backend
SPRING_PROFILES_ACTIVE=local OPENAI_API_KEY=your-key HOME=/tmp ./gradlew bootRun
```

#### Terminal 2 - Frontend
```bash
cd frontend
bun run dev
```

Access the application:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/smartdoc-api
- **Swagger UI**: http://localhost:8080/smartdoc-api/swagger-ui.html
- **JobRunr Dashboard**: http://localhost:8000/dashboard/

### Docker Deployment

#### Build Docker Images

```bash
# Backend
cd backend
./gradlew build
docker build -t docai-backend:latest .

# Frontend
cd ../frontend
docker build -t docai-frontend:latest .
```

#### Run with Docker Compose (if available)

```bash
docker-compose up -d
```

### Production Build

#### Backend
```bash
cd backend
./gradlew clean build -x test
java -jar build/libs/smartdocApi-*.jar
```

#### Frontend
```bash
cd frontend
bun run build
bun run preview
```

## 📚 API Documentation

### Swagger UI

Interactive API documentation is available at:
```
http://localhost:8080/smartdoc-api/swagger-ui.html
```

### Health Checks

```bash
# Overall health
GET /smartdoc-api/actuator/health

# Liveness probe
GET /smartdoc-api/actuator/health/liveness
```

### Main Endpoints

#### Documents
- `POST /documents` - Upload a document
- `GET /documents/{id}` - Get document metadata
- `GET /documents/{id}/download` - Download original document
- `GET /documents/{id}/preview` - Get document preview (PNG for PDFs)

#### Document Types
- `GET /document-types` - List all document types
- `POST /document-types` - Create a new document type
- `PUT /document-types/{id}` - Update document type
- `DELETE /document-types/{id}` - Delete document type

#### Results
- `POST /results` - Process a document with a document type
- `GET /results/{id}` - Get processing result
- `GET /results?documentId={id}` - Get results for a document

#### Samples
- `POST /samples` - Add a training sample
- `DELETE /samples/{id}` - Delete a sample

### Authentication

The API uses Spring Security. Include credentials in your requests:

```bash
curl -u username:password http://localhost:8080/smartdoc-api/documents
```

## 📁 Project Structure

```
docai/
├── backend/                      # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/innocv/smartdoc/
│   │   │   │   ├── api/
│   │   │   │   │   ├── configuration/    # Security, CORS config
│   │   │   │   │   ├── controllers/      # REST endpoints
│   │   │   │   │   └── dto/              # Data transfer objects
│   │   │   │   ├── domain/               # JPA entities
│   │   │   │   ├── exceptions/           # Custom exceptions
│   │   │   │   ├── repositories/         # Data access layer
│   │   │   │   ├── services/             # Business logic
│   │   │   │   ├── usecases/             # Application use cases
│   │   │   │   └── utils/                # Utility classes
│   │   │   └── resources/
│   │   │       ├── db/migration/         # Flyway migrations
│   │   │       └── application.yml       # Configuration
│   │   └── test/                         # Unit & integration tests
│   ├── build.gradle.kts                  # Gradle build configuration
│   ├── Dockerfile                        # Backend container image
│   └── .deploy/                          # K8s & Azure DevOps configs
│
├── frontend/                     # Nuxt 3 frontend
│   ├── components/
│   │   ├── Auth/                         # Authentication components
│   │   ├── Common/                       # Reusable UI components
│   │   ├── Home/                         # Home page components
│   │   ├── Layout/                       # Layout components
│   │   ├── Modal/                        # Modal dialogs
│   │   └── Upload/                       # File upload components
│   ├── composables/                      # Vue composables
│   ├── layouts/                          # Page layouts
│   ├── locales/                          # i18n translations
│   ├── middleware/                       # Route middleware
│   ├── pages/                            # Route pages
│   ├── stores/                           # Pinia stores
│   ├── types/                            # TypeScript definitions
│   ├── utils/                            # Utility functions
│   ├── app.config.ts                     # App configuration
│   ├── nuxt.config.ts                    # Nuxt configuration
│   ├── package.json                      # Dependencies
│   ├── Dockerfile                        # Frontend container image
│   └── .deploy/                          # K8s & Azure DevOps configs
│
└── README.md                     # This file
```

## 🔧 Development

### Running Tests

#### Backend Tests
```bash
cd backend
./gradlew test                    # Run unit tests
./gradlew integrationTest         # Run integration tests (if configured)
./gradlew jacocoTestReport        # Generate coverage report
```

#### Frontend Tests
```bash
cd frontend
bun test                          # Run tests (if configured)
```

### Code Quality

#### Backend
```bash
# Run Kotlin linter (if configured)
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat
```

#### Frontend
```bash
# ESLint check
bun run lint

# Type checking
bun run typecheck
```

### Database Management

#### Drop Database
```bash
docker exec -it postgresql psql -U smartdoc -c "DROP DATABASE dbinnocvsmartdocdev"
```

#### Access Database
```bash
docker exec -it postgresql psql -U smartdoc -d dbinnocvsmartdocdev
```

### JobRunr Dashboard

Access the job processing dashboard (in dev environment):

```bash
# Port-forward if running in Kubernetes
kubectl port-forward svc/smartdoc-api 8001:8000 -n dev

# Access at
http://localhost:8001/dashboard/
```

## 🚢 Deployment

### Kubernetes Deployment

The project includes complete Kubernetes manifests:

```bash
# Backend deployment
kubectl apply -f backend/.deploy/k8s-resources/

# Frontend deployment
kubectl apply -f frontend/.deploy/k8s-resources/
```

Key resources:
- **Namespace**: Isolated environment per stage (dev/staging/prod)
- **ConfigMap**: Non-sensitive configuration
- **Secret**: Sensitive data (API keys, passwords)
- **Deployment**: Application pods with rolling updates
- **Service**: Internal cluster networking
- **Ingress**: External access with TLS
- **HPA**: Horizontal Pod Autoscaling (backend only)

### Azure DevOps Pipelines

CI/CD pipelines are configured for:

- **PR Validation** (`azure-pipelines-pr.yml`)
- **Continuous Deployment** (`azure-pipelines-cd.yml`)
- **Release Management** (`azure-pipelines-rls.yml`)

Pipeline stages:
1. **Build**: Compile and package the application
2. **Quality**: Run tests and code analysis
3. **Deploy**: Deploy to Kubernetes cluster

### Environment Variables by Stage

Configuration is managed via:
- `vars/common-vars.yaml` - Shared across all environments
- `vars/development-vars.yaml` - Development-specific
- `vars/staging-vars.yaml` - Staging-specific
- `vars/production-vars.yaml` - Production-specific

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes with descriptive messages
4. **Push** to your branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Coding Standards

- **Backend**: Follow Kotlin coding conventions and Spring Boot best practices
- **Frontend**: Use TypeScript strictly, follow Vue 3 Composition API patterns
- **Commits**: Use conventional commits with emojis (e.g., `✨ feat: add new feature`)
- **Tests**: Write tests for new features and bug fixes
- **Documentation**: Update README and inline docs for significant changes

## 📄 License

This project is proprietary software developed by INNOCV Solutions. All rights reserved.

For licensing inquiries, please contact: [your-email@innocv.com]

## 📧 Support

For issues, questions, or contributions:

- **Issues**: [GitHub Issues](https://github.com/your-org/docai/issues)
- **Email**: support@innocv.com
- **Documentation**: [Wiki](https://github.com/your-org/docai/wiki)

---

**Made with ❤️ by INNOCV Solutions**
