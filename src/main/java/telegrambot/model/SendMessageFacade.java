package telegrambot.model;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import telegrambot.model.enums.CommandEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;
import static telegrambot.model.enums.CommandEnum.START_COMMAND;

public class SendMessageFacade {
    private SendMessageFacade() {}

    private SendMessage sendMessage;
    private List<CommandEnum> buttonsCommandList;
    private List<String> buttonsStringList;


    public SendMessageFacade(@NotNull Long chatId) {
        SendMessage newSendMessage = new SendMessage();
        newSendMessage.setChatId(chatId);
        sendMessage = newSendMessage;
        buttonsCommandList = new ArrayList<>(20);
        buttonsStringList = new ArrayList<>(20);
    }

    public SendMessageFacade setText(String text) {
        sendMessage.setText(validateOutputMessage(text));
        return this;
    }

    public static String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    public SendMessageFacade addButtons(String... stringButtons) {
        return acceptStringButtons(stringButtons);
    }

    public SendMessageFacade addButtonLeft() {
        return acceptStringButtons("←");
    }

    public SendMessageFacade addButtonRight() {
        return acceptStringButtons("→");
    }

    public SendMessageFacade addButtons(@NotNull @NotEmpty List<String> stringButtons) {
        List<String> removeNullButtons = new ArrayList<>(stringButtons.size());
        for (String stringButton : stringButtons) {
            if(stringButton != null && !stringButton.isBlank()) {
                removeNullButtons.add(stringButton);
            }
        }
        buttonsStringList.addAll(removeNullButtons);
        return this;
    }

    private SendMessageFacade acceptStringButtons(@NotNull String... stringButtons){

        List<String> removeNullButtons = new ArrayList<>(stringButtons.length);
        for (String stringButton : stringButtons) {
            if (stringButton != null && !stringButton.isBlank()) {
                removeNullButtons.add(stringButton);
            }
        }
        buttonsStringList.addAll(removeNullButtons);

        return this;
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

    private SendMessageFacade acceptAddedButtons(@NotNull CommandEnum... commandEnums) {

        List<CommandEnum> removeNullButtons = new ArrayList<>(commandEnums.length);
        for (CommandEnum commandEnum : commandEnums) {
            if (commandEnum != null) {
                removeNullButtons.add(commandEnum);
            }
        }

        buttonsCommandList.addAll(removeNullButtons);
        return this;
    }

    public SendMessage performSendMsg() {
        performButtons();
        return sendMessage;
    }

    private void performButtons() {
        removeDuplicatedButtons();

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        sortButtons();

        fillKeyboardWithButtons(keyboard);

        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboard);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    private void sortButtons() {
        Collections.sort(buttonsStringList);
        buttonsCommandList.sort(Collections.reverseOrder());
    }

    private void removeDuplicatedButtons() {
        Set<CommandEnum> uniqueButtons = new HashSet<>(buttonsCommandList);
        buttonsCommandList = new ArrayList<>(uniqueButtons.size());
        buttonsCommandList.addAll(uniqueButtons);

        Set<String> uniqueButtonsString = new HashSet<>(buttonsStringList);
        buttonsStringList = new ArrayList<>(uniqueButtonsString.size());
        buttonsStringList.addAll(uniqueButtonsString);
    }

    private void fillKeyboardWithButtons(List<KeyboardRow> keyboard) {
        int countOfButtons = buttonsCommandList.size() + buttonsStringList.size();
        int buttonsPerRow = (countOfButtons >= 3) ? 3 : 2;
        int rows = (int) Math.ceil((double) countOfButtons / buttonsPerRow);

        int buttonStringIndex = 0;
        int buttonCommandIndex = 0;

        for (int i = 0; i < rows; i++) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < buttonsPerRow && buttonCommandIndex < buttonsCommandList.size(); j++) {
                KeyboardButton button;
                if (buttonStringIndex < buttonsStringList.size()) {
                    button = new KeyboardButton(buttonsStringList.get(buttonStringIndex));
                    buttonStringIndex++;
                } else {
                    CommandEnum commandEnum = buttonsCommandList.get(buttonCommandIndex);
                    button = new KeyboardButton(commandEnum.getCommand());
                    buttonCommandIndex++;
                }
                row.add(button);
            }
            keyboard.add(row);
        }
    }
}