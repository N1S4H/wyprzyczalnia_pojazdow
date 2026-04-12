package org.example.services;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            System.out.println("Błąd: Użytkownik o loginie " + login + " już istnieje!");
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .login(login)
                .passwordHash(hashedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(newUser);
        System.out.println("Zarejestrowano pomyślnie użytkownika: " + login);
        return true;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        System.out.println("Błąd: Niepoprawny login lub hasło.");
        return Optional.empty();
    }

}