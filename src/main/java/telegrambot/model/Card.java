package telegrambot.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "cards")
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private boolean isGroupBalance;

    private boolean visibility;

    @Setter
    private BigDecimal balance;
}
