//package telegrambot.config.multitenancy;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
//import telegrambot.config.properties.DatabaseProperties;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//@RequiredArgsConstructor
//public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
//
//    private final DatabaseProperties databaseProperties;
//    private final Map<String, DataSource> dataSourceMap = new HashMap<>();
//
//    public void addDataSource(String tenantId, String url, String username, String password) {
//        DataSource dataSource
//                = createDataSource(databaseProperties.getJdbcUrl(),
//                databaseProperties.getUsername(),
//                databaseProperties.getPassword());
//        dataSourceMap.put(tenantId, dataSource);
//    }
//
//    private DataSource createDataSource(String url, String username, String password) {
//        // Создание и конфигурация DataSource для конкретного манданта
//        // Используйте полученный URL, имя пользователя и пароль
//        // Например, для использования HikariCP:
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(url);
//        config.setUsername(username);
//        config.setPassword(password);
//        return new HikariDataSource(config);
//    }
//
//    @Override
//    public Connection getAnyConnection() throws SQLException {
//        // Возвращает соединение из любого DataSource
//        return dataSourceMap.values().iterator().next().getConnection();
//    }
//
//    @Override
//    public void releaseAnyConnection(Connection connection) throws SQLException {
//        connection.close();
//    }
//
//    @Override
//    public Connection getConnection(String tenantId) throws SQLException {
//        // Возвращает соединение из соответствующего DataSource для заданного манданта
//        DataSource dataSource = dataSourceMap.get(tenantId);
//        return dataSource.getConnection();
//    }
//
//    @Override
//    public void releaseConnection(String tenantId, Connection connection) throws SQLException {
//        connection.close();
//    }
//
//    @Override
//    public boolean supportsAggressiveRelease() {
//        return true;
//    }
//
//    @Override
//    public boolean isUnwrappableAs(Class aClass) {
//        return false;
//    }
//
//    @Override
//    public <T> T unwrap(Class<T> aClass) {
//        throw new UnsupportedOperationException();
//    }
//}