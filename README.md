# JWT Authentication System – Spring Boot

A Spring Boot and Spring Security application template providing secure, stateless JWT authentication and role-based authorization (`ROLE_USER` and `ROLE_ADMIN`).

## Tech Stack
Java, Spring Boot, Spring Security, JWT, JPA/Hibernate, Maven, and H2 database for testing.

## Authentication & Token Model
Upon login, the server issues a short-lived Access Token for API authorization and a long-lived Refresh Token stored in the database. The refresh token is used to generate new access tokens. On logout, the refresh token is deleted from the database to prevent further token renewals.

## Security & Authorization
Passwords are encrypted using BCrypt. Security is enforced through a JWT validation filter, Spring Security authorities, and `@PreAuthorize` annotations.

## How to Run
1. Configure database in `application.properties`.
2. Run the Spring Boot application.
3. Test APIs using Postman.

