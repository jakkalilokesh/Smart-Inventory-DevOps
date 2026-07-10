# SmartInventory & Enterprise DevSecOps Platform

SmartInventory is a production-quality 3-tier Java web application for managing inventory, products, categories, suppliers, and stock transactions. This repository has been elevated into a **complete end-to-end Enterprise DevSecOps Platform**, demonstrating standard continuous integration, continuous delivery, supply chain security, and runtime observability using exclusively open-source tools.

---

## 🚀 Enterprise DevSecOps Platform Overview

This project includes a fully production-ready, modular, and secured CI/CD pipeline deploying to Kubernetes (AWS EKS) with GitOps (ArgoCD).

### Key DevSecOps Features:
* **Shift-Left Security Static Analysis**: Static secret scanning (Gitleaks) and SAST code scanning (Semgrep) executing on every code check-in.
* **Vulnerability Scanning & SCA**: In-depth software composition analysis (OWASP Dependency Check) and OS package container scanning (Trivy).
* **Supply Chain Hardening**: Automatically generates Software Bill of Materials (SBOM) (Syft) in CycloneDX format, cryptographically signs the container images (Cosign), and attaches SBOM attestations.
* **Admission Control (Policy-as-Code)**: Runtime admission enforcement (Kyverno) validating signatures, registry sources, and Pod Security Standards (non-root execution, privilege escalation block).
* **GitOps Continuous Deployment**: Automated GitOps sync and self-healing (Argo CD) deploying custom Helm charts across isolated namespaces (Dev, QA, Staging, Production).
* **IaC & Manifest Hardening**: Manifest analysis (kube-score), infrastructure-as-code vulnerability scanning (Checkov), and security posture compliance (Kubescape).
* **Dynamic Analysis (DAST)**: Automated dynamic application security tests (OWASP ZAP) executing against the live staging environment.
* **Runtime Threat Detection**: Real-time Linux kernel monitoring (Falco) detecting unauthorized shell spawns, sensitive file access, and suspicious process executions.
* **Enterprise Observability Stack**: Centralized log aggregation (Loki/Promtail), scrapers (Prometheus), alert routing (Alertmanager) notifying Slack & Email, and rich dashboards (Grafana).

For full details on integration architecture, sequence flows, and troubleshooting, see the [Enterprise DevSecOps Architecture & Integration Guide](docs/architecture.md).

## 📋 Table of Contents

