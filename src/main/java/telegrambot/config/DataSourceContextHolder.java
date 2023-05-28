package telegrambot.config;

import org.springframework.util.Assert;

import javax.sql.DataSource;

public class DataSourceContextHolder {
    private static final ThreadLocal<DataSource> dataSourceHolder = new ThreadLocal<>();

    public static void setDataSource(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource cannot be null");
        dataSourceHolder.set(dataSource);
    }

    public static DataSource getDataSource() {
        return dataSourceHolder.get();
    }

    public static void clearDataSource() {
        dataSourceHolder.remove();
    }
}