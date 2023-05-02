package telegrambot.model;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import telegrambot.model.enums.CommandEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;
import static telegrambot.model.enums.CommandEnum.START_COMMAND;

public class SendMessageFacade {

    private SendMessage sendMessage;
    private List<CommandEnum> buttonsSetList;

    private SendMessageFacade() {
    }

    public SendMessageFacade(@NotNull Long chatId) {
        SendMessage newSendMessage = new SendMessage();
        newSendMessage.setChatId(chatId);
        sendMessage = newSendMessage;
        buttonsSetList = new ArrayList<>(20);
    }

    public SendMessageFacade setText(String text) {
        sendMessage.setText(validateOutputMessage(text));
        return this;
    }

    public static String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    public SendMessageFacade addButtons(@NotNull @NotEmpty CommandEnum... commandEnums) {
        return acceptAddedButtons(commandEnums);
    }

    public SendMessageFacade addStartButton() {
        CommandEnum[] buttonsArray = {START_COMMAND};
        return acceptAddedButtons(buttonsArray);
    }

    public SendMessageFacade addBackButton() {
        CommandEnum[] buttonsArray = {GO_BACK_COMMAND};
        return acceptAddedButtons(buttonsArray);
    }

    public SendMessage performSendMsg() {
        performButtons();
        return sendMessage;
    }

    private void performButtons() {
        removeDuplicatedButtons();

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();
        fillKeyboardWithButtons(keyboard);

        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboard);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    private void removeDuplicatedButtons() {
        Set<CommandEnum> uniqueButtons = new HashSet<>(buttonsSetList);
        buttonsSetList = new ArrayList<>(uniqueButtons.size());
        buttonsSetList.addAll(uniqueButtons);
    }

    private void fillKeyboardWithButtons(List<KeyboardRow> keyboard) {
        int countOfButtons = buttonsSetList.size();
        int buttonsPerRow = (countOfButtons >= 3) ? 3 : 2;
        int rows = (int) Math.ceil((double) countOfButtons / buttonsPerRow);

        for (int i = 0; i < rows; i++) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < buttonsPerRow; j++) {
                int index = i * buttonsPerRow + j;
                if (index >= countOfButtons) {
                    break;
                }
                CommandEnum commandEnum = buttonsSetList.get(index);
                KeyboardButton button = new KeyboardButton();
                button.setText(commandEnum.getCommand());
                row.add(button);
            }
            keyboard.add(row);
        }
    }

    private SendMessageFacade acceptAddedButtons(@NotNull CommandEnum... commandEnums) {

        List<CommandEnum> removeNullButtons = new ArrayList<>(commandEnums.length);
        for (CommandEnum commandEnum : commandEnums) {
            if (commandEnum != null) {
                removeNullButtons.add(commandEnum);
            }
        }

        buttonsSetList.addAll(removeNullButtons);
        return this;
    }
}