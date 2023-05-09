package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.model.Card;
import telegrambot.model.enums.DRAFT_STATUS;
import telegrambot.model.enums.StateEnum;
import telegrambot.model.enums.TransactionTypeEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.model.util.State;
import telegrambot.model.util.drafts.TransactionDraft;
import telegrambot.repository.CardRepository;
import telegrambot.repository.util.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static telegrambot.model.enums.CommandEnum.*;
import static telegrambot.model.enums.StateEnum.*;

@AllArgsConstructor
@Component
public class CreateTransactionExecutor extends AbstractCommandExecutor {

    private static final String THIS_CMD = CREATE_TRANSACTION_COMMAND.getCommand();

    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;
    private final TransactionDraftRepository transactionDraftRepository;
    private final CardRepository cardRepository;
    private static List<List<String>> navigableList;
    private static int navigableListCurrentPosition;


    @Override
    public boolean isSystemHandler() {
        return false;
    }

    @Override
    public void processMessage() throws IllegalAccessException {

        if (UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD)) {
            transactionDraftRepository.deleteAll();
            currentConditionRepository.updateCommandAndState(4L, 1L);
            msgFromStateHistoryRepository.deleteAll();
        }

        CurrentCondition currentCondition = currentConditionRepository.getCurrentCondition();
        StateEnum currentState = StateEnum.findByState(currentCondition.getState().getName());

        switch (currentState) {
            case NO_STATE:
                doIfNoState();
                break;
            case CHOOSE_CARD:
                doIfChooseCard();
                break;
            case SET_TYPE:
                doIfSetType();
                break;
            case SET_AMOUNT:
                doIfSetAmount();
                break;
            default:
                doIfNothingExecuted();
        }
    }

    private void doIfNoState() {
        //перевірити чи не є команди право-ліво
        String msg = UserDataContextHolder.getInputtedTextCommand();
        if (msg.equals("/right")) {
            doIfRight();
            return;
        }
        if (msg.equals("/left")) {
            doIfLeft();
            return;
        }

        Command command = commandRepository.findByName(THIS_CMD);
        State state = stateRepository.findByName(CHOOSE_CARD.getState());
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        transactionDraftRepository.deleteAll();
        transactionDraftRepository.createFirstDraft();

        String answerMsg = "Choose card:";
        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(answerMsg)
                .build());

        List<String> cardNameList = cardRepository.findAll()
                .stream()
                .map(Card::getName)
                .sorted()
                .collect(Collectors.toList());
        if (cardNameList.isEmpty()) {
            doIfNoCards();
            return;
        }
        if (cardNameList.size() > 4) {
            doNavigableList(cardNameList);
            UserDataContextHolder.getFacade()
                    .setText(answerMsg)
//                    .addButtons(navigableList.get(0))
//                    .addButtonRight()
                    .addStartButton()
                    .addBackButton();
        } else {
            UserDataContextHolder.getFacade()
                    .setText(answerMsg)
//                .addButtons(cardNameList)
                    .addStartButton()
                    .addBackButton();
        }
    }

    private void doIfChooseCard() {
        Command command = commandRepository.findByName(THIS_CMD);
        State state = stateRepository.findByName(SET_TYPE.getState());
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        String chosenCardName = UserDataContextHolder.getInputtedTextCommand();

        Card chosenCardEntity = cardRepository.getByName(chosenCardName);
        transactionDraftRepository.updateCardId(chosenCardEntity.getId());

        String answerMsg = "Want to add INCOME or EXPENSE?";
        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(answerMsg)
                .build());

        //Дістаємо список кнопок
        List<String> trTypeList = Arrays.stream(TransactionTypeEnum.values())
                .map(TransactionTypeEnum::getName)
                .collect(Collectors.toList());

        //заповнити SendMessage кнопками
        UserDataContextHolder.getFacade()
                .setText(answerMsg)
