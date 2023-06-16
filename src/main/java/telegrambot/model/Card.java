package telegrambot.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cards")
@AllArgsConstructor
@EqualsAndHashCode
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private BigDecimal balance;
}