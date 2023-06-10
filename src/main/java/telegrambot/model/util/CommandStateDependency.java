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
    @JoinColumn(name = "command_id", referencedColumnName = "id")
    private Command command;

    @ManyToOne
    @JoinColumn(name = "base_id", referencedColumnName = "id")
    private Command base;

    @ManyToOne
    @JoinColumn(name = "current_state_id", referencedColumnName = "id")
    private State currentState;

    @ManyToOne
    @JoinColumn(name = "previous_state_id", referencedColumnName = "id")
    private State previousState;
}