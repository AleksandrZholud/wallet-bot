package telegrambot.model.util.drafts;

import lombok.*;
import telegrambot.model.Card;
import telegrambot.model.enums.DRAFT_STATUS;
import telegrambot.model.enums.TransactionTypeEnum;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "transaction_draft")
@AllArgsConstructor
public class TransactionDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum type;

    @Setter
    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    @Setter
    @Column(name = "amount")
    private BigDecimal amount;

    @Setter
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DRAFT_STATUS status;
}
