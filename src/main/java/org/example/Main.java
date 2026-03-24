package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        IVehicleRepository vehicleRepo = new VehicleRepositoryImpl();
        IUserRepository userRepo = new UserRepository();
        Authentication auth = new Authentication(userRepo);
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- SYSTEM WYPOŻYCZALNI POJAZDÓW ---");

        // 1. LOGOWANIE
        System.out.print("Login: ");
        String login = scanner.next();
        System.out.print("Hasło: ");
        String password = scanner.next();

        User currentUser = auth.authenticate(login, password);

        if (currentUser != null) {
            System.out.println("\nZalogowano pomyślnie jako: " + currentUser.getRole());

            boolean running = true;
            while (running) {
                if (currentUser.getRole().equals("ADMIN")) {
                    running = showAdminMenu(scanner, vehicleRepo, userRepo);
                } else {
                    running = showUserMenu(scanner, vehicleRepo, userRepo, currentUser);
                }
            }
        } else {
            System.out.println("Błąd logowania! Nieprawidłowy login lub hasło.");
        }

        scanner.close();
    }

    // --- MENU DLA ADMINA ---
    private static boolean showAdminMenu(Scanner sc, IVehicleRepository vRepo, IUserRepository uRepo) {
        System.out.println("\n--- MENU ADMINA ---");
        System.out.println("1. Lista pojazdów");
        System.out.println("2. Lista użytkowników i ich wypożyczeń");
        System.out.println("3. Dodaj pojazd");
        System.out.println("4. Usuń pojazd");
        System.out.println("5. Wyjdź");
        System.out.print("Wybierz: ");

        int choice = sc.nextInt();
        switch (choice) {
            case 1 -> vRepo.getVehicles().forEach(System.out::println);
            case 2 -> uRepo.getUsers().forEach(u -> System.out.println(u.getLogin() + " - Wypożyczone ID: " + u.getRentedVehicleId()));
            case 3 -> {
                // Tu możesz dodać logikę tworzenia nowego auta
                System.out.println("Funkcja dodawania w przygotowaniu...");
            }
            case 4 -> {
                System.out.print("Podaj ID do usunięcia: ");
                String id = sc.next();
                if (vRepo.remove(id)) System.out.println("Usunięto.");
            }
            case 5 -> {
                return false;
            }
        }
        return true;
    }

    // --- MENU DLA UŻYTKOWNIKA ---
    private static boolean showUserMenu(Scanner sc, IVehicleRepository vRepo, IUserRepository uRepo, User user) throws IOException {
        System.out.println("\n--- MENU UŻYTKOWNIKA ---");
        System.out.println("1. Lista dostępnych pojazdów");
        System.out.println("2. Wypożycz pojazd");
        System.out.println("3. Zwróć pojazd");
        System.out.println("4. Moje dane");
        System.out.println("5. Wyjdź");
        System.out.print("Wybierz: ");

        int choice = sc.nextInt();
        switch (choice) {
            case 1 -> vRepo.getVehicles().stream().filter(v -> !v.isRented()).forEach(System.out::println);
            case 2 -> {
                System.out.print("Podaj ID do wypożyczenia: ");
                String id = sc.next();
                if (vRepo.rentVehicle(id)) {
                    user.setRentedVehicleId(id);
                    uRepo.update(user); // Zapisujemy info o wypożyczeniu u usera
                    System.out.println("Wypożyczono!");
                }
            }
            case 3 -> {
                if (vRepo.returnVehicle(user.getRentedVehicleId())) {
                    user.setRentedVehicleId("null");
                    uRepo.update(user);
                    System.out.println("Zwrócono!");
                }
            }
            case 4 -> {
                Vehicle v = vRepo.getVehicle(user.getRentedVehicleId());
                System.out.println("Użytkownik: " + user.getLogin() + " | Wypożyczony pojazd: " + (v != null ? v : "Brak"));
            }
            case 5 -> {
                return false;
            }
        }
        return true;
    }
}