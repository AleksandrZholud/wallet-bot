package telegrambot.config.interceptor;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.model.SendMessageFacade;

public class UserDataContextHolder {
    private UserDataContextHolder() {
    }

    private static final ThreadLocal<UserDataContext> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    public static void initContext(Update update) {
        CONTEXT_HOLDER.remove();

        if (update.getMessage() == null) {
            throw new IllegalArgumentException("Cannot resolve message. Please, send message with text to prevent this error.");
        }

        var context = new UserDataContext(update);
        CONTEXT_HOLDER.set(context);
    }

    public static String getInputtedTextCommand() {
        return CONTEXT_HOLDER.get().getUpdate().getMessage().getText();
    }

    public static SendMessageFacade getFacade() {
        return CONTEXT_HOLDER.get().getSendMessageFacade();
    }

    public static SendMessage performMessage() {
        SendMessage sendMessage = CONTEXT_HOLDER.get().getSendMessageFacade().performSendMsg();
        sendMessage.setText(validateOutputMessage(sendMessage.getText()));
        return sendMessage;
    }

    public static String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    public static String getSenderName() {
        return CONTEXT_HOLDER.get().getUpdate().getMessage().getChat().getFirstName();
    }

    public static Long getChatId() {
        return CONTEXT_HOLDER.get().getUpdate().getMessage().getChatId();
    }
}