package com.learning.boot.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private final UserRepository repository =
            mock(UserRepository.class);

    private final UserService service =
            new UserService(repository);

    @Test
    void shouldRejectDuplicate() {
        when(repository.existsByEmail("alex@example.com"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.create(
                        "Alex",
                        "alex@example.com"
                ))
                .isInstanceOf(
                        IllegalStateException.class
                );

        verify(repository, never()).save(any());
    }
}
