package info.deckermail.demoemailserver.business;

import info.deckermail.demoemailserver.EmailState;
import info.deckermail.demoemailserver.data.EmailEntity;
import info.deckermail.demoemailserver.data.ParticipantEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AI generated but manually reviewed unit tests.
 */
class EmailEntityMapperTest {

    private final EmailEntityMapper mapper = new EmailEntityMapper();

    @Test
    void mapToResponse_shouldMapAllFields() {
        EmailEntity entity = new EmailEntity(
                42L,
                EmailState.DRAFT,
                "subject",
                "body",
                new ParticipantEntity("from@example.com"),
                Set.of(new ParticipantEntity("to@example.com")),
                Instant.now()
        );
        EmailResponse response = mapper.mapToResponse(entity);
        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.subject()).isEqualTo(entity.getSubject());
        assertThat(response.body()).isEqualTo(entity.getBody());
        assertThat(response.state()).isEqualTo(entity.getState());
        assertThat(response.from()).isEqualTo(entity.getFrom().getAddress());
        assertThat(response.to()).isEqualTo(entity.getTo().stream().map(ParticipantEntity::getAddress).collect(Collectors.toSet()));
    }

    @Test
    void mapFromCreationRequest_shouldMapAllFields() {
        EmailCreationRequest request = new EmailCreationRequest(
                "subject2",
                "body2",
                EmailState.SENT,
                "from2@example.com",
                Set.of("to2@example.com")
        );
        EmailEntity entity = mapper.mapFromCreationRequest(request, new ParticipantEntity(request.from()),
                request.to().stream().map(ParticipantEntity::new).collect(Collectors.toSet()));
        assertThat(entity.getId()).isNull();
        assertThat(entity.getState()).isEqualTo(request.state());
        assertThat(entity.getSubject()).isEqualTo(request.subject());
        assertThat(entity.getBody()).isEqualTo(request.body());
        assertThat(entity.getFrom().getAddress()).isEqualTo(request.from());
        assertThat(entity.getTo().stream().map(ParticipantEntity::getAddress).collect(Collectors.toSet())).isEqualTo(request.to());
        assertThat(entity.getLastModified()).isNotNull();
    }
}

