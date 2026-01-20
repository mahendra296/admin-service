# Admin Service

A Spring Boot web application that synchronizes product data from an external service and provides a web UI for product management.

## Overview

This service fetches product data from an external service (famme-service) at regular intervals using a scheduler, persists the data to a PostgreSQL database, and provides a web interface for viewing and managing products.

## Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.4.0 | Core framework |
| Kotlin | 1.9.25 | Programming language |
| Java | 21 | Runtime |
| MapStruct | 1.5.5.Final | DTO-Entity mapping |
| Spring Cloud OpenFeign | 4.2.0 | Service-to-service communication |
| Spring Data JDBC | - | Database access |
| PostgreSQL | - | Database |
| Flyway | - | Database migrations |
| Thymeleaf | - | Server-side templating |
| HTMX | 2.0.0 | Dynamic frontend interactions |

## Project Structure

```
src/main/kotlin/com/admin/
├── controller/          # REST and MVC controllers
├── service/             # Business logic
├── dto/                 # Data transfer objects
├── entity/              # Database entities
├── mapper/              # MapStruct mappers and JDBC row mappers
├── repository/          # Data access layer
├── scheduler/           # Scheduled tasks
├── clients/             # OpenFeign clients
├── exceptions/          # Custom exceptions and handlers
├── config/              # Application configuration
├── constants/           # Application constants
└── utils/               # Utility classes
```

## MapStruct Mappers

MapStruct is used for type-safe mapping between DTOs and entities. The library generates implementation code at compile time, providing better performance than reflection-based mapping.

### ProductMapper

**Location:** `com.admin.mapper.ProductMapper`

The main mapper interface that converts between external API DTOs and internal entities.

#### Mapping Methods

| Method | Source | Target | Description |
|--------|--------|--------|-------------|
| `toProductEntity()` | ProductDTO | Product | Maps product data with date conversions |
| `toProduct()` | ProductRequestDTO | Product | Maps internal request to entity |
| `toProductVariantEntity()` | VariantDTO | ProductVariant | Maps variant with JSON serialization |
| `toProductImageEntity()` | ImageDTO | ProductImage | Maps image data with date conversions |

#### Custom Conversion Methods

```kotlin
// Converts ISO date strings to ZonedDateTime
fun convertToZonedDate(date: String?): ZonedDateTime?

// Converts date strings using DateUtils
fun convertStringToDate(date: String?): ZonedDateTime?

// Serializes objects to JSON strings (for storing complex fields)
fun convertObjectTOString(data: Any?): String?
```

#### Configuration

- **Component Model:** Spring (injectable as a bean)
- **Unmapped Target Policy:** IGNORE (unmapped fields are ignored)

### ProductRowMapper

**Location:** `com.admin.mapper.ProductRowMapper`

A Spring JDBC RowMapper implementation for converting SQL ResultSet rows to Product entities.

```kotlin
@Component
class ProductRowMapper : RowMapper<Product> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Product
}
```

Used by `ProductRepository` for custom SQL query result mapping.

## Exception Handling

The application implements a dual exception handling strategy for REST APIs and MVC views.

### Custom Exceptions

**Location:** `com.admin.exceptions.ExceptionClass`

#### FeignClientException

Thrown when OpenFeign client calls to external services fail.

```kotlin
class FeignClientException(message: String) : RuntimeException(message)
```

**Usage:** Wrap external service communication errors.

#### RequestInvalidException

Thrown when incoming request data is invalid or malformed.

```kotlin
class RequestInvalidException(message: String) : RuntimeException(message)
```

**Usage:** Validate request parameters and body content.

### Exception Handlers

#### RestExceptionHandler

**Location:** `com.admin.exceptions.RestExceptionHandler`

Global exception handler for REST/API endpoints using `@ControllerAdvice`.

| Exception | HTTP Status | Response |
|-----------|-------------|----------|
| RequestInvalidException | 400 BAD_REQUEST | "Request Invalid" |
| FeignClientException | 500 INTERNAL_SERVER_ERROR | "Internal Server Error" |
| InternalServerError | 500 INTERNAL_SERVER_ERROR | "Internal Server Error" |
| Exception (general) | 500 INTERNAL_SERVER_ERROR | "General Error" |

#### MvcExceptionHandler

**Location:** `com.admin.exceptions.MvcExceptionHandler`

Global exception handler for MVC/Thymeleaf endpoints using `@ControllerAdvice`.

| Exception | Response Type | Description |
|-----------|--------------|-------------|
| RuntimeException | ModelAndView | Returns error view with message |
| MethodArgumentNotValidException | ModelAndView | Returns field validation errors |
| InternalServerError (Feign) | HtmxResponse | HTMX redirect with "Network Error" |

### ApiResponse

**Location:** `com.admin.exceptions.ApiResponse`

Standardized API error response structure.

```kotlin
data class ApiResponse(
    var code: HttpStatus,    // HTTP status code
    var key: String,         // Error category/type
    var description: String  // Detailed error message
)
```

## Key Features

### Scheduled Product Synchronization

- Runs every 12 minutes via `ProductScheduler`
- Fetches products from external famme-service using OpenFeign
- Persists products, variants, and images to PostgreSQL

### Product Management UI

- HTMX-powered dynamic updates
- Paginated product listing
- Thymeleaf templates with Shoelace UI components

### Data Persistence

- Spring Data JDBC with PostgreSQL
- Flyway database migrations
- Three main tables: products, product_variants, product_images

## Running the Application

### Prerequisites

- JDK 21
- PostgreSQL database
- Access to famme-service endpoint

### Configuration

Configure the following in `application.properties` or `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/admin_db
    username: your_username
    password: your_password

famme-service:
  url: https://your-famme-service-url
```

### Build and Run

```bash
# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

## License

This project is proprietary.
