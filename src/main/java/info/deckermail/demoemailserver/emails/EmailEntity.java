package info.deckermail.demoemailserver.emails;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

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
    @Column(name = "\"from\"")
    private String from;
    @Column(name = "\"to\"")
    private Collection<String> to;

}
