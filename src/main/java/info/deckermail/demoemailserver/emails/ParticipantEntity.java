package info.deckermail.demoemailserver.emails;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
@Data
@Entity
@Table(name = "participants")
class ParticipantEntity {

    ParticipantEntity(String address) {
        this.address = address;
    }

    ParticipantEntity(Long id, String address) {
        this.id = id;
        this.address = address;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String address;

    @OneToMany(
            mappedBy = "from",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            targetEntity = EmailEntity.class
    )
    private Collection<EmailEntity> emails;

}
