package com.gerov.storedprocedurelight;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {

    @Bean
    @DependsOn("h2TcpServer")
    public JdbcTemplate dbjdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}