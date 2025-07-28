package info.deckermail.demoemailserver.emails;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class EmailEntityMapper {

    EmailResponse mapToResponse(EmailEntity emailEntity) {
        return new EmailResponse(
                emailEntity.getId(),
                emailEntity.getSubject(),
                emailEntity.getBody(),
                emailEntity.getState(),
                emailEntity.getFrom().getAddress(),
                emailEntity.getTo().stream().map(ParticipantEntity::getAddress).collect(Collectors.toSet())
        );
    }

    EmailEntity mapFromCreationRequest(EmailCreationRequest request, ParticipantEntity from, Set<ParticipantEntity> receivers) {
        return new EmailEntity(
                null,
                request.state(),
                request.subject(),
                request.body(),
                from,
                receivers,
                Instant.now()
        );
    }
}
