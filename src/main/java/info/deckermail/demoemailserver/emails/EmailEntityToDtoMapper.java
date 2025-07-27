package info.deckermail.demoemailserver.emails;

import org.springframework.stereotype.Component;

@Component
class EmailEntityToDtoMapper {

    EmailDto map(EmailEntity emailEntity) {
        return new EmailDto(
                emailEntity.getId(),
                emailEntity.getSubject(),
                emailEntity.getBody(),
                emailEntity.getState(),
                emailEntity.getFrom(),
                emailEntity.getTo()
        );
    }
}
