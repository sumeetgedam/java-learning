package com.learning.spring.jdbc;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseInitializer {

    public DatabaseInitializer(DataSource dataSource) {
        try(Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("schema.sql")
            );
        }catch(SQLException exception) {
            throw new IllegalArgumentException(
                    "Could not initialize database", exception
            );
        }
    }
}
