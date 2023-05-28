package telegrambot.service.command;

import telegrambot.model.util.Command;

public interface CommandService {

    Command findByName(String name);
}
