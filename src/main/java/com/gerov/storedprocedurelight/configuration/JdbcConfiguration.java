package com.gerov.storedprocedurelight.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {

    @Profile("h2")
    @Bean
    @DependsOn("h2TcpServer")
    public JdbcTemplate dbjdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
