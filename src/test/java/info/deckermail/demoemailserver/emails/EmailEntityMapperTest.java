package info.deckermail.demoemailserver.emails;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

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
                "from@example.com",
                List.of("to@example.com"),
                Instant.now()
        );
        EmailResponse response = mapper.mapToResponse(entity);
        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.subject()).isEqualTo(entity.getSubject());
        assertThat(response.body()).isEqualTo(entity.getBody());
        assertThat(response.state()).isEqualTo(entity.getState());
        assertThat(response.from()).isEqualTo(entity.getFrom());
        assertThat(response.to()).isEqualTo(entity.getTo());
    }

    @Test
    void mapFromCreationRequest_shouldMapAllFields() {
        EmailCreationRequest request = new EmailCreationRequest(
                "subject2",
                "body2",
                EmailState.SENT,
                "from2@example.com",
                List.of("to2@example.com")
        );
        EmailEntity entity = mapper.mapFromCreationRequest(request);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getState()).isEqualTo(request.state());
        assertThat(entity.getSubject()).isEqualTo(request.subject());
        assertThat(entity.getBody()).isEqualTo(request.body());
        assertThat(entity.getFrom()).isEqualTo(request.from());
        assertThat(entity.getTo()).isEqualTo(request.to());
        assertThat(entity.getLastModified()).isNotNull();
    }
}

