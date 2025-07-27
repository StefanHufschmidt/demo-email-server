package info.deckermail.demoemailserver.emails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
class EmailService {

    private final EmailRepository emailRepository;
    private final EmailEntityToDtoMapper emailEntityToDtoMapper;

    Page<EmailResponse> findAll(Pageable pagable) {
        return emailRepository.findAll(pagable)
                .map(emailEntityToDtoMapper::map);
    }

    EmailResponse findById(long id) throws NoSuchEmailException {
        return emailEntityToDtoMapper.map(
                emailRepository.findById(id)
                        .orElseThrow(() -> new NoSuchEmailException(id))
        );
    }

    EmailResponse create(EmailCreationRequest request) {
        EmailEntity emailEntity = new EmailEntity(
                null,
                request.state(),
                request.subject(),
                request.body(),
                request.from(),
                request.to(),
                Instant.now()
        );
        return emailEntityToDtoMapper.map(
                emailRepository.save(emailEntity)
        );
    }

    @Transactional
    Collection<EmailResponse> createBulk(EmailBulkCreationRequest request) {
        var mailsToBeCreated = request.emails().stream()
                .map(email -> new EmailEntity(
                        null,
                        email.state(),
                        email.subject(),
                        email.body(),
                        email.from(),
                        email.to(),
                        Instant.now()
                ))
                .toList();
        var savedMails = emailRepository.saveAll(mailsToBeCreated);
        var iterator = savedMails.iterator();
        return Stream.generate(() -> null)
                .takeWhile(x -> iterator.hasNext())
                .map(n -> iterator.next())
                .map(emailEntityToDtoMapper::map)
                .toList();
    }

    EmailResponse update(long existingEmailId, EmailUpdateRequest request) throws NoSuchEmailException, EmailUpdateFailedException {
        var emailEntity = emailRepository.findById(existingEmailId).orElseThrow(() -> new NoSuchEmailException(existingEmailId));
        if (emailEntity.getState() != EmailState.DRAFT) {
            log.warn("Attempt to update email with ID {} that is not in DRAFT state.", existingEmailId);
            throw new EmailUpdateFailedException(existingEmailId);
        }

        emailEntity.setBody(request.body());
        emailEntity.setSubject(request.subject());
        emailEntity.setTo(request.to());
        emailEntity.setFrom(request.from());
        emailEntity.setState(request.state());
        return emailEntityToDtoMapper.map(
                emailRepository.save(emailEntity)
        );
    }

    void delete(long id) {
        emailRepository.deleteById(id);
        log.info("Deleted email with ID {}", id);
    }

    @Transactional
    void delete(EmailBulkDeletionRequest request) {
        // Thanks for the reminder that Kotlin makes the world a better place.
        final var emailsToDelete = request.emailIds().stream().filter(Objects::nonNull).toList();
        if (!emailsToDelete.isEmpty()) {
            emailRepository.deleteAllById(emailsToDelete);
            log.info("Deleted {} emails.", emailsToDelete.size());
        }
    }
}
