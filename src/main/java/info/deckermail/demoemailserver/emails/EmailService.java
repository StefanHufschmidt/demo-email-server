package info.deckermail.demoemailserver.emails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
class EmailService {

    private final EmailRepository emailRepository;
    private final EmailEntityToDtoMapper emailEntityToDtoMapper;

    Page<EmailDto> findAll(Pageable pagable) {
        return emailRepository.findAll(pagable)
                .map(emailEntityToDtoMapper::map);
    }

    EmailDto findById(Long id) throws NoSuchEmailException {
        return emailEntityToDtoMapper.map(
                emailRepository.findById(id)
                        .orElseThrow(() -> new NoSuchEmailException(id))
        );
    }

    EmailDto create(EmailCreationRequest request) {
        EmailEntity emailEntity = new EmailEntity(
                null,
                request.state(),
                request.subject(),
                request.body(),
                request.from(),
                request.to()
        );
        return emailEntityToDtoMapper.map(
                emailRepository.save(emailEntity)
        );
    }

    Collection<EmailDto> createBulk(EmailBulkCreationRequest request) {
        var mailsToBeCreated = request.emails().stream()
                .map(email -> new EmailEntity(
                        null,
                        email.state(),
                        email.subject(),
                        email.body(),
                        email.from(),
                        email.to()
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
}
