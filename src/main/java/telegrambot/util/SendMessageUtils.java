package telegrambot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import telegrambot.model.enums.CommandEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;
import static telegrambot.model.enums.CommandEnum.START_COMMAND;

public class SendMessageUtils {
    private SendMessageUtils() {
    }

    public static void addButtons(SendMessage message, CommandEnum... commandEnums) {
        CommandEnum[] enums = Arrays.copyOf(commandEnums, commandEnums.length + 2);
        enums[commandEnums.length] = GO_BACK_COMMAND;
        enums[commandEnums.length + 1] = START_COMMAND;
        addDefaultButtons(message, enums);
    }

    private static void addDefaultButtons(SendMessage message, CommandEnum... commandEnums) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        int buttonsPerRow = (commandEnums.length >= 3) ? 3 : 2;
        int rows = (int) Math.ceil((double) commandEnums.length / buttonsPerRow);

        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = 0; j < buttonsPerRow; j++) {
                int index = i * buttonsPerRow + j;
                if (index >= commandEnums.length) {
                    break;
                }
                CommandEnum commandEnum = commandEnums[index];
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .text(commandEnum.getButtonText())
                        .callbackData(commandEnum.getCommand())
                        .build();
                rowInline.add(button);
            }
            rowsInline.add(rowInline);
        }

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
    }


    public static SendMessage getSendMessageWithChatIdAndText(Update update, String text) {
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(text)
                .build();
    }
}