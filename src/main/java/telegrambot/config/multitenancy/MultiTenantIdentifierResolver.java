//package telegrambot.config.multitenancy;
//
//import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
//
//public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
//
//    @Override
//    public String resolveCurrentTenantIdentifier() {
//        // Извлекайте идентификатор манданта из полученного URL или другого источника
//        // и возвращайте его
//        return "tenantId";
//    }
//
//    @Override
//    public boolean validateExistingCurrentSessions() {
//        return true;
//    }
//}