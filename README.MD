# demo-email-server
This project is a demo showing how a very rudimentary web server could be implemented by using Java and Spring Boot. The
use case for this project should show a simple email server.

## Running the project
### Prerequisites
- Java 24
- Docker

### Run the project within test environment
Alternatively you can run the test application `info.deckermail.demoemailserver.TestDemoEmailServerApplication.main`
directly. It will spin up the application and the pre-configured Postgres DB in a test environment.

### Run the project with Docker Compose
1. Clone the repository
2. Build the applications Docker image using Maven:
   ```bash
   ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=deckermail/demo-email-server
   ```
3. Start the application together with the required Postgres DB using Docker Compose:
   ```bash
   docker compose up
   ```
4. The application should now be running at http://localhost:8080. You can check the health of the application by
   opening http://localhost:8080/actuator/health in your browser.

## API Documentation
The API documentation is available at http://localhost:8080/swagger-ui/index.html.
