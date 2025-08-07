package info.deckermail.demoemailserver.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "participants")
public class ParticipantEntity {

    public ParticipantEntity(String address) {
        this.address = address;
    }

    public ParticipantEntity(Long id, String address) {
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
    private Collection<EmailEntity> writtenEmails;

    @ManyToMany(mappedBy = "to")
    private Set<EmailEntity> receivedEmails = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantEntity that = (ParticipantEntity) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
