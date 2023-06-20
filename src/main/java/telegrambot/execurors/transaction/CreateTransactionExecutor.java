package telegrambot.execurors.transaction;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;
import telegrambot.model.drafts.TransactionDraft;
import telegrambot.model.entity.Card;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.StateEnum;
import telegrambot.model.enums.TransactionTypeEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.model.util.State;
import telegrambot.service.card.CardService;
import telegrambot.service.command.CommandService;
import telegrambot.service.current_condition.CurrentConditionService;
import telegrambot.service.state.StateService;
import telegrambot.service.state_history.MsgFromStateHistoryService;
import telegrambot.service.transaction_draft.TransactionDraftService;

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


    private final CurrentConditionService currentConditionService;
    private final CommandService commandService;
    private final StateService stateService;
    private final MsgFromStateHistoryService msgFromStateHistoryService;
    private final TransactionDraftService transactionDraftService;
    private final CardService cardService;
    private static final String THIS_CMD = CREATE_TRANSACTION_COMMAND.getCommand();
    private static List<List<String>> navigableList;
    private static int navigableListCurrentPosition;


    @Override
    public boolean isSystemExecutor() {
        return false;
    }

    @Override
    public void exec() {

        if (UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD)) {
            transactionDraftService.deleteAll();
            currentConditionService.updateCommandAndState(CREATE_TRANSACTION_COMMAND, NO_STATE);
            msgFromStateHistoryService.deleteAll();
        }

        CurrentCondition currentCondition = currentConditionService.getCurrentCondition();
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
        Command command = commandService.getByName(THIS_CMD);
        State state = stateService.findByName(CHOOSE_CARD.getState());
        currentConditionService.updateCommandAndState(command, state);

        transactionDraftService.deleteAll();
        transactionDraftService.createSingleDraft();

        String answerMsg = "Choose card:";
        msgFromStateHistoryService.save(MsgFromStateHistory.builder()
                .message(answerMsg)
                .build());

        List<String> cardNameList = cardService.getAll()
                .stream()
                .map(Card::getName)
                .sorted()
                .collect(Collectors.toList());
        if (cardNameList.isEmpty()) {
            doIfNoCards();
            return;
        }
        doNavigableList(cardNameList);
        if (cardNameList.size() > 4) {
            UserDataContextHolder.getFacade()
                    .setText(answerMsg)
                    .addButtons(navigableList.get(0))
                    .addButtonRight()
                    .addStartButton();
        } else {
            UserDataContextHolder.getFacade()
                    .setText(answerMsg)
                    .addButtons(navigableList.get(0))
                    .addStartButton();
        }
    }

    private void doIfChooseCard() {
        //перевірити чи не є команди право-ліво
        String msg = UserDataContextHolder.getInputtedTextCommand();
        if (msg.equals(RIGHT_COMMAND.getCommand())) {
            doIfRight();
            return;
        }
        if (msg.equals(LEFT_COMMAND.getCommand())) {
            doIfLeft();
            return;
        }

        Command command = commandService.getByName(THIS_CMD);
        State state = stateService.findByName(SET_TYPE.getState());

        String inputtedText = UserDataContextHolder.getInputtedTextCommand();
        Card chosenCardEntity = cardService.getByName(inputtedText);

        if (chosenCardEntity != null) {
            currentConditionService.updateCommandAndState(command, state);
            transactionDraftService.updateCard(chosenCardEntity);
            String answerMsg = "Want to add INCOME or EXPENSE?";
            msgFromStateHistoryService.save(MsgFromStateHistory.builder()
                    .message(answerMsg)
                    .build());

            UserDataContextHolder.getFacade()
                    .setText(answerMsg)
                    .addButtons(Arrays.stream(TransactionTypeEnum.values())
                            .map(TransactionTypeEnum::getName)
                            .collect(Collectors.toList()))
                    .addStartButton()
                    .addBackButton();
        } else {
            addButtonsToFacadeDependsOnNaviListSize();
            UserDataContextHolder.getFacade()
                    .setText("Card '" + inputtedText + "' not found:(\nType correct Card name or create new Card.")
                    .addButtons(CREATE_CARD_COMMAND)
                    .addStartButton()
                    .addBackButton();
        }
    }

    private void doIfSetType() {
        TransactionTypeEnum chosenTrType;
        try {
            chosenTrType = TransactionTypeEnum.getByName(UserDataContextHolder.getInputtedTextCommand());
        } catch (IllegalStateException ise) {
            UserDataContextHolder.getFacade()
                    .setText("No no no...\nChoose transaction type.")
                    .addButtons(Arrays.stream(TransactionTypeEnum.values())
                            .map(TransactionTypeEnum::getName)
                            .collect(Collectors.toList()))
                    .addStartButton()
                    .addBackButton();
            return;
        }

        Command command = commandService.getByName(THIS_CMD);
        State state = stateService.findByName(SET_AMOUNT.getState());
        currentConditionService.updateCommandAndState(command, state);

        transactionDraftService.updateTransactionType(chosenTrType);

        String answerMsg = "Input amount:";
        msgFromStateHistoryService.save(MsgFromStateHistory.builder()
                .message(answerMsg)
                .build());

        UserDataContextHolder.getFacade()
                .setText(answerMsg)
                .addStartButton()
                .addBackButton();
    }

    private void doIfSetAmount() {
        BigDecimal amount;
        try {
            amount = BigDecimal.valueOf(Double.parseDouble(UserDataContextHolder.getInputtedTextCommand()));
        } catch (NumberFormatException nfe) {
            UserDataContextHolder.getFacade()
                    .setText("Input correct decimal amount.\nUse numbers please!")
                    .addStartButton()
                    .addBackButton();
            return;
        }

        TransactionDraft draft = transactionDraftService.getFirstDraft();
        BigDecimal cardAmount = cardService.getByName(draft.getCard().getName()).getBalance();
        if (draft.getType().equals(TransactionTypeEnum.EXPENSE) && cardAmount.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            UserDataContextHolder.getFacade()
                    .setText("Not enough money on card '"+draft.getCard().getName()+"'\nYou can spend only "+cardAmount+" UAH :(")
                    .addStartButton()
                    .addBackButton();
            return;
        }

        Command command = commandService.getByName(THIS_CMD);
        State state = stateService.findByName(CONFIRMATION.getState());
        currentConditionService.updateCommandAndState(command, state);

        transactionDraftService.updateAmountAndStatus(amount, DraftStatus.BUILT);

        TransactionDraft transactionDraft = transactionDraftService.getFirstDraft();
        String answerMsg = "Confirm transaction:"
                + "\n  Card: " + transactionDraft.getCard().getName()
                + "\n  Type: " + transactionDraft.getType()
                + "\nAmount: " + transactionDraft.getAmount();

        msgFromStateHistoryService.save(MsgFromStateHistory.builder()
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
                    .setText("Page " + (navigableListCurrentPosition + 1))
                    .addButtons(navigableList.get(navigableListCurrentPosition))
                    .addButtonLeft()
                    .addStartButton();
        } else {
            navigableListCurrentPosition++;
            UserDataContextHolder.getFacade()
                    .setText("Page " + (navigableListCurrentPosition + 1))
                    .addButtons(navigableList.get(navigableListCurrentPosition))
                    .addButtonLeft()
                    .addButtonRight()
                    .addStartButton();
        }
    }

    private synchronized void doIfLeft() {
        if (navigableListCurrentPosition == 1) {
            navigableListCurrentPosition--;
            UserDataContextHolder.getFacade()
                    .setText("Page " + (navigableListCurrentPosition + 1))
                    .addButtons(navigableList.get(navigableListCurrentPosition))
                    .addButtonRight()
                    .addStartButton();
        } else {
            navigableListCurrentPosition--;
            UserDataContextHolder.getFacade()
                    .setText("Page " + (navigableListCurrentPosition + 1))
                    .addButtons(navigableList.get(navigableListCurrentPosition))
                    .addButtonRight()
                    .addButtonLeft()
                    .addStartButton();
        }

    }

    private void addButtonsToFacadeDependsOnNaviListSize() {
        if (navigableList.size() > 1) {
            UserDataContextHolder.getFacade()
                    .addButtons(navigableList.get(0))
                    .addButtonRight();
        } else {
            UserDataContextHolder.getFacade()
                    .addButtons(navigableList.get(0));
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
        Command command = commandService.getByName(START_COMMAND.getCommand());
        State state = stateService.findByName(NO_STATE.getState());
        currentConditionService.updateCommandAndState(command, state);
        UserDataContextHolder.getFacade()
                .setText("Seems you have no any card yet;(\nCreate your first card.")
                .addButtons(CREATE_CARD_COMMAND)
                .addStartButton();
    }

    private void doIfNothingExecuted() {
        Command command = commandService.getByName(START_COMMAND.getCommand());
        State state = stateService.findByName(NO_STATE.getState());
        currentConditionService.updateCommandAndState(command, state);
        cleanAllData();
        UserDataContextHolder.getFacade()
                .setText("Something gone wrong:(\nTry to create Transaction again.")
                .addButtons(CREATE_TRANSACTION_COMMAND)
                .addStartButton();
    }


    @Override
    public boolean canExec() {
        String currentCommandName = currentConditionService.getCurrentCondition().getCommand().getName();
        String message = UserDataContextHolder.getInputtedTextCommand();

        return message.equals(THIS_CMD) || currentCommandName.equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        transactionDraftService.deleteAll();
        return transactionDraftService.isEmpty();
    }
}
