package info.deckermail.demoemailserver.emails;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
