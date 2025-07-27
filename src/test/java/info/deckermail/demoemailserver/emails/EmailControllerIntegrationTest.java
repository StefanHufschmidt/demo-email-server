package info.deckermail.demoemailserver.emails;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.deckermail.demoemailserver.TestcontainersConfiguration;
import jakarta.transaction.Transactional;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Sql(scripts = {
        "classpath:insert_emails.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@AutoConfigureMockMvc
@Transactional
class EmailControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testFindEmailEndpoint_returnsEmailById() throws Exception {
        //given: expected result
        final var expectedEmail = new EmailDto(1L, "Welcome!", "Hello and welcome to our service.", EmailState.DRAFT, "admin@example.com", List.of("user1@example.com", "user2@example.com"));

        // when: call the endpoint
        final String contentAsString = mockMvc.perform(get("/emails/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final EmailDto actualEmail = objectMapper.readValue(contentAsString, EmailDto.class);

        // then: result should match expected email
        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void testFindEmailEndpoint_returnsNotFoundForNonExistentEmail() throws Exception {
        // when: call the endpoint with a non-existent email ID
        mockMvc.perform(get("/emails/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindAllEmailsEndpoint_returnsInsertedEmails() throws Exception {
        //given: expected result
        final var expectedEmails = List.of(
                new EmailDto(1L, "Welcome!", "Hello and welcome to our service.", EmailState.DRAFT, "admin@example.com", List.of("user1@example.com", "user2@example.com")),
                new EmailDto(2L, "Your Invoice", "Please find attached your invoice.", EmailState.SENT, "billing@example.com", List.of("user2@example.com")),
                new EmailDto(3L, "Delivery Issue", "We could not deliver your email.", EmailState.DELETED, "support@example.com", List.of("user3@example.com")),
                new EmailDto(4L, "Password Reset", "Click here to reset your password.", EmailState.SPAM, "security@example.com", List.of("user4@example.com", "user2@example.com"))
        );

        // when: call the endpoint
        final String contentAsString = mockMvc.perform(get("/emails").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final JsonNode node = objectMapper.readTree(contentAsString);
        final List<EmailDto> returnedEmails = objectMapper.treeToValue(node.get("content"), new TypeReference<List<EmailDto>>() {});

        // then: result should include correct emails
        assertEquals(expectedEmails, returnedEmails);
    }

    @Test
    void testCreateEmailEndpoint_shouldAddEmail() throws Exception {
        // given: the email creation payload
        @Language("JSON")
        final var payload = """
        {
          "subject": "Test Email",
          "body": "This is a test email.",
          "state": "DRAFT",
          "from": "foo@bar.com",
          "to": [
            "zap@zarapp.com",
            "bar@foo.com"
          ]
        }
        """;

        // when: call the endpoint to create a new email
        final var createdObject = objectMapper.readValue(mockMvc.perform(post("/emails").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/emails/5")))
                .andReturn().getResponse().getContentAsString(), EmailDto.class);

        // then: the created email should match the payload
        final var expectedEmail = new EmailDto(
                5L,
                "Test Email",
                "This is a test email.",
                EmailState.DRAFT,
                "foo@bar.com",
                List.of("zap@zarapp.com", "bar@foo.com")
        );
        assertEquals(expectedEmail, createdObject);
    }

    @Test
    void testCreateBulkEmailEndpoint_shouldAddBulkEmails() throws Exception {
        // given: the bulk email creation payload
        @Language("JSON") final var payload = """
        
                {
            "emails": [
              {
                  "subject": "Test Bulk Email",
                  "body": "This is a test email.",
                  "state": "DRAFT",
                  "from": "foo@bar.com",
                  "to": [
                    "zap@zarapp.com",
                    "bar@foo.com"
                  ]
              },
              {
                  "subject": "Test Bulk Email2",
                  "body": "This is a test email.2",
                  "state": "DRAFT",
                  "from": "foo@bar.com",
                  "to": [
                    "zap@zarapp.com",
                    "bar@foo.com"
                  ]
              },
              {
                  "subject": "Test Bulk Email3",
                  "body": "This is a test email.3",
                  "state": "DRAFT",
                  "from": "foo@bar.com",
                  "to": [
                    "zap@zarapp.com",
                    "bar@foo.com"
                  ]
              }
            ]
        }
        """;

        // when: call the endpoint to create bulk emails
        final var createdEmails = objectMapper.readValue(mockMvc.perform(post("/emails/bulk").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(), new TypeReference<List<EmailDto>>() {});

        // then: the created emails should match the payload
        assertTrue(createdEmails.stream().anyMatch(it -> it.subject().equals("Test Bulk Email") && it.id() != null));
        assertTrue(createdEmails.stream().anyMatch(it -> it.subject().equals("Test Bulk Email2") && it.id() != null));
        assertTrue(createdEmails.stream().anyMatch(it -> it.subject().equals("Test Bulk Email3") && it.id() != null));
    }
}
