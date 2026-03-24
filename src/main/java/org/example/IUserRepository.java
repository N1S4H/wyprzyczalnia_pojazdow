package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User user) throws IOException;
    boolean register(String login, String password);
    boolean removeUser(String login) throws IOException;
}