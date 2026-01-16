# AGENTS.md

This file provides coding guidelines for agentic coding assistants working in the Course-Pick backend repository.

## Build, Test, and Lint Commands

### Build Commands
```bash
# Build the project
./gradlew build

# Clean build artifacts
./gradlew clean

# Build without tests
./gradlew build -x test

# Run the application
./gradlew bootRun
```

### Test Commands
```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "coursepick.coursepick.application.CourseApplicationServiceTest"

# Run a specific test method
./gradlew test --tests "coursepick.coursepick.application.CourseApplicationServiceTest.가까운_코스들을_조회한다"

# Run tests matching a pattern
./gradlew test --tests "*CourseTest"

# Run tests with verbose output
./gradlew test --info

# Run tests with debug output
./gradlew test --debug
```

### Other Commands
```bash
# Check dependencies
./gradlew dependencies

# Build Docker image
docker build -t coursepick-backend .
```

## Code Style Guidelines

### Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line length**: No hard limit (max_line_length = 999 for Java)
- **Charset**: UTF-8
- **Line endings**: LF (Unix-style)
- **Final newline**: Required at end of files
- **Trailing whitespace**: Remove all trailing whitespace

### Import Organization
- Use single-class imports (no wildcard imports except for allowed packages)
- Import order (per `.editorconfig`):
  1. All other imports (`@*`, `*`)
  2. Blank line separator (`|`)
  3. `javax.**`, `java.**` imports
  4. Blank line separator (`|`)
  5. Static imports (`$*`)
- Maximum 5 classes from same package before switching to wildcard
- Layout static imports separately
- No unused imports

### Naming Conventions
- **Classes**: PascalCase
- **Interfaces**: PascalCase (no `I` prefix)
- **Methods**: camelCase
- **Variables**: camelCase
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase
- **Test classes**: Suffix with `Test` (e.g., `CourseApplicationServiceTest`)
- **Test methods**: Use Korean descriptive names (e.g., `가까운_코스들을_조회한다`)

### Java-Specific Conventions
- **Lombok annotations**: Use `@RequiredArgsConstructor`, `@Getter`, `@Slf4j` where appropriate
- **Records**: Prefer records for immutable value objects (e.g., `Meter`, `CourseName`, `Coordinate`)
- **Fluent accessors**: Use `@Accessors(fluent = true)` for domain entities (e.g., `course.name()` instead of `course.getName()`)
- **Nullability**: Use `@Nullable` from `org.jspecify.annotations.Nullable` for nullable parameters/returns
- **Final variables**: Not required for local variables or parameters (per `.editorconfig`)

### Code Structure
- **Brace style**: End of line (K&R style)
- **Blank lines**: 
  - 1 blank line after imports
  - 1 blank line around methods
  - 1 blank line around classes
  - 0 blank lines around fields
- **Control statements**: No forced braces for single-line if/for/while (but allowed)
- **Method chaining**: No wrapping (wrap = off)

### Error Handling
- **Domain validation**: Throw exceptions immediately using `ErrorType` enum
- **Error messages**: Use `ErrorType.create()` with formatted messages
- **Exception types**:
  - `IllegalArgumentException`: For invalid input/validation errors
  - `NoSuchElementException`: For missing entities
  - `SecurityException`: For authentication/authorization errors
  - `UnauthorizedException`: For custom unauthorized access
- **Exception handling**: Use `@RestControllerAdvice` with `WebExceptionHandler` for consistent error responses
- **Logging**: Log exceptions at WARN level for expected errors, ERROR for unexpected

### Logging
- **Logger**: Use `@Slf4j` Lombok annotation
- **Log levels**:
  - `log.error()`: Unhandled exceptions, system failures
  - `log.warn()`: Expected exceptions, validation failures, non-critical issues
  - `log.info()`: Important business events (not used frequently in current code)
  - `log.debug()`: Detailed debugging information
- **Log messages**: Use Korean for business logic messages, English for technical messages
- **Structured logging**: Use `LogContent` utility for exception logging

### Testing
- **Test framework**: JUnit 5 (`@Test`, `@AfterEach`, etc.)
- **Assertions**: Use AssertJ (`assertThat()`, `isEqualTo()`, `hasSize()`, etc.)
- **Test structure**: Arrange-Act-Assert (AAA) pattern
- **Integration tests**: Extend `AbstractIntegrationTest` for DB-backed tests
- **Test data**: Use `DatabaseTestUtil` for saving test entities
- **Cleanup**: Always clean up test data in `@AfterEach` methods
- **Test names**: Use descriptive Korean names that explain the behavior being tested
- **Mock servers**: Use `MockWebServer` from OkHttp for external API testing

### Dependency Injection
- **Constructor injection**: Use `@RequiredArgsConstructor` with final fields
- **Service layer**: Use `@Service` annotation
- **Transactions**: Use `@Transactional(readOnly = true)` for read operations
- **Configuration**: Use `@Configuration` for Spring configuration classes

### API Design
- **Controllers**: Implement interface from `api/` package (e.g., `CourseWebApi`)
- **Documentation**: Use SpringDoc annotations (`@Operation`, `@ApiResponse`, `@Parameter`)
- **Request/Response DTOs**: Use `WebRequest`/`WebResponse` suffix
- **Validation**: Validate in domain layer, not controller layer
- **HTTP status codes**: 
  - 200: Success
  - 400: Bad request (validation errors)
  - 401: Unauthorized
  - 404: Resource not found
  - 500: Internal server error

### Domain-Driven Design Principles
- **Layering**: Domain → Application → Infrastructure → Presentation
- **Aggregates**: Use `Course` as aggregate root with value objects
- **Value objects**: Immutable records with validation (e.g., `CourseName`, `Meter`, `Coordinate`)
- **Repository interfaces**: Define in domain layer, implement in infrastructure
- **Domain services**: Keep business logic in domain entities when possible
- **Factory methods**: Use static factory methods (e.g., `Meter.zero()`, `ErrorType.create()`)

## Common Patterns

### Creating Domain Entities
```java
// Use constructor with validation
var course = new Course(null, "Course Name", coordinateList);

// MongoDB will assign ID on save
var savedCourse = courseRepository.save(course);
```

### Error Handling Pattern
```java
// Define in ErrorType enum
NOT_EXIST_COURSE("코스가 존재하지 않습니다. 코스id=%s", NoSuchElementException::new)

// Use in service layer
Course course = courseRepository.findById(id)
    .orElseThrow(() -> NOT_EXIST_COURSE.create(id));
```

### Value Object Pattern
```java
// Immutable record with validation
public record CourseName(String value) {
    public CourseName {
        value = compact(value);
        validateLength(value);
    }
}
```

## Notes for AI Agents

- **Test-first**: Always run existing tests after making changes
- **Korean comments**: Use Korean for business logic documentation, English for technical comments
- **Profile awareness**: Check active Spring profile (local/dev/prod) when debugging
- **Geospatial queries**: Remember MongoDB geospatial indexes are required for distance queries
- **Dummy implementations**: Use `Dummy*` classes for local development without external dependencies
- **Import management**: Keep imports organized per the specified order
- **Code consistency**: Match existing code style in the repository
- **Error messages**: Follow the `ErrorType` enum pattern for all domain validation errors
