# Course-Pick Backend

Spring Boot-based course recommendation and management system backend.

## 🚀 Features

- **Course Management**: Create, read, update courses through GPX/KML file parsing
- **Geospatial Search**: Location-based course search using MongoDB geospatial indexes
- **User Authentication**: Social login via Kakao OAuth
- **Admin Features**: Course management and system operations
- **Coordinate Snapping**: Route optimization using OSRM API

## 🛠️ Tech Stack

- **Java 21** + **Spring Boot 3.5**
- **MongoDB**: Geospatial data storage and queries
- **Lombok**: Boilerplate code reduction
- **JJWT**: JWT token-based authentication
- **Spatial4J**: Geospatial calculations
- **SpringDoc**: OpenAPI 3.0 documentation

## 📁 Architecture

Domain-Driven Design with 4-layer architecture:

```
┌─ Presentation (Controllers, DTOs)
├─ Application (Services, Use Cases)
├─ Domain (Entities, Value Objects, Repositories)
└─ Infrastructure (MongoDB, External APIs)
```

### Core Domain Models

- **Course**: Course information (name, coordinates, distance)
- **Coordinate**: Latitude/longitude coordinates
- **User**: User information
- **Notice**: Announcements

## 🏃‍♂️ Build & Run

### Prerequisites
- Java 21+
- MongoDB
- Gradle 8+

### Build
```bash
# Full build
./gradlew build

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build
```

### Run
```bash
# Run application
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "CourseApplicationServiceTest"

# Run specific test method
./gradlew test --tests "CourseApplicationServiceTest.findNearbyCourses"

# Verbose test output
./gradlew test --info
```

## 📊 API Documentation

Auto-generated via SpringDoc OpenAPI:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Key API Endpoints

- `GET /api/v1/courses` - List courses
- `POST /api/v1/courses` - Create course
- `GET /api/v1/courses/{id}` - Get specific course
- `PUT /api/v1/courses/{id}` - Update course
- `POST /api/v1/users/sign` - User login
- `GET /api/v1/notices` - List notices

## 🔧 Configuration

### Profiles
- `local`: Local development (dummy implementations)
- `dev`: Development environment
- `prod`: Production environment

### Key Settings
```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/coursepick
```

## 🐳 Docker

```bash
# Build Docker image
docker build -t coursepick-backend .

# Run Docker container
docker run -p 8080:8080 coursepick-backend
```

## 📈 Monitoring

- **Actuator**: `/actuator` endpoints
- **Prometheus**: `/actuator/prometheus` metrics
- **CloudWatch**: JVM/Spring metrics forwarding

## 🤝 Contributing

1. Fork repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit and push (`git commit -m 'Add amazing feature'`)
4. Create Pull Request

## 📄 License

This project is for internal use only.