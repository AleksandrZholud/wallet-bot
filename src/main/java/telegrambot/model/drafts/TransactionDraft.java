package telegrambot.model.drafts;

import lombok.*;
import telegrambot.model.entity.Card;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.TransactionTypeEnum;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "transaction_draft")
@AllArgsConstructor
@EqualsAndHashCode
public class TransactionDraft {

    @Id
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
    private DraftStatus status;
}
