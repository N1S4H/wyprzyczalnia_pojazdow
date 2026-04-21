package org.example.services;

import org.example.models.User;
import org.example.repositories.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final RentalService rentalService;

    public UserService(UserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public void deleteUser(String targetUserId, String loggedInUserId) {
        if (targetUserId.equals(loggedInUserId)) {
            throw new IllegalStateException("Nie możesz usunąć samego siebie.");
        }

        boolean hasActiveRentals = rentalService.findActiveRentalByUserId(targetUserId).isPresent();
        if (hasActiveRentals) {
            throw new IllegalStateException("Nie można usunąć użytkownika, ponieważ ma wypożyczony pojazd.");
        }

        userRepository.deleteById(targetUserId);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));
    }
}