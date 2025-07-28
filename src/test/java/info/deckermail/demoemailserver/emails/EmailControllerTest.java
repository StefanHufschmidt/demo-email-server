package info.deckermail.demoemailserver.emails;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private static EmailService emailService;

    private static MockMvc mockMvc;

    @BeforeAll
    static void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EmailController(emailService)).build();
    }

    @Test
    void testCreateEmail_inputValidation_state() throws Exception {
        // given: invalid input with null state
        @Language("JSON")
        var payload = """
        {
          "subject": "test subject",
          "body": "test body",
          "state": null,
          "from": "foo@bar.com",
          "to": [
            "zap@zarapp.com",
            "bar@foo.com"
          ]
        }
        """;

        // expect: validation error for null state
        mockMvc.perform(post("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmail_inputValidation_from() throws Exception {
        // given: invalid input with too long from field value
        @Language("JSON")
        final var payload = """
            {
              "subject": "test subject",
              "body": "test body",
              "state": "DRAFT",
              "from": "$$EMAIL$$",
              "to": [
                "zap@zarapp.com",
                "bar@foo.com"
              ]
            }
            """;

        // expect: validation error for too long from field
        mockMvc.perform(post("/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload.replace("$$EMAIL$$", "a".repeat(256) + "@example.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBulkEmail_inputValidation_emptyList() throws Exception {
        // given: invalid bulk email creation payload with null state
        @Language("JSON") final var payload = """
        {
          "emails": []
        }
        """;

        // expect: validation error for empty email list
        mockMvc.perform(post("/emails/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }
}
