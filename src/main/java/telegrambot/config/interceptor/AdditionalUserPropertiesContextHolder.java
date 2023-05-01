package telegrambot.config.interceptor;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.model.SendMessageFacade;

public class AdditionalUserPropertiesContextHolder {
    private AdditionalUserPropertiesContextHolder(){}

    private static final ThreadLocal<AdditionalUserPropertiesContext> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    public static void initContext(Update update) throws IllegalAccessException {
        CONTEXT_HOLDER.remove();

        if (update.getMessage() == null) {
            throw new IllegalAccessException("Cannot resolve message. Please, send message with text to prevent this error.");
        }

        var context = new AdditionalUserPropertiesContext(update);
        CONTEXT_HOLDER.set(context);
    }

    public static Update getUpdate() {
        return CONTEXT_HOLDER.get().getUpdate();
    }

    public static SendMessageFacade getFacade(){
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
    public static AdditionalUserPropertiesContext getContext(){
        return CONTEXT_HOLDER.get();
    };
}