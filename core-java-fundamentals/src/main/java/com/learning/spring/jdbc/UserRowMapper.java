package com.learning.spring.jdbc;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(
            ResultSet resultSet,
            int rowNumber
    ) throws SQLException {

        return new User(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("email")
        );
    }
}
