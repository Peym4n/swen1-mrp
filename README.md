# Media Ratings Platform (MRP)

A pure Java REST API for managing media ratings, users, and genres. Built without frameworks (Spring/Hibernate) to demonstrate deep understanding of core Java libraries, JDBC, and architecture patterns.

## 🚀 Features
- **User Management**: Registration, Login (Token-based Auth), Profile Management.
- **Media Management**: Create, Edit, Delete (Owner restricted), Filter, and Search.
- **Ratings & Reviews**: 
  - 1-5 Star Ratings with Comments.
  - **Moderation**: Comments require confirmation.
  - **One Rating Rule**: 1 rating per user per media.
  - **Likes**: Users can like helpful ratings.
- **Engagement**:
  - **Favorites**: Users can mark media as favorites.
  - **Leaderboard**: Track most active users.
  - **Recommendations**: Personalized suggestions based on rating history and genre similarity.

## 🛠️ Tech Stack
- **Language**: Java 21+
- **Database**: PostgreSQL (via Docker)
- **Server**: `com.sun.net.httpserver` (No Frameworks)
- **Persistence**: Pure JDBC with `PreparedStatement` & Singleton `DatabaseManager`.
- **Architecture**: 
  - Resource Router Pattern (`UserRouter`, `MediaRouter`...) -> Granular Handlers.
  - Layered: Handler -> Service -> Repository -> Database.

## 📚 Documentation
- [Project Protocol](protocol.md): Design decisions, lessons learned, time tracking.
- [Class Diagram](class_diagram.md): Visual representation of the architecture.

## ⚙️ Setup & Run

### 1. Start Database
```bash
docker-compose up -d
```
*Ensures PostgreSQL is running on port 5432.*

### 2. Build & Run Application
```bash
mvn clean package
java -jar target/swen1-mrp-1.0-SNAPSHOT.jar
```
Server starts on `http://localhost:8080`.

## 🧪 Testing

### Unit Tests
Run the JUnit 5 test suite (mocks repositories):
```bash
mvn test
```

### Integration Tests
Run the full stack integration tests (Rest-Assured):
```bash
mvn test -Dtest=MRPIntegrationTest
```
*Note: Integration tests automatically reset the test database.*
