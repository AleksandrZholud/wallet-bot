package telegrambot.model.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "command_state_message_history")
public class MsgFromStateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "message", nullable = false, unique = true)
    private String message;

    @Column(name = "timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false, unique = true)
    private Timestamp timestamp;

    @PrePersist
    protected void setTimestamp() {
        timestamp = Timestamp.valueOf(LocalDateTime.now());
    }
}
