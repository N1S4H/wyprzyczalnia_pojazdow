package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserJsonRepository implements UserRepository {
    private final JsonFileStorage<User> storage;
    private final List<User> users;

    public UserJsonRepository(JsonFileStorage<User> storage) {
        this.storage = storage;
        List<User> loadedData = storage.load();
        this.users = (loadedData != null) ? loadedData : new ArrayList<>();
    }

    @Override
    public List<User> findAll() {
        return users.stream()
                .map(User::copy)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .map(User::copy)
                .findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .map(User::copy)
                .findFirst();
    }

    @Override
    public User save(User user) {
        Optional<User> existingUser = users.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(user.getLogin()))
                .findFirst();

        if (existingUser.isPresent()) {
            int index = users.indexOf(existingUser.get());
            users.set(index, user.copy());
        } else {
            users.add(user.copy());
        }

        storage.save(users);
        return user;
    }

    @Override
    public void deleteById(String id) {
        if (users.removeIf(u -> u.getId().equals(id))) {
            storage.save(users);
        }
    }
}