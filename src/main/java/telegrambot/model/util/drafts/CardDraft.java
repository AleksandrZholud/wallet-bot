package telegrambot.model.util.drafts;

import lombok.*;
import telegrambot.model.enums.DRAFT_STATUS;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "card_draft")
public class CardDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "balance")
    private BigDecimal balance;

    @Setter
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DRAFT_STATUS status;
}
