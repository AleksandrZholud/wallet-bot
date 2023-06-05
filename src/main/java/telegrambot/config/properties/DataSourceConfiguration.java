package telegrambot.config.properties;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class DataSourceConfiguration {

    private final DefaultDatabaseProperties defaultDatabaseProperties;

    private final JpaProperties jpaProperties;

    private final ConfigurableListableBeanFactory beanFactory;

    @Value("${multi-tenancy.master.entityManager.packages}")
    private String entityPackages;

    @Bean
    @LiquibaseDataSource
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        String dbName = defaultDatabaseProperties.getDbName();
        dataSource.setJdbcUrl(defaultDatabaseProperties.getJdbcUrl(dbName));
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(defaultDatabaseProperties.getUsername());
        dataSource.setPassword(defaultDatabaseProperties.getPassword());

        dataSource.setConnectionTimeout(3000);
        dataSource.setIdleTimeout(10000);
        dataSource.setMaxLifetime(30000);

        dataSource.setMinimumIdle(0);
        dataSource.setMaximumPoolSize(2);

        dataSource.setPoolName("masterDataSource");

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");

        entityManagerFactory.setJpaPropertyMap(properties);
        entityManagerFactory.setPersistenceUnitName("master-persistence-unit");
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan(entityPackages);
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());

        return entityManagerFactory;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL10Dialect");
        return vendorAdapter;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    public String getName(){
        return defaultDatabaseProperties.getDbName();
    }

    public HikariDataSource getNewDataSource(String dbName) {
        defaultDatabaseProperties.setDbName(dbName);

        HikariDataSource newDataSource = new HikariDataSource();

        newDataSource.setDriverClassName("org.postgresql.Driver");
        newDataSource.setJdbcUrl(defaultDatabaseProperties.getJdbcUrl(dbName));
        newDataSource.setUsername(defaultDatabaseProperties.getUsername());
        newDataSource.setPassword(defaultDatabaseProperties.getPassword());

        newDataSource.setConnectionTimeout(3000);
        newDataSource.setIdleTimeout(10000);
        newDataSource.setMaxLifetime(30000);

        newDataSource.setMinimumIdle(0);
        newDataSource.setMaximumPoolSize(2);

        return newDataSource;
    }
}