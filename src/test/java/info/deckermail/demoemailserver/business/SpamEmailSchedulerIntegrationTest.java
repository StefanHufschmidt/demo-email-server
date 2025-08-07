package info.deckermail.demoemailserver.business;

import info.deckermail.demoemailserver.EmailState;
import info.deckermail.demoemailserver.TestcontainersConfiguration;
import info.deckermail.demoemailserver.data.EmailRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import({TestcontainersConfiguration.class, SpamEmailScheduler.class})
@DataJpaTest
@DirtiesContext
@AutoConfigureTestDatabase
@Sql(scripts = {
        "classpath:insert_emails.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class SpamEmailSchedulerIntegrationTest {

    @Autowired
    SpamEmailScheduler spamEmailScheduler;

    @Autowired
    EmailRepository emailRepository;

    @Test
    void testMarkEmailsAsSpam() {
        var spamEmailsBefore = emailRepository.countEmailEntityByState(EmailState.SPAM);
        assertEquals(1, spamEmailsBefore);

        spamEmailScheduler.markEmailsAsSpam();

        var spamEmailsAfter = emailRepository.countEmailEntityByState(EmailState.SPAM);
        assertTrue(spamEmailsBefore < spamEmailsAfter);
        assertEquals(3, spamEmailsAfter);
        assertEquals(1, emailRepository.countEmailEntityByState(EmailState.DELETED));
    }
}
