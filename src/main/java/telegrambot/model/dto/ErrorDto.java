package telegrambot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private String message;
    private Integer code;

    public ErrorDto(String message) {
        this.message = message;
    }
}