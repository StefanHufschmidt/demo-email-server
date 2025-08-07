package info.deckermail.demoemailserver.business.participants;

import info.deckermail.demoemailserver.data.ParticipantEntity;
import info.deckermail.demoemailserver.data.ParticipantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    @Transactional
    public ParticipantEntity getOrCreateParticipant(String address) {
        return participantRepository.findByAddress(address)
                .orElseGet(() -> participantRepository.save(new ParticipantEntity(address)));
    }

    @Transactional
    public Set<ParticipantEntity> getOrCreateParticipants(Collection<String> addresses) {
        return addresses.stream()
                .map(this::getOrCreateParticipant)
                .collect(Collectors.toSet());
    }
}
