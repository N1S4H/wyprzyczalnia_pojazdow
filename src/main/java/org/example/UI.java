package org.example;

import org.example.models.*;
import org.example.repositories.*;
import org.example.services.AuthService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class UI {
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;
    private final AuthService authService;
    private final Scanner scanner;

    public UI(VehicleRepository vehicleRepo, UserRepository userRepo, RentalRepository rentalRepo, AuthService authService) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
        this.authService = authService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- SYSTEM WYPOŻYCZALNI ---");
            System.out.println("1. Logowanie\n2. Rejestracja\n3. Wyjście");
            int choice = scanner.nextInt();

            if (choice == 1) {
                loginProcess();
            } else if (choice == 2) {
                registerProcess();
            } else {
                running = false;
            }
        }
    }

    private void loginProcess() {
        System.out.print("Login: ");
        String login = scanner.next();
        System.out.print("Hasło: ");
        String password = scanner.next();

        Optional<User> loggedUser = authService.login(login, password);
        loggedUser.ifPresent(this::showMainMenu);
    }

    private void registerProcess() {
        System.out.print("Nowy login: ");
        String login = scanner.next();
        System.out.print("Nowe hasło: ");
        String password = scanner.next();
        authService.register(login, password);
    }

    private void showMainMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            if (user.getRole() == Role.ADMIN) {
                loggedIn = adminMenu();
            } else {
                loggedIn = userMenu(user);
            }
        }
    }

    private boolean userMenu(User user) {
        System.out.println("\n1. Dostępne pojazdy\n2. Wypożycz\n3. Zwróć\n4. Wyloguj");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> showAvailableVehicles();
            case 2 -> rentVehicleAction(user);
            case 3 -> returnVehicleAction(user);
            case 4 -> { return false; }
        }
        return true;
    }

    private void showAvailableVehicles() {
        vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .forEach(System.out::println);
    }

    private void rentVehicleAction(User user) {
        System.out.print("Podaj ID pojazdu: ");
        String vId = scanner.next();
        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vId).isEmpty()) {
            Rental rental = new Rental();
            rental.setId(UUID.randomUUID().toString());
            rental.setUserId(user.getId());
            rental.setVehicleId(vId);
            rental.setRentDateTime(LocalDateTime.now().toString());
            rentalRepo.save(rental);
            System.out.println("Pojazd wypożyczony!");
        } else {
            System.out.println("Pojazd jest niedostępny.");
        }
    }

    private void returnVehicleAction(User user) {
        System.out.print("Podaj ID pojazdu do zwrotu: ");
        String vId = scanner.next();
        Optional<Rental> active = rentalRepo.findByVehicleIdAndReturnDateIsNull(vId);
        if (active.isPresent() && active.get().getUserId().equals(user.getId())) {
            Rental r = active.get();
            r.setReturnDateTime(LocalDateTime.now().toString());
            rentalRepo.save(r);
            System.out.println("Zwrot przyjęty.");
        } else {
            System.out.println("Nie masz aktywnego wypożyczenia dla tego auta.");
        }
    }

    private boolean adminMenu() {
        System.out.println("\n1. Lista aut\n2. Dodaj auto\n3. Usuń auto\n4. Wszystkie wypożyczenia\n5. Wyloguj");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> vehicleRepo.findAll().forEach(System.out::println);
            case 2 -> addVehicleAction();
            case 3 -> {
                System.out.print("ID do usunięcia: ");
                vehicleRepo.deleteById(scanner.next());
            }
            case 4 -> rentalRepo.findAll().forEach(System.out::println);
            case 5 -> { return false; }
        }
        return true;
    }

    private void addVehicleAction() {
        System.out.print("Marka: ");
        String brand = scanner.next();
        System.out.print("Model: ");
        String model = scanner.next();
        System.out.print("Cena: ");
        double price = scanner.nextDouble();

        Vehicle v = new Vehicle();
        v.setId(UUID.randomUUID().toString());
        v.setBrand(brand);
        v.setModel(model);
        v.setPrice(price);
        vehicleRepo.save(v);
        System.out.println("Pojazd dodany.");
    }
}
