# CipherTalk Kotlin Backend

This is the backend for the CipherTalk application, implemented in Kotlin using Spring Boot. It provides a REST API and WebSocket endpoint for real-time messaging.

## Technologies Used

- Kotlin
- Spring Boot
- Spring Web (REST API)
- Spring WebSocket
- Spring Security (JWT Authentication)
- Spring Data JPA
- PostgreSQL
- Redis

## Setup

1.  **Prerequisites:**
    - Java 17 or higher (Spring Boot 3.3.x requires Java 17+)
    - Maven
    - PostgreSQL
    - Redis
2.  **Configure `application.properties`:**
    Update `src/main/resources/application.properties` with your database and Redis connection details.

    ```properties
    # PostgreSQL
    spring.datasource.url=jdbc:postgresql://localhost:5432/ciphertalk_kotlin
    spring.datasource.username=your_db_user
    spring.datasource.password=your_db_password
    spring.jpa.hibernate.ddl-auto=update # Or 'create' for the first run

    # Redis
    spring.redis.host=localhost
    spring.redis.port=6379
    # spring.redis.password=your_redis_password (if applicable)

    # JWT
    jwt.secret=YourJWTSecretKeyWhichShouldBeLongAndSecureForKotlinBackend
    jwt.expiration.ms=86400000 # 24 hours
    ```

3.  **Build the project:**
    Navigate to the `backend` directory (if you aren't already there).
    ```bash
    ./mvnw clean install
    ```
4.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    Alternatively, you can run the packaged JAR file:
    ```bash
    java -jar target/ciphertalk-backend-kotlin-0.0.1-SNAPSHOT.jar
    ```

## API Endpoints

(To be documented as they are developed)

## WebSocket Channels

(To be documented as they are developed)
