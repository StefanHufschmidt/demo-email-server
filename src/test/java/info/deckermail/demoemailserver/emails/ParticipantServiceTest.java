package info.deckermail.demoemailserver.emails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    @Test
    void getOrCreateParticipant_shouldReturnExistingParticipant() {
        String address = "test@example.com";
        ParticipantEntity existing = new ParticipantEntity(1L, address);
        when(participantRepository.findByAddress(address)).thenReturn(Optional.of(existing));

        ParticipantEntity result = participantService.getOrCreateParticipant(address);

        assertThat(result).isEqualTo(existing);
        verify(participantRepository).findByAddress(address);
        verify(participantRepository, never()).save(any());
    }

    @Test
    void getOrCreateParticipant_shouldCreateAndReturnNewParticipant() {
        String address = "new@example.com";
        ParticipantEntity newParticipant = new ParticipantEntity(2L, address);
        when(participantRepository.findByAddress(address)).thenReturn(Optional.empty());
        when(participantRepository.save(any(ParticipantEntity.class))).thenReturn(newParticipant);

        ParticipantEntity result = participantService.getOrCreateParticipant(address);

        assertThat(result).isEqualTo(newParticipant);
        verify(participantRepository).findByAddress(address);
        verify(participantRepository).save(any(ParticipantEntity.class));
    }

    @Test
    void getOrCreateParticipants_shouldReturnExistingAndCreateNewParticipants() {
        String address1 = "existing@example.com";
        String address2 = "new@example.com";
        ParticipantEntity existing = new ParticipantEntity(1L, address1);
        ParticipantEntity newParticipant = new ParticipantEntity(2L, address2);
        when(participantRepository.findByAddress(address1)).thenReturn(Optional.of(existing));
        when(participantRepository.findByAddress(address2)).thenReturn(Optional.empty());
        when(participantRepository.save(any(ParticipantEntity.class))).thenReturn(newParticipant);

        Set<ParticipantEntity> result = participantService.getOrCreateParticipants(Set.of(address1, address2));

        assertThat(result).containsExactlyInAnyOrder(existing, newParticipant);
        verify(participantRepository).findByAddress(address1);
        verify(participantRepository).findByAddress(address2);
        verify(participantRepository).save(any(ParticipantEntity.class));
    }

    @Test
    void getOrCreateParticipants_shouldReturnAllExistingParticipants() {
        String address1 = "existing1@example.com";
        String address2 = "existing2@example.com";
        ParticipantEntity existing1 = new ParticipantEntity(1L, address1);
        ParticipantEntity existing2 = new ParticipantEntity(2L, address2);
        when(participantRepository.findByAddress(address1)).thenReturn(Optional.of(existing1));
        when(participantRepository.findByAddress(address2)).thenReturn(Optional.of(existing2));

        Set<ParticipantEntity> result = participantService.getOrCreateParticipants(Set.of(address1, address2));

        assertThat(result).containsExactlyInAnyOrder(existing1, existing2);
        verify(participantRepository).findByAddress(address1);
        verify(participantRepository).findByAddress(address2);
        verify(participantRepository, never()).save(any());
    }
}
