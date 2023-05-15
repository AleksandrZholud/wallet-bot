package telegrambot.service.state;

import telegrambot.model.util.State;

public interface StateService {

    State findByName(String name);
}
