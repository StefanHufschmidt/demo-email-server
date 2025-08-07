package info.deckermail.demoemailserver.business;

import info.deckermail.demoemailserver.data.EmailRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
class SpamEmailScheduler {

    private final EmailRepository emailRepository;
    private final String spamEmailAddress;

    SpamEmailScheduler(
            EmailRepository emailRepository,
            @Value("${spam-email.address:foo@bar.com}")
            String spamEmailAddress) {
        this.emailRepository = emailRepository;
        this.spamEmailAddress = spamEmailAddress;
    }

    @Scheduled(cron = "${spam-email.cron:0 0 10 * * *}")
    @Transactional
    void markEmailsAsSpam() {
        log.info("Marking emails as spam for address: {}", spamEmailAddress);

        var updatedEmailsWithSpamAsFrom = emailRepository.markEmailsAsSpamByFromEmail(spamEmailAddress);
        var updatedEmailsWithSpamInTo = emailRepository.markEmailsAsSpamByToEmail(spamEmailAddress);

        log.info("Finished marking emails as spam. Updated {} emails with spam as 'from' and {} emails with spam in 'to'.",
                updatedEmailsWithSpamAsFrom, updatedEmailsWithSpamInTo);
    }
}
