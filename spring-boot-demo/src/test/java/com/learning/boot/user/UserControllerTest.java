package com.learning.boot.user;

import com.learning.boot.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {

        given(userService.create(any(), any()))
                .willReturn(
                        new User(
                                "Alex",
                                "alex@example.com"
                        )
                );

        mockMvc.perform(
                post("/users")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("""
                                {
                                    "name": "Alex",
                                    "email": "alex@example.com"
                                }
                                """)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    void shouldRejectInvalidUSer() throws Exception {
        mockMvc.perform(
                post("/users")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("""
                                {
                                    "name": "",
                                    "email": "invalid"
                                }
                                """)

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));
    }

    @Test
    void shouldReturnUser() throws Exception {

        given(userService.findRequired(42L))
                .willReturn(
                        new User(
                                "Alex",
                                "alex@example.com"
                        )
                );

        mockMvc.perform(
                get("/users/42")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex"));
    }


    @Test
    void shouldReturnNotFound() throws Exception {

        given(userService.findRequired(99L))
                .willThrow(
                        new UserNotFoundException(99L)
                );

        mockMvc.perform(
                get("/users/99")

        )
                .andExpect(status().isNotFound());
    }
}
