package info.deckermail.demoemailserver.emails;

import org.springframework.stereotype.Component;

@Component
class EmailEntityToDtoMapper {

    EmailResponse map(EmailEntity emailEntity) {
        return new EmailResponse(
                emailEntity.getId(),
                emailEntity.getSubject(),
                emailEntity.getBody(),
                emailEntity.getState(),
                emailEntity.getFrom(),
                emailEntity.getTo()
        );
    }
}
