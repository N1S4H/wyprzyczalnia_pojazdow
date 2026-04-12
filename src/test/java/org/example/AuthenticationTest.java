package org.example;

import org.example.models.User;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    @Test
    void shouldAuthenticateUserWithCorrectLoginAndPassword() throws FileNotFoundException {
        IUserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("admin", "admin123");

        assertNotNull(user);
        assertEquals("admin", user.getLogin());
    }

    @Test
    void shouldNotAuthenticateUserWithWrongPassword() throws FileNotFoundException {
        IUserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("admin", "zlehaslo");

        assertNull(user);
    }

    @Test
    void shouldNotAuthenticateNonExistingUser() throws FileNotFoundException {
        IUserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("brak", "admin123");

        assertNull(user);
    }

    @Test
    void hashPasswordShouldReturnSameHashForSameInput() {
        String hash1 = Authentication.hashPassword("admin123");
        String hash2 = Authentication.hashPassword("admin123");

        assertEquals(hash1, hash2);
    }
}