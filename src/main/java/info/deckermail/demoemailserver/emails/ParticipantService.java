package info.deckermail.demoemailserver.emails;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ParticipantService {

    private final ParticipantRepository participantRepository;

    ParticipantEntity getOrCreateParticipant(String address) {
        return participantRepository.findByAddress(address)
                .orElseGet(() -> participantRepository.save(new ParticipantEntity(address)));
    }
}
