package com.learning.spring.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(String name, String email) {
        return jdbcTemplate.update(
                """
                        INSERT INTO users(name, email)
                        VALUES (?, ?)
                        """,
                name,
                email
        );
    }

    public List<User> findAll() {
        return jdbcTemplate.query(
                """
                    SELECT *
                    FROM users""",
                new UserRowMapper()
        );
    }

    public long saveAndReturnId(
            String name,
            String email
    ) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(
                            """
                            INSERT INTO users(name, email)
                            VALUES (?, ?)
                            """,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );
            statement.setString(1, name);
            statement.setString(2, email);

            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if(key == null) {
            throw new IllegalStateException(
                    "Database did not return generated key"
            );
        }
        return key.longValue();
    }
}
