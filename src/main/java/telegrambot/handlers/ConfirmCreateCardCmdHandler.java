package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.Card;
import telegrambot.model.util.CardDraft;
import telegrambot.model.util.Command;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.model.util.State;
import telegrambot.repository.CardRepository;
import telegrambot.repository.util.*;

import java.util.Optional;

import static telegrambot.model.enums.CommandEnum.*;
import static telegrambot.model.enums.StateEnum.NO_STATE;

@AllArgsConstructor
@Component
public class ConfirmCreateCardCmdHandler extends AbstractCmdHandler {

    private static final String THIS_CMD = CREATE_CARD_CONFIRM_COMMAND.getCommand();
    private final CardDraftRepository cardDraftRepository;
    private final CardRepository cardRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public boolean canProcessMessage() {
        return AdditionalUserPropertiesContextHolder.getUpdate().getMessage().getText().equals(THIS_CMD);
    }

    @Transactional
    @Override
    public void processMessage() {
        var currentCondition = currentConditionRepository.getFirst();

        String currentConditionName = currentCondition.getCommand().getName();
        if (currentConditionName.equals(CREATE_CARD_COMMAND.getCommand())) {
            confirmCard();
        } else {
            AdditionalUserPropertiesContextHolder.getFacade()
                    .setText("You can not confirm something from command '" + currentConditionName + "'");
        }
    }

    private void confirmCard() {
        var baseCommand = commandRepository.findByName(START_COMMAND.getCommand());
        var baseState = stateRepository.findByName(NO_STATE.getState());
        Optional<CardDraft> draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());

        Card cardToSave = null;

        if (draft.isEmpty()) {
            processStartCreateCard(baseCommand, baseState);
            return;
        }

        if (draft.get().getStatus().equals(DRAFT_STATUS.BUILT) ||
                draft.get().getStatus().equals(DRAFT_STATUS.SAVING)) {
            cardDraftRepository.updateStatus(DRAFT_STATUS.SAVING.name());
            cardToSave = cardRepository.save(Card.builder()
                    .name(draft.get().getName())
                    .balance(draft.get().getBalance())
                    .build());

        }

        if (cardToSave == null) {
            processErrorCreation();
            return;
        }

        cleanAllData();
        currentConditionRepository.updateCommandAndState(baseCommand.getId(), baseState.getId());

        processFinish(cardToSave);
    }

    private void processFinish(Card cardRes) {
        AdditionalUserPropertiesContextHolder.getFacade()
                .setText("Card '" + cardRes.getName() + "' successfully saved.\nGood luck!")
                .addStartButton();
    }

    private void processErrorCreation() {
        AdditionalUserPropertiesContextHolder.getFacade()
                .setText("Something got wrong....")
                .addButtons(false, CREATE_CARD_CONFIRM_COMMAND);
    }

    private void processStartCreateCard(Command command, State state) {
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        AdditionalUserPropertiesContextHolder.getFacade()
                .setText("Seems you have not started creating card.")
                .addButtons(false, CREATE_CARD_COMMAND);
    }

    @Transactional
    @Override
    public boolean cleanAllData() {
        cardDraftRepository.deleteAll();
        msgFromStateHistoryRepository.deleteAll();

        return msgFromStateHistoryRepository.findLast() == null && cardDraftRepository.getFirstDraft() == null;
    }
}