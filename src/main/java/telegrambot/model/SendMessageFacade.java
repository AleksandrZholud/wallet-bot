package telegrambot.model;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import telegrambot.model.enums.CommandEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;
import static telegrambot.model.enums.CommandEnum.START_COMMAND;

public class SendMessageFacade {

    private SendMessage sendMessage;

    private SendMessageFacade(){}

    public SendMessageFacade(Long chatId){
        SendMessage newSendMessage = new SendMessage();
        newSendMessage.setChatId(chatId);
        sendMessage = newSendMessage;
    }

    public SendMessageFacade setText(String text){
        sendMessage.setText(validateOutputMessage(text));
        return this;
    }

    public static String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    private SendMessageFacade addButtons(CommandEnum... commandEnums) {
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
        this.sendMessage.setReplyMarkup(keyboardMarkup);
        return this;
    }

    public SendMessageFacade addButtons(boolean showBack, boolean showStart, CommandEnum... commandEnums) {
        List<CommandEnum> enums = new ArrayList<>(Arrays.asList(commandEnums));
        if (showBack) {
            enums.add(GO_BACK_COMMAND);
        }
        if (showStart) {
            enums.add(START_COMMAND);
        }
        CommandEnum[] enumArray = enums.toArray(new CommandEnum[0]);
        return addButtons(enumArray);
    }

    public SendMessageFacade addButtons(boolean showBack, CommandEnum... commandEnums) {
        return addButtons(showBack, true, commandEnums);
    }

    public SendMessageFacade addStartButton() {
        CommandEnum[] buttonsArray = {START_COMMAND};
        return addButtons(buttonsArray);
    }

    public SendMessageFacade addBackButton() {
        CommandEnum[] buttonsArray = {GO_BACK_COMMAND};
        return addButtons(buttonsArray);
    }

    public String getText() {
        return sendMessage.getText();
    }

    public SendMessage performSendMsg() {
        return sendMessage;
    }
}
