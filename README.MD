# demo-email-server
This project is a demo showing how a very rudimentary web server could be implemented by using Java and Spring Boot. The
use case for this project should show a simple email server.

## Running the project
### Prerequisites
- Java 24
- Docker

### Run the project within test environment
1. Adjust the `src/test/resources/application.yml` file and set the wanted `spam-email.address` to the one you'd like 
   the email server to use for spam emails.
2. Run the test application `info.deckermail.demoemailserver.TestDemoEmailServerApplication.main`
directly. It will spin up the application and the pre-configured Postgres DB in a test environment.

### Run the project with Docker Compose
1. Clone the repository
2. Build the applications Docker image using Maven:
   ```bash
   ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=deckermail/demo-email-server
   ```
3. Adjust the `application-dc.yml` file and set the wanted `spam-email.address` to the one you'd like the email server
   to use for spam emails.
4. Start the application together with the required Postgres DB using Docker Compose:
   ```bash
   docker compose up
   ```
5. The application should now be running at http://localhost:8080. You can check the health of the application by
   opening http://localhost:8080/actuator/health in your browser.
6. After exploring the application, you can stop it by running:
   ```bash
   docker compose down -v
   ```

## API Documentation
The API documentation is available at http://localhost:8080/swagger-ui/index.html.

# Shortcuts
Due to external time constraints, the project contains several shortcuts which should be avoided in production. To show 
that I'm aware of them and that I'm able to handle them differently, I list them here:
- **Input validation:** The input validation is very rudimentary. Usually, you'd try to validate emails which are 
  inserted. Since there are so many different email formats, this is not a trivial task. From my point of view, only
  email addresses which already received an email, and you know that are valid email addresses. See 
  https://stackoverflow.com/a/201378 for more information.
- **Tests/TDD:** The project contains a number of tests which should confidentially cover the most important 
  functionality. However, they are not complete and are not written in a TDD manner. I know how to build a project in 
  TDD style, but it takes me more time than adding some functional tests for the core logic.
- **Huge data amounts:** The project is made by the principle of "keep it simple and stupid". This also means that not 
  every bit of code is optimized for large data amounts. E.g.: The email spam detection is done by querying over all 
  emails. This could block other database queries and could be done by chunking the deletions and processing them with 
  a small delay if necessary. However, this would also add complexity to the code and is not necessary for a demo 
  project.
