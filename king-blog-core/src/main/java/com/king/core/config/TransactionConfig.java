package com.king.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * transaction config
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Configuration
public class TransactionConfig {

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager platformTransactionManager = new DataSourceTransactionManager();
        platformTransactionManager.setDataSource(dataSource);
        return platformTransactionManager;
    }

}
