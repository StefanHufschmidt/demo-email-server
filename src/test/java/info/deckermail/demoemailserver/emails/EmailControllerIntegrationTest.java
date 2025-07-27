package info.deckermail.demoemailserver.emails;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.deckermail.demoemailserver.TestcontainersConfiguration;
import jakarta.transaction.Transactional;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
@Sql(scripts = {
        "classpath:insert_emails.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
class EmailControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testFindEmailEndpoint_returnsEmailById() throws Exception {
        //given: expected result
        final var expectedEmail = new EmailResponse(1L, "Welcome!", "Hello and welcome to our service.", EmailState.DRAFT, "admin@example.com", List.of("user1@example.com", "user2@example.com", "billing@example.com"));

        // when: call the endpoint
        final String contentAsString = mockMvc.perform(get("/emails/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final EmailResponse actualEmail = objectMapper.readValue(contentAsString, EmailResponse.class);

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
                new EmailResponse(1L, "Welcome!", "Hello and welcome to our service.", EmailState.DRAFT, "admin@example.com", List.of("user1@example.com", "user2@example.com", "billing@example.com")),
                new EmailResponse(2L, "Your Invoice", "Please find attached your invoice.", EmailState.SENT, "billing@example.com", List.of("user2@example.com")),
                new EmailResponse(3L, "Delivery Issue", "We could not deliver your email.", EmailState.DELETED, "support@example.com", List.of("user3@example.com")),
                new EmailResponse(4L, "Password Reset", "Click here to reset your password.", EmailState.SPAM, "security@example.com", List.of("user4@example.com", "user2@example.com"))
        );

        // when: call the endpoint
        final String contentAsString = mockMvc.perform(get("/emails").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final JsonNode node = objectMapper.readTree(contentAsString);
        final List<EmailResponse> returnedEmails = objectMapper.treeToValue(node.get("content"), new TypeReference<List<EmailResponse>>() {});

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
                .andReturn().getResponse().getContentAsString(), EmailResponse.class);

        // then: the created email should match the payload
        assertEquals("Test Email", createdObject.subject());
        assertEquals("This is a test email.", createdObject.body());
        assertEquals(EmailState.DRAFT, createdObject.state());
        assertEquals("foo@bar.com", createdObject.from());
        assertEquals(List.of("zap@zarapp.com", "bar@foo.com"), createdObject.to());
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
                .andReturn().getResponse().getContentAsString(), new TypeReference<List<EmailResponse>>() {});

        // then: the created emails should match the payload
        assertTrue(createdEmails.stream().anyMatch(it -> it.subject().equals("Test Bulk Email") && it.id() != null));
        assertTrue(createdEmails.stream().anyMatch(it -> it.subject().equals("Test Bulk Email2") && it.id() != null));
        assertTrue(createdEmails.stream().anyMatch(it -> it.subject().equals("Test Bulk Email3") && it.id() != null));
    }

    @Test
    void testUpdate_shouldUpdateDraftEmailAndReturn() throws Exception {
        // given: the email update payload
        @Language("JSON") final var payload = """
        {
          "subject": "Updated Email",
          "body": "This is an updated email.",
          "state": "DRAFT",
          "from": "",
          "to": []
        }
        """;

        // when: call the endpoint to update an existing email
        final var updatedEmail = objectMapper.readValue(mockMvc.perform(post("/emails/1").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), EmailResponse.class);

        // then: the updated email should match the payload
        final var expectedEmail = new EmailResponse(
                1L,
                "Updated Email",
                "This is an updated email.",
                EmailState.DRAFT,
                "",
                List.of()
        );
        assertEquals(expectedEmail, updatedEmail);
    }

    @Test
    void testUpdate_shouldUpdateDraftEmail() throws Exception {
        // given: the email update payload
        @Language("JSON") final var payload = """
        {
          "subject": "Updated Email",
          "body": "This is an updated email.",
          "state": "DRAFT",
          "from": "",
          "to": []
        }
        """;

        // when: call the endpoint to update an existing email
        mockMvc.perform(put("/emails/1").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());

        // then: the updated email should match the payload
        final var updatedEmail = objectMapper.readValue(mockMvc.perform(get("/emails/1").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), EmailResponse.class);
        final var expectedEmail = new EmailResponse(
                1L,
                "Updated Email",
                "This is an updated email.",
                EmailState.DRAFT,
                "",
                List.of()
        );
        assertEquals(expectedEmail, updatedEmail);
    }

    @ParameterizedTest
    @ValueSource(longs = {
            2L, // SENT
            3L, // DELETED
            4L  // SPAM
    })
    void testUpdate_shouldNotUpdateEmailsWithinOtherStateThanDraft(long emailId) throws Exception {
        // given: the email update payload
        @Language("JSON") final var payload = """
        {
          "subject": "Updated Email",
          "body": "This is an updated email.",
          "state": "DRAFT",
          "from": "",
          "to": []
        }
        """;

        // expected: failure when calling the endpoint to update an existing email
        mockMvc.perform(put("/emails/" + emailId).contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void testDelete_shouldDeleteEmail() throws Exception {
        // when: call the endpoint to delete an existing email
        mockMvc.perform(delete("/emails/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // then: the email should no longer be found
        mockMvc.perform(get("/emails/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete_shouldDeleteNonExistingEmail() throws Exception {
        // expect: call the endpoint to delete an existing email will not fail on already deleted email
        mockMvc.perform(delete("/emails/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDelete_shouldBulkDeleteEmails() throws Exception {
        // given: the bulk email deletion payload
        @Language("JSON") final var payload = """
        {
          "emailIds": [
            2,
            3,
            4,
            5
          ]
        }
        """;

        // when: call the endpoint to delete an existing email
        mockMvc.perform(delete("/emails").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isNoContent());

        // then: the emails should no longer be found
        mockMvc.perform(get("/emails/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/emails/3").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/emails/4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

