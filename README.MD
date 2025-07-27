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

# Shortcuts
Due to external time constraints, the project contains several shortcuts which should be avoided in production. To show 
that I'm aware of them and that I'm able to handle them differently, I list them here:
- **Entity design:** In a more complex scenario, it would make sense to lift the email recipient and sender into a separate entity. This
  would allow reusing the same email address for multiple emails easily and make the storage of emails more efficient.
  This would also include some more mapping logic for saving emails and querying them. 
- **Input validation:** The input validation is very rudimentary. Usually, you'd try to validate emails which are 
  inserted. Since there are so many different email formats, this is not a trivial task. From my point of view, only
  email addresses which already received an email, and you know that are valid email addresses. See 
  https://stackoverflow.com/a/201378 for more information.
- **Tests/TDD:** The project contains a number of tests which should confidentially cover the most important 
  functionality. However, they are not complete and are not written in a TDD manner. I know how to build a project in 
  TDD style, but it takes me more time than adding some functional tests for the core logic.