//                .addButtons(trTypeList)
                .addStartButton();
    }

    private void doIfSetType() {
        Command command = commandRepository.findByName(THIS_CMD);
        State state = stateRepository.findByName(SET_AMOUNT.getState());
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        TransactionTypeEnum chosenTrType = TransactionTypeEnum.valueOf(UserDataContextHolder.getInputtedTextCommand());
        transactionDraftRepository.updateTransactionType(chosenTrType.getName());

        String answerMsg = "Input amount:";
        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(answerMsg)
                .build());

        UserDataContextHolder.getFacade()
                .setText(answerMsg)
                .addStartButton()
                .addBackButton();
    }

    private void doIfSetAmount() {
        Command command = commandRepository.findByName(THIS_CMD);
        State state = stateRepository.findByName(CONFIRMATION.getState());
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());


        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(UserDataContextHolder.getInputtedTextCommand()));
        transactionDraftRepository.updateStatusAndStatus(amount, DRAFT_STATUS.BUILT.name());
        TransactionDraft transactionDraft = transactionDraftRepository.getFirstDraft();

        String answerMsg = "Confirm transaction:"
                + "\n  Card: " + transactionDraft.getCard().getName()
                + "\n  Type: " + transactionDraft.getType()
                + "\nAmount: " + transactionDraft.getAmount();

        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(answerMsg)
                .build());

        UserDataContextHolder.getFacade()
                .setText(answerMsg)
                .addButtons(CREATE_TRANSACTION_CONFIRM_COMMAND)
                .addStartButton()
                .addBackButton();
    }

    //made synchronized to avoid SonarLint S2696
    private synchronized void doIfRight() {
        if (navigableListCurrentPosition == navigableList.size() - 2) {
            navigableListCurrentPosition++;
            UserDataContextHolder.getFacade()
                    .setText(null) // TODO: 08.05.2023 Ask ZH what if null? Msg wont show? Can we add mock button to fill out naviList?
//                    .addButtons(navigableList.get(navigableListCurrentPosition))
//                    .addButtonLeft()
                    .addStartButton()
                    .addBackButton();
        } else {
            navigableListCurrentPosition++;
            UserDataContextHolder.getFacade()
                    .setText(null)
//                    .addButtons(navigableList.get(navigableListCurrentPosition))
//                    .addButtonLeft()
//                    .addButtonRight()
                    .addStartButton()
                    .addBackButton();
        }
    }

    private synchronized void doIfLeft() {
        if (navigableListCurrentPosition == 1) {
            navigableListCurrentPosition--;
            UserDataContextHolder.getFacade()
                    .setText(null)
//                    .addButtons(navigableList.get(navigableListCurrentPosition))
//                    .addButtonRight()
                    .addStartButton()
                    .addBackButton();
        } else {
            navigableListCurrentPosition--;
            UserDataContextHolder.getFacade()
                    .setText(null)
//                    .addButtons(navigableList.get(navigableListCurrentPosition))
//                    .addButtonRight()
//                    .addButtonLeft()
                    .addStartButton()
                    .addBackButton();
        }

    }

    private static void doNavigableList(List<String> sourceList) {
        List<List<String>> list = new ArrayList<>();
        List<String> subList = new ArrayList<>();
        int subListSize = 4;

        for (String s : sourceList) {
            if (subList.size() == subListSize) {
                list.add(subList);
                subList = new ArrayList<>();
            }
            subList.add(s);
            if (sourceList.indexOf(s) == sourceList.size() - 1) {
                list.add(subList);
            }
        }
        navigableListCurrentPosition = 0;
        navigableList = list;
    }

    private void doIfNoCards() {
        Command command = commandRepository.findByName(START_COMMAND.getCommand());
        State state = stateRepository.findByName(NO_STATE.getState());
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        UserDataContextHolder.getFacade()
                .setText("Seems you have no any card yet;(\nCreate your first card.")
                .addButtons(CREATE_CARD_COMMAND)
                .addStartButton();
    }

    private void doIfNothingExecuted() {
        Command command = commandRepository.findByName(START_COMMAND.getCommand());
        State state = stateRepository.findByName(NO_STATE.getState());
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        cleanAllData();
        UserDataContextHolder.getFacade()
                .setText("Something gone wrong:(\nTry to create Transaction again.")
                .addButtons(CREATE_TRANSACTION_COMMAND)
                .addStartButton();
    }


    @Override
    public boolean canProcessMessage() {
        String currentCommandName = currentConditionRepository.getCurrentCondition().getCommand().getName();
        String message = UserDataContextHolder.getInputtedTextCommand();

        return message.equals(THIS_CMD) || currentCommandName.equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        transactionDraftRepository.deleteAll();
        return transactionDraftRepository.getFirstDraft() == null;
    }
}
