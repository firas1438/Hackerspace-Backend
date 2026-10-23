The backend service for Hackerspace, the production application for discovering and managing community events.

#### What It Provides

- User registration, login, logout, and token refresh
- Event browsing and management
- User profile management
- File uploads backed by S3-compatible object storage

#### Technology

Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA/Hibernate, PostgreSQL, Maven, and the AWS SDK for S3.

#### Architecture

![Hackerspace backend architecture](https://i.imgur.com/gY2PUr1.png)

#### Authentication

Users authenticate with email and password. Passwords are stored using BCrypt. After login, the backend issues a short-lived access token and a long-lived refresh token in `HttpOnly` cookies. Refresh tokens are stored in PostgreSQL and can be used to renew access; logging out revokes the refresh token and clears the cookies.

#### Configuration

Configure the database connection with `DATABASE_URL`. Set the S3-compatible storage credentials and endpoint as needed using `AWS_ENDPOINT_URL_S3`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, and `S3_BUCKET`. The backend listens on port `8080` by default; override it with `PORT`.

#### Running Locally

From this directory, run:

```powershell
.\mvnw.cmd spring-boot:run
```

Ensure the required environment variables are configured before starting the application.

#### Tests

Run the backend test suite with:

```powershell
.\mvnw.cmd test
```
