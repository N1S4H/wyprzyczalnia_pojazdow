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
            System.out.println("\n--- SYSTEM WYPOŻYCZALNI POJAZDÓW ---");
            System.out.println("1. Logowanie");
            System.out.println("2. Rejestracja");
            System.out.println("3. Wyjście");
            System.out.print("Wybór: ");

            String choice = scanner.next();
            switch (choice) {
                case "1" -> loginProcess();
                case "2" -> registerProcess();
                case "3" -> running = false;
                default -> System.out.println("Nieprawidłowy wybór.");
            }
        }
    }

    private void loginProcess() {
        System.out.print("Login: ");
        String login = scanner.next();
        System.out.print("Hasło: ");
        String password = scanner.next();

        Optional<User> loggedUser = authService.login(login, password);
        if (loggedUser.isPresent()) {
            System.out.println("\nZalogowano pomyślnie!");
            showMainMenu(loggedUser.get());
        }
    }

    private void registerProcess() {
        System.out.print("Podaj nowy login: ");
        String login = scanner.next();
        System.out.print("Podaj nowe hasło: ");
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
        System.out.println("\n--- MENU UŻYTKOWNIKA ---");
        System.out.println("1. Lista dostępnych pojazdów");
        System.out.println("2. Wypożycz pojazd");
        System.out.println("3. Zwróć pojazd");
        System.out.println("4. Wyloguj");
        System.out.print("Wybór: ");

        String choice = scanner.next();
        switch (choice) {
            case "1" -> showAvailableVehicles();
            case "2" -> rentVehicleAction(user);
            case "3" -> returnVehicleAction(user);
            case "4" -> { return false; }
            default -> System.out.println("Nieprawidłowy wybór.");
        }
        return true;
    }

    private void showAvailableVehicles() {
        System.out.println("\n--- DOSTĘPNE POJAZDY ---");
        vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .forEach(System.out::println);
    }

    private void rentVehicleAction(User user) {
        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String vehicleId = scanner.next();

        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isEmpty()) {
            Rental rental = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .vehicleId(vehicleId)
                    .rentDateTime(LocalDateTime.now().toString())
                    .build();
            rentalRepo.save(rental);
            System.out.println("Pojazd został wypożyczony!");
        } else {
            System.out.println("Błąd: Ten pojazd jest obecnie niedostępny.");
        }
    }

    private void returnVehicleAction(User user) {
        System.out.print("Podaj ID pojazdu, który zwracasz: ");
        String vehicleId = scanner.next();

        Optional<Rental> activeRental = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId);

        if (activeRental.isPresent() && activeRental.get().getUserId().equals(user.getId())) {
            Rental r = activeRental.get();
            r.setReturnDateTime(LocalDateTime.now().toString());
            rentalRepo.save(r);
            System.out.println("Zwrot przyjęty pomyślnie.");
        } else {
            System.out.println("Błąd: Nie masz aktywnego wypożyczenia dla tego pojazdu.");
        }
    }

    private boolean adminMenu() {
        System.out.println("\n--- MENU ADMINISTRATORA ---");
        System.out.println("1. Lista wszystkich aut");
        System.out.println("2. Dodaj nowy pojazd");
        System.out.println("3. Usuń pojazd");
        System.out.println("4. Lista wszystkich wypożyczeń");
        System.out.println("5. Wyloguj");
        System.out.print("Wybór: ");

        String choice = scanner.next();
        switch (choice) {
            case "1" -> showAllVehiclesWithStatus();
            case "2" -> addVehicleAction();
            case "3" -> {
                System.out.print("Podaj ID do usunięcia: ");
                vehicleRepo.deleteById(scanner.next());
                System.out.println("Usunięto (jeśli istniało).");
            }
            case "4" -> rentalRepo.findAll().forEach(System.out::println);
            case "5" -> { return false; }
            default -> System.out.println("Nieprawidłowy wybór.");
        }
        return true;
    }

    private void showAllVehiclesWithStatus() {
        System.out.println("\n--- LISTA POJAZDÓW I ICH STATUS ---");
        vehicleRepo.findAll().forEach(v -> {
            Optional<Rental> active = rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId());
            String status = active.isPresent() ? "[WYPOŻYCZONE przez ID: " + active.get().getUserId() + "]" : "[WOLNE]";
            System.out.println(v.toString() + " -> " + status);
        });
    }

    private void addVehicleAction() {
        System.out.println("\n--- DODAWANIE POJAZDU ---");
        System.out.print("Marka: ");
        String brand = scanner.next();
        System.out.print("Model: ");
        String model = scanner.next();
        System.out.print("Rok produkcji: ");
        int year = scanner.nextInt();
        System.out.print("Numer rejestracyjny: ");
        String plate = scanner.next();
        System.out.print("Cena za dobę: ");
        double price = scanner.nextDouble();

        Vehicle newVehicle = Vehicle.builder()
                .id(UUID.randomUUID().toString())
                .brand(brand)
                .model(model)
                .year(year)
                .plate(plate)
                .price(price)
                .build();

        vehicleRepo.save(newVehicle);
        System.out.println("Pojazd dodany pomyślnie!");
    }
}