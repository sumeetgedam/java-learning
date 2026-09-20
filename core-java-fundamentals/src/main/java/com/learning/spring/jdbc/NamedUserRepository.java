package com.learning.spring.jdbc;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class NamedUserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NamedUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findByNameAndEmail(
            String name,
            String email
    ){
        String sql =
                """
                SELECT id, name, email
                FROM users
                WHERE name = :name
                AND email = :email
                """;

        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue("name", name)
                        .addValue("email", email);

        return jdbcTemplate.query(
                sql,
                parameters,
                new UserRowMapper()
        );

    }
}
