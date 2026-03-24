package org.example;

import org.apache.commons.codec.digest.DigestUtils;

public class Authentication {
    private final IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String login, String password) {
        User user = userRepository.getUser(login);
        if (user == null) {
            return null;
        }

        String hashedInput = hashPassword(password);
        if (user.getPassword().equals(hashedInput)) {
            return user;
        }
        return null;
    }

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}