package com.gerov.storedprocedurelight;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {

    @Bean
    public JdbcTemplate dbjdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}