package info.deckermail.demoemailserver.emails;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.deckermail.demoemailserver.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Sql(scripts = {
        "classpath:insert_emails.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@AutoConfigureMockMvc
class EmailControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
}
