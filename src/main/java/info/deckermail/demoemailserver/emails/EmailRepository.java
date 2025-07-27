package info.deckermail.demoemailserver.emails;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface EmailRepository extends PagingAndSortingRepository<EmailEntity, Long>, CrudRepository<EmailEntity, Long> {

    @Query(value = "UPDATE emails SET state = 'SPAM' WHERE \"from\" ILIKE :spamEmail", nativeQuery = true)
    @Modifying
    int markEmailsAsSpamByFromEmail(@Param("spamEmail") String spamEmail);

    @Query(value = "UPDATE emails SET state = 'SPAM' WHERE :spamEmail ILIKE any(\"to\")", nativeQuery = true)
    @Modifying
    int markEmailsAsSpamByToEmail(@Param("spamEmail") String spamEmail);

    int countEmailEntityByState(EmailState state);

}
