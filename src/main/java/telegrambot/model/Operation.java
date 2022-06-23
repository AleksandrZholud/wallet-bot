package telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operations")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private OPERATION_TYPE type;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    @JsonIgnoreProperties("operation")
    private Category category;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "cardId", referencedColumnName = "id")
    @JsonIgnoreProperties("card")
    private Card card;

    private BigDecimal amount;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    public Operation(Card card, OPERATION_TYPE type, Category category, BigDecimal amount, LocalDateTime createDate) {
        this.card = card;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.createDate = createDate;
    }
}
