package telegrambot.model.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "command_state_dependency")
public class CommandStateDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commandid", referencedColumnName = "id")
    private Command commandId;

    @ManyToOne
    @JoinColumn(name = "baseid", referencedColumnName = "id")
    private Command baseCommandId;

    @ManyToOne
    @JoinColumn(name = "currentstateid", referencedColumnName = "id")
    private State currentState;

    @ManyToOne
    @JoinColumn(name = "nextstateid", referencedColumnName = "id")
    private State nextState;

    @ManyToOne
    @JoinColumn(name = "previousstateid", referencedColumnName = "id")
    private State previousState;

}
