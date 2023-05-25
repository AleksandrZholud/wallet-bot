//package telegrambot.config.multitenancy;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class MultiTenantDataSourceConfig {
//
//    @Value("${spring.datasource.url}")
//    private String defaultUrl;
//
//    @Value("${spring.datasource.username}")
//    private String defaultUsername;
//
//    @Value("${spring.datasource.password}")
//    private String defaultPassword;
//
//    @Autowired
//    private TenantRepository tenantRepository;
//
//    @Bean
//    public AbstractRoutingDataSource multiTenantDataSource() {
//        MultitenantDataSource dataSource = new MultitenantDataSource();
//
//        Map<Object, Object> targetDataSources = new HashMap<>();
//        targetDataSources.put("default", createDataSource(defaultUrl, defaultUsername, defaultPassword));
//
//        tenantRepository.findAll().forEach(tenant -> {
//            String tenantId = tenant.getId();
//            String url = tenant.getUrl();
//            String username = tenant.getUsername();
//            String password = tenant.getPassword();
//            targetDataSources.put(tenantId, createDataSource(url, username, password));
//        });
//
//        dataSource.setTargetDataSources(targetDataSources);
//        dataSource.setDefaultTargetDataSource(createDataSource(defaultUrl, defaultUsername, defaultPassword));
//
//        return dataSource;
//    }
//
//    @Bean
//    @Primary
//    public DataSource dataSource(AbstractRoutingDataSource multiTenantDataSource) {
//        return multiTenantDataSource;
//    }
//
//    private DataSource createDataSource(String url, String username, String password) {
//        return DataSourceBuilder.create()
//                .url(url)
//                .username(username)
//                .password(password)
//                .build();
//    }
//}