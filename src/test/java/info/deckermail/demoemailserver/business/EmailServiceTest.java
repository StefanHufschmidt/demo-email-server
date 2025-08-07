package info.deckermail.demoemailserver.business;

import info.deckermail.demoemailserver.EmailState;
import info.deckermail.demoemailserver.business.participants.ParticipantService;
import info.deckermail.demoemailserver.data.EmailEntity;
import info.deckermail.demoemailserver.data.EmailRepository;
import info.deckermail.demoemailserver.data.ParticipantEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * AI generated but manually reviewed unit tests.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailRepository emailRepository;
    @Mock
    private EmailEntityMapper emailEntityMapper;
    @Mock
    private ParticipantService participantService;
    @InjectMocks
    private EmailService emailService;

    @Test
    void findAll_shouldReturnMappedPage() {
        Pageable pageable = mock(Pageable.class);
        EmailEntity entity = mock(EmailEntity.class);
        EmailResponse response = mock(EmailResponse.class);
        Page<EmailEntity> entityPage = new PageImpl<>(Collections.singletonList(entity));
        when(emailRepository.findAll(pageable)).thenReturn(entityPage);
        when(emailEntityMapper.mapToResponse(entity)).thenReturn(response);

        Page<EmailResponse> result = emailService.findAll(pageable);

        assertThat(result.getContent()).containsExactly(response);
        verify(emailRepository).findAll(pageable);
        verify(emailEntityMapper).mapToResponse(entity);
    }

    @Test
    void findById_shouldReturnMappedEmail_whenEmailExists() throws NoSuchEmailException {
        long id = 1L;
        EmailEntity entity = mock(EmailEntity.class);
        EmailResponse response = mock(EmailResponse.class);
        when(emailRepository.findById(id)).thenReturn(Optional.of(entity));
        when(emailEntityMapper.mapToResponse(entity)).thenReturn(response);

        EmailResponse result = emailService.findById(id);

        assertThat(result).isEqualTo(response);
        verify(emailRepository).findById(id);
        verify(emailEntityMapper).mapToResponse(entity);
    }

    @Test
    void findById_shouldThrowException_whenEmailNotFound() {
        long id = 2L;
        when(emailRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchEmailException.class, () -> emailService.findById(id));
    }

    @Test
    void create_shouldSaveAndMapEmail() {
        EmailCreationRequest request = mock(EmailCreationRequest.class);
        ParticipantEntity fromParticipant = mock(ParticipantEntity.class);
        Set<ParticipantEntity> toParticipants = Set.of(mock(ParticipantEntity.class));
        EmailEntity mappedEntity = mock(EmailEntity.class);
        EmailEntity savedEntity = mock(EmailEntity.class);
        EmailResponse response = mock(EmailResponse.class);
        when(request.from()).thenReturn("foo@bar.com");
        when(request.to()).thenReturn(List.of("bar@foo.com"));
        when(participantService.getOrCreateParticipant("foo@bar.com")).thenReturn(fromParticipant);
        when(participantService.getOrCreateParticipants(List.of("bar@foo.com"))).thenReturn(toParticipants);
        when(emailEntityMapper.mapFromCreationRequest(request, fromParticipant, toParticipants)).thenReturn(mappedEntity);
        when(emailRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(emailEntityMapper.mapToResponse(savedEntity)).thenReturn(response);

        EmailResponse result = emailService.create(request);

        assertThat(result).isEqualTo(response);
        verify(participantService).getOrCreateParticipant("foo@bar.com");
        verify(participantService).getOrCreateParticipants(List.of("bar@foo.com"));
        verify(emailEntityMapper).mapFromCreationRequest(request, fromParticipant, toParticipants);
        verify(emailRepository).save(mappedEntity);
        verify(emailEntityMapper).mapToResponse(savedEntity);
    }

    @Test
    void createBulk_shouldSaveAllAndMapEmails() {
        EmailCreationRequest emailReq = mock(EmailCreationRequest.class);
        EmailBulkCreationRequest bulkRequest = mock(EmailBulkCreationRequest.class);
        when(bulkRequest.emails()).thenReturn(List.of(emailReq));
        EmailEntity entity = mock(EmailEntity.class);
        when(emailRepository.saveAll(anyCollection())).thenReturn(List.of(entity));
        EmailResponse response = mock(EmailResponse.class);
        when(emailEntityMapper.mapToResponse(entity)).thenReturn(response);

        var result = emailService.createBulk(bulkRequest);

        assertThat(result).containsExactly(response);
        verify(emailRepository).saveAll(anyCollection());
        verify(emailEntityMapper).mapToResponse(entity);
    }

    @Test
    void update_shouldUpdateAndMapEmail_whenDraft() throws Exception {
        long id = 1L;
        EmailUpdateRequest request = mock(EmailUpdateRequest.class);
        EmailEntity entity = mock(EmailEntity.class);
        when(emailRepository.findById(id)).thenReturn(Optional.of(entity));
        when(entity.getState()).thenReturn(EmailState.DRAFT);
        EmailEntity savedEntity = mock(EmailEntity.class);
        when(emailRepository.save(entity)).thenReturn(savedEntity);
        EmailResponse response = mock(EmailResponse.class);
        when(emailEntityMapper.mapToResponse(savedEntity)).thenReturn(response);

        EmailResponse result = emailService.update(id, request);

        assertThat(result).isEqualTo(response);
        verify(emailRepository).findById(id);
        verify(emailRepository).save(entity);
        verify(emailEntityMapper).mapToResponse(savedEntity);
    }

    @Test
    void update_shouldThrowException_whenNotDraft() {
        long id = 1L;
        EmailUpdateRequest request = mock(EmailUpdateRequest.class);
        EmailEntity entity = mock(EmailEntity.class);
        when(emailRepository.findById(id)).thenReturn(Optional.of(entity));
        when(entity.getState()).thenReturn(EmailState.SENT);
        assertThrows(EmailUpdateFailedException.class, () -> emailService.update(id, request));
    }

    @Test
    void update_shouldThrowNoSuchEmailException_whenNotFound() {
        long id = 1L;
        EmailUpdateRequest request = mock(EmailUpdateRequest.class);
        when(emailRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchEmailException.class, () -> emailService.update(id, request));
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        long id = 1L;
        emailService.delete(id);
        verify(emailRepository).deleteById(id);
    }

    @Test
    void deleteBulk_shouldCallRepositoryDeleteAllById_whenNotEmpty() {
        EmailBulkDeletionRequest request = mock(EmailBulkDeletionRequest.class);
        when(request.emailIds()).thenReturn(List.of(1L, 2L));
        emailService.delete(request);
        verify(emailRepository).deleteAllById(List.of(1L, 2L));
    }

    @Test
    void deleteBulk_shouldNotCallRepositoryDeleteAllById_whenEmpty() {
        EmailBulkDeletionRequest request = mock(EmailBulkDeletionRequest.class);
        when(request.emailIds()).thenReturn(List.of());
        emailService.delete(request);
        verify(emailRepository, never()).deleteAllById(anyCollection());
    }
}
