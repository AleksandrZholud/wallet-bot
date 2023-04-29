package telegrambot.config.interceptor;


import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdditionalUserPropertiesContextHolder {
    private AdditionalUserPropertiesContextHolder(){}

    private static final ThreadLocal<AdditionalUserPropertiesContext> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    public static AdditionalUserPropertiesContext initContext(Update update) {
        CONTEXT_HOLDER.remove();

        var context = new AdditionalUserPropertiesContext();
        if (update.getMessage() == null) {
            Message message = update.getCallbackQuery().getMessage();
            String data = update.getCallbackQuery().getData();
            Chat chat = update.getCallbackQuery().getMessage().getChat();
            update.setMessage(message);
            update.getMessage().setChat(chat);
            update.getMessage().setText(data);
        }

        context.setUpdate(update);
        CONTEXT_HOLDER.set(context);
        return context;
    }

    public static AdditionalUserPropertiesContext getContext() {
        AdditionalUserPropertiesContext context = CONTEXT_HOLDER.get();

        if (context == null) {
            context = new AdditionalUserPropertiesContext();
            CONTEXT_HOLDER.set(context);
        }

        return context;
    }

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}