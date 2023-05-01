package telegrambot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.enums.CommandEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;
import static telegrambot.model.enums.CommandEnum.START_COMMAND;

public class SendMessageUtils {
    private SendMessageUtils() {
    }

    public static void addButtons(SendMessage message, boolean showBack, boolean showStart, boolean confirm, CommandEnum... commandEnums) {
        List<CommandEnum> enums = new ArrayList<>(Arrays.asList(commandEnums));
        if (showBack) {
            enums.add(GO_BACK_COMMAND);
        }
        if (showStart) {
            enums.add(START_COMMAND);
        }
        if (confirm) {
            enums.add(START_COMMAND);
        }
        CommandEnum[] enumArray = enums.toArray(new CommandEnum[0]);
        addDefaultButtons(message, enumArray);
    }

    public static void addButtonsWithStart(SendMessage message, boolean showBack, CommandEnum... commandEnums) {
        addButtons(message, showBack, true, false, commandEnums);
    }

    private static void addDefaultButtons(SendMessage message, CommandEnum... commandEnums) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        int buttonsPerRow = (commandEnums.length >= 3) ? 3 : 2;
        int rows = (int) Math.ceil((double) commandEnums.length / buttonsPerRow);

        for (int i = 0; i < rows; i++) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < buttonsPerRow; j++) {
                int index = i * buttonsPerRow + j;
                if (index >= commandEnums.length) {
                    break;
                }
                CommandEnum commandEnum = commandEnums[index];
                KeyboardButton button = new KeyboardButton();
                button.setText(commandEnum.getCommand());
                row.add(button);
            }
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
    }

    public static SendMessage getSendMessageWithChatIdAndText(String text) {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(validateOutputMessage(text))
                .build();
    }

    public static String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    public static void addStartButton(SendMessage message) {
        CommandEnum[] enumArray = {START_COMMAND};
        addDefaultButtons(message, enumArray);
    }
}