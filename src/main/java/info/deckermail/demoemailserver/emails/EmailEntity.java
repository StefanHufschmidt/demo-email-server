package info.deckermail.demoemailserver.emails;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "emails")
class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private EmailState state;
    private String subject;
    private String body;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "\"from\"", referencedColumnName = "id")
    private ParticipantEntity from;
    @ManyToMany
    @JoinTable(
            name = "receivers",
            joinColumns = @JoinColumn(name = "email_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private Set<ParticipantEntity> to = new HashSet<>();
    @UpdateTimestamp
    private Instant lastModified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailEntity that = (EmailEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
