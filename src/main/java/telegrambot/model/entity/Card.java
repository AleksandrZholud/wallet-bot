package telegrambot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@Data
@Entity
@Table(name = "cards")
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private BigDecimal balance;
}