package org.example;
import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.*;
import org.example.repositories.impl.*;
import org.example.services.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        boolean jdbc = true;

        VehicleRepository vehicleRepo;
        UserRepository userRepo;
        RentalRepository rentalRepo;

        if (jdbc) {
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new RuntimeException("Brak zmiennej środowiskowej DB_URL!");
            }
            vehicleRepo = new VehicleJdbcRepository();
            userRepo = new UserJdbcRepository();
            rentalRepo = new RentalJdbcRepository();
        } else {
            JsonFileStorage<User> userStorage = new JsonFileStorage<>("users.json", new TypeToken<List<User>>(){}.getType());
            JsonFileStorage<Vehicle> vehicleStorage = new JsonFileStorage<>("vehicles.json", new TypeToken<java.util.List<Vehicle>>(){}.getType());
            JsonFileStorage<Rental> rentalStorage = new JsonFileStorage<>("rentals.json", new TypeToken<java.util.List<Rental>>(){}.getType());

            userRepo = new UserJsonRepository(userStorage);
            vehicleRepo = new VehicleJsonRepository(vehicleStorage);
            rentalRepo = new RentalJsonRepository(rentalStorage);
        }
        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
        AuthService authService = new AuthService(userRepo);
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);

        RentalService rentalService = new RentalService(rentalRepo, vehicleRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        UserService userService = new UserService(userRepo, rentalService);

        UI ui = new UI(authService, vehicleService, rentalService, userService, configService);
        ui.start();
    }
}