- [Overview](#overview)
- [Enterprise DevSecOps Pipeline](#enterprise-devsecops-pipeline)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Deployment](#deployment)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

SmartInventory is a comprehensive inventory management system built with Java 17, Maven, Servlets, JSP, and MySQL. It follows industry-standard architectural patterns including MVC, DAO, and Service Layer patterns to ensure maintainability, scalability, and security.


### Key Features

- **User Management**: Role-based access control (Admin, Manager, Staff)
- **Category Management**: Hierarchical category structure
- **Product Management**: Complete product lifecycle with SKU and barcode support
- **Supplier Management**: Supplier information and contact details
- **Inventory Operations**: Stock in, stock out, and stock adjustments
- **Dashboard**: Real-time statistics and low stock alerts
- **Activity Logging**: Comprehensive audit trail
- **Search & Filter**: Advanced search with pagination
- **Responsive Design**: Mobile-friendly UI with Bootstrap 5

## 🛠 Technology Stack

### Backend
- **Java 17**: Core programming language
- **Maven**: Build automation and dependency management
- **Apache Tomcat 9**: Application server
- **Java Servlets**: Web framework
- **JSP & JSTL**: Presentation technology
- **JDBC**: Database connectivity
- **Apache Commons DBCP2**: Connection pooling

### Database
- **MySQL 8**: Relational database

### Security
- **BCrypt**: Password hashing
- **Session Management**: Secure session handling
- **Input Validation**: XSS and SQL injection prevention

### Frontend
- **HTML5**: Markup language
- **CSS3**: Styling
- **Bootstrap 5**: UI framework
- **JavaScript (ES6)**: Client-side scripting
- **Font Awesome**: Icons

### Logging
- **Log4j2**: Logging framework

### DevOps
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **Jenkins**: CI/CD pipeline
- **Kubernetes**: Container orchestration

## 🏗 Architecture

### 3-Tier Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│  (JSP, HTML, CSS, JavaScript, Bootstrap 5)              │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    Business Layer                         │
│  (Servlets, Services, Business Logic)                    │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                           │
│  (DAO, JDBC, MySQL 8)                                   │
└─────────────────────────────────────────────────────────┘
```

### Design Patterns

- **MVC (Model-View-Controller)**: Separation of concerns
- **DAO (Data Access Object)**: Database abstraction
- **Service Layer**: Business logic encapsulation
- **DTO (Data Transfer Object)**: Data transfer between layers
- **Singleton**: Database connection pool management

### Project Structure

```
smartinventory/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/smartinventory/
│   │   │   │   ├── controller/     # Servlets
│   │   │   │   ├── service/        # Service interfaces
│   │   │   │   │   └── impl/       # Service implementations
│   │   │   │   ├── dao/            # DAO interfaces
│   │   │   │   │   └── impl/       # DAO implementations
│   │   │   │   ├── model/          # Entity classes
│   │   │   │   ├── dto/            # Data Transfer Objects
│   │   │   │   ├── util/           # Utility classes
│   │   │   │   ├── filter/         # Filters
│   │   │   │   ├── listener/       # Listeners
│   │   │   │   └── exception/      # Custom exceptions
│   │   ├── resources/
│   │   │   ├── db.properties       # Database configuration
│   │   │   └── log4j2.xml          # Logging configuration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── views/          # JSP pages
│   │       │   └── web.xml         # Web application descriptor
│   │       ├── css/                # Stylesheets
│   │       ├── js/                 # JavaScript files
│   │       └── index.jsp           # Welcome page
├── database/
│   └── schema.sql                  # Database schema
├── k8s/                           # Kubernetes manifests
├── Dockerfile                     # Docker configuration
├── docker-compose.yml             # Docker Compose configuration
├── Jenkinsfile                    # Jenkins CI/CD pipeline
└── pom.xml                        # Maven configuration
```

## 📦 Prerequisites

- **JDK 17** or higher
- **Maven 3.8+**
- **Apache Tomcat 9.x**
- **MySQL 8.x**
- **Git**

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/smartinventory.git
cd smartinventory
```

### 2. Database Setup

Create a MySQL database and run the schema:

```bash
mysql -u root -p < database/schema.sql
```

Or manually:

```sql
CREATE DATABASE smartinventory;
USE smartinventory;
SOURCE database/schema.sql;
```

### 3. Configure Database Connection

Edit `src/main/resources/db.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/smartinventory?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=your_password
db.pool.initialSize=5
db.pool.maxTotal=20
db.pool.maxIdle=10
db.pool.minIdle=5
db.pool.maxWaitMillis=10000
```

### 4. Build the Project

```bash
mvn clean package
```

This will generate the WAR file: `target/smartinventory.war`

### 5. Deploy to Tomcat

Copy the WAR file to Tomcat's webapps directory:

```bash
cp target/smartinventory.war $TOMCAT_HOME/webapps/
```

Or use the Tomcat Manager application.

## ⚙️ Configuration

### Database Configuration

Configure database connection in `src/main/resources/db.properties`.

### Logging Configuration

Adjust logging levels in `src/main/resources/log4j2.xml`.

### Application Constants

Modify application constants in `src/main/java/com/smartinventory/util/AppConstants.java`.

## 🏃 Running the Application

### Using Tomcat

1. Start Tomcat:
```bash
$TOMCAT_HOME/bin/startup.sh  # Linux/Mac
$TOMCAT_HOME/bin/startup.bat # Windows
```

2. Access the application:
```
http://localhost:8080/smartinventory/
```

### Using Docker Compose

```bash
docker-compose up -d
```

Access the application at `http://localhost:8080/`

### Using Maven

```bash
mvn tomcat7:run
```

## 🚢 Deployment

### Docker Deployment

Build the Docker image:

```bash
docker build -t smartinventory:latest .
```

Run the container:

```bash
docker run -p 8080:8080 smartinventory:latest
```

### Docker Compose Deployment

```bash
docker-compose up -d
```

### Kubernetes Deployment

Apply Kubernetes manifests:

```bash
kubectl apply -f k8s/
```

Check deployment status:

```bash
kubectl get pods
kubectl get services
```

### Jenkins CI/CD

The `Jenkinsfile` defines the CI/CD pipeline stages:
1. Checkout
2. Build
3. Test
4. Package
5. Security Scan
6. Build Docker Image
7. Push to Registry
8. Deploy

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/` | Login page |
| POST | `/auth/login` | Authenticate user |
| GET | `/auth/logout` | Logout user |

### Dashboard Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard` | Dashboard page |

### Category Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/categories/` | List categories |
| GET | `/categories/create` | Create category form |
| POST | `/categories/create` | Create category |
| GET | `/categories/edit/{id}` | Edit category form |
| POST | `/categories/update/{id}` | Update category |
| GET | `/categories/delete/{id}` | Delete category |

### Product Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/products/` | List products |
| GET | `/products/create` | Create product form |
| POST | `/products/create` | Create product |
| GET | `/products/edit/{id}` | Edit product form |
| POST | `/products/update/{id}` | Update product |
| GET | `/products/delete/{id}` | Delete product |

### Supplier Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/suppliers/` | List suppliers |
| GET | `/suppliers/create` | Create supplier form |
| POST | `/suppliers/create` | Create supplier |
| GET | `/suppliers/edit/{id}` | Edit supplier form |
| POST | `/suppliers/update/{id}` | Update supplier |
| GET | `/suppliers/delete/{id}` | Delete supplier |

### Inventory Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/inventory/` | Inventory overview |
| GET | `/inventory/stock-in` | Stock in form |
| POST | `/inventory/stock-in` | Process stock in |
| GET | `/inventory/stock-out` | Stock out form |
| POST | `/inventory/stock-out` | Process stock out |
| GET | `/inventory/adjust` | Adjustment form |
| POST | `/inventory/adjust` | Process adjustment |
| GET | `/inventory/history` | Transaction history |

### Profile Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/profile/` | View profile |
| GET | `/profile/edit` | Edit profile form |
| POST | `/profile/update` | Update profile |
| GET | `/profile/change-password` | Change password form |
| POST | `/profile/change-password` | Change password |

## 🗄 Database Schema

### Tables

- **roles**: User roles (Admin, Manager, Staff)
- **users**: User accounts
- **categories**: Product categories
- **suppliers**: Supplier information
- **products**: Product details
- **inventory_transactions**: Stock movement records
- **activity_logs**: Audit trail

### Key Relationships

- Users → Roles (Many-to-One)
- Products → Categories (Many-to-One)
- Products → Suppliers (Many-to-One)
- Categories → Categories (Self-referencing for hierarchy)
- Inventory Transactions → Products (Many-to-One)
- Activity Logs → Users (Many-to-One)

## 🔒 Security

### Authentication
- BCrypt password hashing
- Session-based authentication
- 30-minute session timeout
- Remember me functionality

### Authorization
- Role-based access control
- Admin-only routes protection
- Authorization filter for sensitive operations

### Input Validation
- SQL injection prevention (prepared statements)
- XSS prevention (input sanitization)
- CSRF protection (session tokens)
- Input validation utilities

### Password Policy
- Minimum 8 characters
- Complexity requirements enforced

## 🔧 Troubleshooting

### Database Connection Issues

**Problem**: Cannot connect to database

**Solution**:
1. Verify MySQL is running
2. Check database credentials in `db.properties`
3. Ensure database exists and schema is loaded
4. Check firewall settings

### Build Failures

**Problem**: Maven build fails

**Solution**:
1. Ensure JDK 17 is installed and configured
2. Run `mvn clean install -U` to update dependencies
3. Check internet connection for dependency downloads

### Deployment Issues

**Problem**: Application won't start on Tomcat

**Solution**:
1. Check Tomcat logs for errors
2. Verify WAR file is correctly deployed
3. Ensure all dependencies are included in the WAR
4. Check database connection

### Docker Issues

**Problem**: Docker container fails to start

**Solution**:
1. Check Docker logs: `docker logs <container-id>`
2. Verify database container is healthy
3. Check environment variables
4. Ensure ports are not already in use

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Add Javadoc for public methods
- Write unit tests for new features
- Ensure code passes security scans
- Update documentation as needed

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- SmartInventory Development Team

## 🙏 Acknowledgments

- Apache Software Foundation
- Oracle Corporation (Java, MySQL)
- Bootstrap Team
- All open-source contributors

## 📞 Support

For support, email support@smartinventory.com or open an issue in the repository.

---

**Version**: 1.0.0  
**Last Updated**: July 2026
