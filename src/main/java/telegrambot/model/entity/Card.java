package telegrambot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Data
@Entity
@Table(name = "cards")
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private BigDecimal balance;
}