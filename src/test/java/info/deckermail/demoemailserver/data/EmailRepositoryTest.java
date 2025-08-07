package info.deckermail.demoemailserver.data;

import info.deckermail.demoemailserver.EmailState;
import info.deckermail.demoemailserver.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@DirtiesContext
@AutoConfigureTestDatabase
@Sql(scripts = {
        "classpath:insert_emails.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class EmailRepositoryTest {

    @Autowired
    EmailRepository emailRepository;

    @Test
    void testFindAllEmails_returnsInsertedEmails() {
        final Page<EmailEntity> result = emailRepository.findAll(PageRequest.of(0, 4).withSort(Sort.by(Sort.Direction.ASC, "id")));

        assertEquals(4, result.getTotalElements());

        final List<EmailEntity> emailEntities = result.toList();

        final var mail1 = emailEntities.getFirst();
        assertEquals(1L, mail1.getId());
        assertEquals(EmailState.DRAFT, mail1.getState());
        assertEquals("Welcome!", mail1.getSubject());
        assertEquals("Hello and welcome to our service.", mail1.getBody());
        assertEquals("admin@example.com", mail1.getFrom().getAddress());
        assertEquals(Set.of("user1@example.com", "user2@example.com", "billing@example.com"), mail1.getTo().stream().map(ParticipantEntity::getAddress).collect(Collectors.toSet()));

        final var mail2 = emailEntities.get(1);
        assertEquals(2L, mail2.getId());
        assertEquals(EmailState.SENT, mail2.getState());
        assertEquals("Your Invoice", mail2.getSubject());
        assertEquals("Please find attached your invoice.", mail2.getBody());
        assertEquals("billing@example.com", mail2.getFrom().getAddress());
        assertEquals(Set.of("user2@example.com"), mail2.getTo().stream().map(ParticipantEntity::getAddress).collect(Collectors.toSet()));

        final var mail3 = emailEntities.get(2);
        assertEquals(3L, mail3.getId());
        assertEquals(EmailState.DELETED, mail3.getState());
        assertEquals("Delivery Issue", mail3.getSubject());
        assertEquals("We could not deliver your email.", mail3.getBody());
        assertEquals("support@example.com", mail3.getFrom().getAddress());
        assertEquals(Set.of("user3@example.com"), mail3.getTo().stream().map(ParticipantEntity::getAddress).collect(Collectors.toSet()));

        final var mail4 = emailEntities.get(3);
        assertEquals(4L, mail4.getId());
        assertEquals(EmailState.SPAM, mail4.getState());
        assertEquals("Password Reset", mail4.getSubject());
        assertEquals("Click here to reset your password.", mail4.getBody());
        assertEquals("security@example.com", mail4.getFrom().getAddress());
        assertEquals(Set.of("user4@example.com", "user2@example.com"), mail4.getTo().stream().map(ParticipantEntity::getAddress).collect(Collectors.toSet()));
    }

}
