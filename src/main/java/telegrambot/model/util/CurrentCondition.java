package telegrambot.model.util;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "current_condition")
public class CurrentCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "commandid", referencedColumnName = "id")
    private Command command;

    @Setter
    @ManyToOne
    @JoinColumn(name = "stateid", referencedColumnName = "id")
    private State state;

}
