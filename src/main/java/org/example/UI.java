package org.example;

import org.example.models.*;
import org.example.services.*;

import java.util.Scanner;

public class UI {
    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final UserService userService;
    private final VehicleCategoryConfigService configService;
    private final Scanner scanner;

    public UI(AuthService authService, VehicleService vehicleService, RentalService rentalService, UserService userService, VehicleCategoryConfigService configService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.userService = userService;
        this.configService = configService;
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

            String choice = scanner.nextLine().trim();
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
        String login = scanner.nextLine().trim();
        System.out.print("Hasło: ");
        String password = scanner.nextLine().trim();

        authService.login(login, password).ifPresentOrElse(
                user -> {
                    System.out.println("\nZalogowano pomyślnie!");
                    showMainMenu(user);
                },
                () -> System.out.println("Nieprawidłowy login lub hasło.")
        );
    }

    private void registerProcess() {
        System.out.print("Podaj nowy login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Podaj nowe hasło: ");
        String password = scanner.nextLine().trim();
        if (authService.register(login, password)) {
            System.out.println("Rejestracja zakończona sukcesem.");
        } else {
            System.out.println("Taki użytkownik już istnieje.");
        }
    }

    private void showMainMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            if (user.getRole() == Role.ADMIN) {
                loggedIn = adminMenu(user);
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

        String choice = scanner.nextLine().trim();
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
        vehicleService.findAvailableVehicles().forEach(System.out::println);
    }

    private void rentVehicleAction(User user) {
        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String vehicleId = scanner.nextLine().trim();

        try {
            rentalService.rentVehicle(user.getId(), vehicleId);
            System.out.println("Pojazd został wypożyczony!");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void returnVehicleAction(User user) {
        try {
            rentalService.returnVehicle(user.getId());
            System.out.println("Zwrot przyjęty pomyślnie.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private boolean adminMenu(User adminUser) {
        System.out.println("\n--- MENU ADMINISTRATORA ---");
        System.out.println("1. Lista wszystkich aut");
        System.out.println("2. Dodaj nowy pojazd");
        System.out.println("3. Usuń pojazd");
        System.out.println("4. Lista wszystkich wypożyczeń");
        System.out.println("5. Usuń użytkownika");
        System.out.println("0. Wyloguj");
        System.out.print("Wybór: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> showAllVehiclesWithStatus();
            case "2" -> addVehicleAction();
            case "3" -> deleteVehicleAction();
            case "4" -> rentalService.findAllRentals().forEach(System.out::println);
            case "5" -> deleteUserAction(adminUser);
            case "0" -> { return false; }
            default -> System.out.println("Nieprawidłowy wybór.");
        }
        return true;
    }

    private void showAllVehiclesWithStatus() {
        System.out.println("\n--- LISTA POJAZDÓW I ICH STATUS ---");
        vehicleService.findAllVehicles().forEach(v -> {
            String status = vehicleService.isVehicleRented(v.getId()) ? "[WYPOŻYCZONE]" : "[WOLNE]";
            System.out.println(v.toString() + " -> " + status);
        });
    }

    private void addVehicleAction() {
        System.out.println("\n--- DODAWANIE POJAZDU (Config-Driven) ---");

        System.out.print("Podaj kategorię (np. CAR, MOTORCYCLE): ");
        String category = scanner.nextLine().trim();

        try {
            VehicleCategoryConfig config = configService.getByCategory(category);

            System.out.print("Marka: "); String brand = scanner.nextLine().trim();
            System.out.print("Model: "); String model = scanner.nextLine().trim();
            System.out.print("Rok produkcji: "); int year = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Numer rejestracyjny: "); String plate = scanner.nextLine().trim();
            System.out.print("Cena za dobę: "); double price = Double.parseDouble(scanner.nextLine().trim());

            Vehicle vehicle = Vehicle.builder()
                    .category(config.getCategory())
                    .brand(brand)
                    .model(model)
                    .year(year)
                    .plate(plate)
                    .price(price)
                    .build();

            config.getAttributes().forEach((attrName, attrType) -> {
                System.out.print("Podaj wartość atrybutu " + attrName + " (" + attrType + "): ");
                String rawValue = scanner.nextLine().trim();

                Object value = switch (attrType.toLowerCase()) {
                    case "string" -> rawValue;
                    case "integer" -> Integer.parseInt(rawValue);
                    case "number" -> Double.parseDouble(rawValue);
                    case "boolean" -> Boolean.parseBoolean(rawValue);
                    default -> throw new IllegalArgumentException("Nieobsługiwany typ: " + attrType);
                };
                vehicle.addAttribute(attrName, value);
            });
            vehicleService.addVehicle(vehicle);
            System.out.println("Pojazd dodany pomyślnie!");

        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wpisano nieprawidłową liczbę.");
        } catch (Exception e) {
            System.out.println("Błąd dodawania pojazdu: " + e.getMessage());
        }
    }

    private void deleteVehicleAction() {
        System.out.print("Podaj ID do usunięcia: ");
        String id = scanner.nextLine().trim();
        try {
            vehicleService.removeVehicle(id);
            System.out.println("Pojazd został usunięty.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void deleteUserAction(User adminUser) {
        System.out.print("Podaj ID użytkownika do usunięcia: ");
        String targetId = scanner.nextLine().trim();
        try {
            userService.deleteUser(targetId, adminUser.getId());
            System.out.println("Użytkownik został usunięty.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }
}