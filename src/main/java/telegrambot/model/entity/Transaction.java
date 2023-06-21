package telegrambot.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import telegrambot.model.enums.TransactionTypeEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    @Column(name = "timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false, unique = true)
    private Timestamp timestamp;

    @PrePersist
    protected void setTimestamp() {
        timestamp = Timestamp.valueOf(LocalDateTime.now());
    }
}
