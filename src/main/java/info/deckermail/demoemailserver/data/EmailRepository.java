package info.deckermail.demoemailserver.data;

import info.deckermail.demoemailserver.EmailState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends PagingAndSortingRepository<EmailEntity, Long>, CrudRepository<EmailEntity, Long> {

    @Query(value = "UPDATE emails SET state = 'SPAM' WHERE \"from\" = (SELECT p.id FROM participants p WHERE p.address  ILIKE :spamEmail LIMIT 1)", nativeQuery = true)
    @Modifying
    int markEmailsAsSpamByFromEmail(@Param("spamEmail") String spamEmail);

    @Query(value = "UPDATE emails SET state = 'SPAM' WHERE id IN (SELECT r.email_id FROM receivers r INNER JOIN participants p ON p.id = r.participant_id WHERE p.address ILIKE :spamEmail)", nativeQuery = true)
    @Modifying
    int markEmailsAsSpamByToEmail(@Param("spamEmail") String spamEmail);

    int countEmailEntityByState(EmailState state);

}
