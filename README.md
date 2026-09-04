A Spring Boot and Spring Security application template providing secure JWT authentication and role-based authorization (`ROLE_USER` and `ROLE_ADMIN`).

## Stack
Java, Spring Boot, Spring Security, JWT, JPA/Hibernate, Maven, and PostgreSQL.

## Authentication & Token Model
Upon login, the server issues a short-lived Access Token (15 min) and a long-lived Refresh Token (7 days) as secure `HttpOnly` cookies. The refresh token is stored in PostgreSQL and used to generate new access tokens on expiry. On logout, the refresh token is deleted from the database and both cookies are cleared.

## Security & Authorization
Passwords are encrypted using BCrypt. Security is enforced through custom JWT validation, CORS configuration, `@PreAuthorize` annotations, and global exception handling.

## How to Run
1. Configure database credentials in `application.properties`.
2. Run the Spring Boot application.
3. Test APIs using Postman or your frontend client.

