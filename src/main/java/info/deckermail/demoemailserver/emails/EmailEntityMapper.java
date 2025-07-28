package info.deckermail.demoemailserver.emails;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class EmailEntityMapper {

    EmailResponse mapToResponse(EmailEntity emailEntity) {
        return new EmailResponse(
                emailEntity.getId(),
                emailEntity.getSubject(),
                emailEntity.getBody(),
                emailEntity.getState(),
                emailEntity.getFrom(),
                emailEntity.getTo()
        );
    }

    EmailEntity mapFromCreationRequest(EmailCreationRequest request) {
        return new EmailEntity(
                null,
                request.state(),
                request.subject(),
                request.body(),
                request.from(),
                request.to(),
                Instant.now()
        );
    }
}
