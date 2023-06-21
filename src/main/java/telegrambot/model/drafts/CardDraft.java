package telegrambot.model.drafts;

import lombok.*;
import telegrambot.model.enums.DraftStatus;

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
    private DraftStatus status;
}
