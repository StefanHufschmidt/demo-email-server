package info.deckermail.demoemailserver.emails;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ParticipantService {

    private final ParticipantRepository participantRepository;

    ParticipantEntity getOrCreateParticipant(String address) {
        return participantRepository.findByAddress(address)
                .orElseGet(() -> participantRepository.save(new ParticipantEntity(address)));
    }

    public Set<ParticipantEntity> getOrCreateParticipants(Collection<String> addresses) {
        return addresses.stream()
                .map(this::getOrCreateParticipant)
                .collect(Collectors.toSet());
    }
}
