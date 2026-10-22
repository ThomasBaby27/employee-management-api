# Employee Management REST API
A production-grade Employee Management backend service built using Java 17, Spring Boot 3.3.4, and PostgreSQL. Designed following clean layered architecture with domain validation, centralized error handling, pagination, filtering, OpenAPI documentation, and automated tests.

## Architecture Overview
The application adheres to clean separation of concerns:
- controller: Exposes RESTful HTTP endpoints, enforces validation using @Valid, and delegates business tasks.
- service: Implements domain logic, manages declarative transactions (@Transactional), and converts between Entities and DTOs.
- repository: Handles database interactions via Spring Data JPA with derived queries.
- entity: JPA persistence mapping with Hibernate, tracking automatic timestamps.
- dto: Request and response representations preventing data leakage and decoupling internal models from external APIs.
- exception: Centralized @RestControllerAdvice mapping domain exceptions to RFC 7807-compliant payloads.

## Tech Stack
- Language: Java 17 (Eclipse Temurin)
- Framework: Spring Boot 3.3.4
- ORM & Data: Spring Data JPA, Hibernate 6
- Database: PostgreSQL 17
- Validation: Jakarta Bean Validation (spring-boot-starter-validation)
- Documentation: SpringDoc OpenAPI 2.6.0 (Swagger UI)
- Testing: JUnit 5, Mockito, MockMvc

## Prerequisites
- JDK 17 installed and configured in PATH
- PostgreSQL 17 running on localhost:5432
- Maven Wrapper (bundled in repository)

## Database Configuration
1. Create the database in PostgreSQL via pgAdmin:
sql
CREATE DATABASE employeedb;

2. Check src/main/resources/application.properties and verify your credentials:
spring.datasource.url=jdbc:postgresql://localhost:5432/employeedb
spring.datasource.username=postgres
spring.datasource.password= Enter your database password
spring.datasource.driver-class-name=org.postgresql.Driver

## Setup & Execution
1. Clone the repository:
git clone https://github.com/ThomasBaby27/employee-management-api
cd employee-management-api

2. Run the automated test suite:
./mvnw clean test  (On Mac)
.\mvnw.cmd clean test   (On Windows)

3. Start the application:
./mvnw spring-boot:run  (On Mac)
.\mvnw.cmd spring-boot:run  (On Windows)

4. Access Interactive Swagger Documentation:
Open: http://localhost:8080/swagger-ui.html
API Endpoints
HTTP Method     Endpoint               Description           Status Code
POST            /api/v1/employees      Create a new employee    201 Created
GET             /api/v1/employees      Get all employees (Paged & Filterable)      200 OK
GET             /api/v1/employees/{id}  Get single employee details      200 OK
PUT             /api/v1/employees/{id}   Update employee record     200 OK
DELETE          /api/v1/employees/{id}   Delete employee record
   204 No Content

Query Parameters for GET /api/v1/employees
page: Page index (default: 0)
size: Page record count (default: 10)
sortBy: Field to sort by (default: id)
direction: Sort order (asc or desc, default: desc)
department: Filter by department (case-insensitive contains)
status: Filter by status (ACTIVE, INACTIVE)

## Sample Requests & Responses
1. Create Employee
POST /api/v1/employees
Request Body:
{
  "firstName": "Thomas",
  "lastName": "Baby",
  "email": "thomas@gmail.com",
  "department": "Engineering",
  "designation": "Java Developer",
  "salary": 35000.00,
  "status": "ACTIVE"
}

Response (201 Created):
{
  "id": 1,
  "firstName": "Thomas",
  "lastName": "Baby",
  "email": "thomas@gmail.com",
  "department": "Engineering",
  "designation": "Java Developer",
  "salary": 35000.00,
  "status": "ACTIVE",
  "createdAt": "2026-10-21T21:16:46.903",
  "updatedAt": "2026-10-21T21:16:46.903"
}

2. Validation Error Handling
POST /api/v1/employees with invalid data:
{
  "firstName": "",
  "lastName": "",
  "email": "invalid-email",
  "department": "Engineering",
  "designation": "Junior Dev",
  "salary": -500,
  "status": ""
}

Response (400 Bad Request):
{
  "timestamp": "2026-10-21T21:20:00.000",
  "status": 400,
  "error": "Validation Error",
  "message": "Input validation failed for one or more fields",
  "path": "/api/v1/employees",
  "validationErrors": {
    "firstName": "First name is mandatory",
    "email": "Email must be a well-formed email address",
    "salary": "Salary must be greater than zero",
    "status": "Status is mandatory (e.g. ACTIVE, INACTIVE)"
  }
}

3. Duplicate Resource Handling
POST /api/v1/employees with an already registered email:
Response (409 Conflict):
{
  "timestamp": "2026-10-21T21:21:00.000",
  "status": 409,
  "error": "Conflict",
  "message": "Employee with email thomas@gmail.com already exists",
  "path": "/api/v1/employees"
}

Postman Collection
A pre-configured Postman collection is located at:
postman/employee_management_collection.json
