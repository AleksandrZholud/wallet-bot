package telegrambot.config.interceptor;


public class AdditionalUserPropertiesContextHolder {
    private AdditionalUserPropertiesContextHolder(){}

    private static final ThreadLocal<AdditionalUserPropertiesContext> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    public static AdditionalUserPropertiesContext initContext() {
        CONTEXT_HOLDER.remove();

        var context = new AdditionalUserPropertiesContext();
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
