# Media Ratings Platform (MRP)

A pure Java REST API for managing media ratings, users, and genres. Built without frameworks (Spring/Hibernate) using standard libraries and JDBC.

## Technician Stack
- **Language**: Java 21+
- **Database**: PostgreSQL (via Docker)
- **Server**: `com.sun.net.httpserver`
- **Architecture**: Layered (Handler -> Service -> Repository -> Database)

## Prerequisites
- Java 21+ SDK
- Docker & Docker Compose
- Maven

## Setup & Run

1.  **Start Database**
    ```bash
    docker-compose up -d
    ```

2.  **Build & Run Application**
    ```bash
    mvn clean package
    java -jar target/swen1-mrp-1.0-SNAPSHOT.jar
    ```
    Server starts on `http://localhost:8080`.

## API Usage Examples

**1. Register User**
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

**2. Login (Get Token)**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```
*Returns: `{"token": "mrp-token-..."}`*

**3. Create Media (Requires Auth)**
```bash
curl -X POST http://localhost:8080/api/media \
  -H "Authorization: Bearer mrp-token-..." \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Inception",
    "mediaType": "movie",
    "releaseYear": 2010,
    "ageRestriction": 13,
    "genres": ["Sci-Fi", "Action"]
  }'
```

**4. List Media**
```bash
curl -X GET "http://localhost:8080/api/media?title=Inception"
```
