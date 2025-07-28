package info.deckermail.demoemailserver.emails;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface ParticipantRepository extends CrudRepository<ParticipantEntity, Long> {
    Optional<ParticipantEntity> findByAddress(String address);
}
