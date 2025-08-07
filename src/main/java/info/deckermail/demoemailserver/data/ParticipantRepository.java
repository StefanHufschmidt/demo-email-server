package info.deckermail.demoemailserver.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends CrudRepository<ParticipantEntity, Long> {
    Optional<ParticipantEntity> findByAddress(String address);
}
