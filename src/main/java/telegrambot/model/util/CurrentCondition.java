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
    @Column(name = "id", nullable = false)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "command_id", referencedColumnName = "id")
    private Command command;

    @Setter
    @ManyToOne
    @JoinColumn(name = "state_id", referencedColumnName = "id")
    private State state;

}