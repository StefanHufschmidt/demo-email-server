package info.deckermail.demoemailserver.emails;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class EmailService {

    private final EmailRepository emailRepository;

    Page<EmailDto> findAll(Pageable pagable) {
        return emailRepository.findAll(pagable)
                .map((emailEntity) ->
                        new EmailDto(
                                emailEntity.getId(),
                                emailEntity.getSubject(),
                                emailEntity.getBody(),
                                emailEntity.getState(),
                                emailEntity.getFrom(),
                                emailEntity.getTo()
                        )
                );
    }
}
