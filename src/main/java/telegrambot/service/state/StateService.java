package telegrambot.service.state;

import telegrambot.model.util.State;

public interface StateService {

    public State findByName(String name);
}
