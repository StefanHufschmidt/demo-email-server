package info.deckermail.demoemailserver.emails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
class EmailService {

    private final EmailRepository emailRepository;
    private final EmailEntityMapper emailEntityMapper;
    private final ParticipantService participantService;

    Page<EmailResponse> findAll(Pageable pagable) {
        return emailRepository.findAll(pagable)
                .map(emailEntityMapper::mapToResponse);
    }

    EmailResponse findById(long id) throws NoSuchEmailException {
        return emailEntityMapper.mapToResponse(
                emailRepository.findById(id)
                        .orElseThrow(() -> new NoSuchEmailException(id))
        );
    }

    EmailResponse create(EmailCreationRequest request) {
        return emailEntityMapper.mapToResponse(
                emailRepository.save(
                        emailEntityMapper.mapFromCreationRequest(
                                request,
                                participantService.getOrCreateParticipant(request.from()),
                                participantService.getOrCreateParticipants(request.to())
                        )
                )
        );
    }

    @Transactional
    Collection<EmailResponse> createBulk(EmailBulkCreationRequest request) {
        var mailsToBeCreated = request.emails().stream()
                .map(it ->
                        emailEntityMapper.mapFromCreationRequest(
                                it,
                                participantService.getOrCreateParticipant(it.from()),
                                participantService.getOrCreateParticipants(it.to())
                        )
                )
                .toList();
        var savedMails = emailRepository.saveAll(mailsToBeCreated);
        var iterator = savedMails.iterator();
        return Stream.generate(() -> null)
                .takeWhile(_ -> iterator.hasNext())
                .map(_ -> iterator.next())
                .map(emailEntityMapper::mapToResponse)
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
        emailEntity.setTo(participantService.getOrCreateParticipants(request.to()));
        emailEntity.setFrom(new ParticipantEntity(request.from()));
        emailEntity.setState(request.state());
        return emailEntityMapper.mapToResponse(
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
