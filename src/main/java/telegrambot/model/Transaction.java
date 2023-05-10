package telegrambot.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import telegrambot.model.enums.TransactionTypeEnum;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
