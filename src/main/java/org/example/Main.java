package org.example;

import org.example.db.JsonFileStorage;
import org.example.models.*;
import org.example.repositories.*;
import org.example.repositories.impl.*;
import org.example.services.*;
import com.google.gson.reflect.TypeToken;

public class Main {
    public static void main(String[] args) {
        JsonFileStorage<User> userStorage = new JsonFileStorage<>("users.json", new TypeToken<java.util.List<User>>(){}.getType());
        JsonFileStorage<Vehicle> vehicleStorage = new JsonFileStorage<>("vehicles.json", new TypeToken<java.util.List<Vehicle>>(){}.getType());
        JsonFileStorage<Rental> rentalStorage = new JsonFileStorage<>("rentals.json", new TypeToken<java.util.List<Rental>>(){}.getType());

        UserRepository userRepo = new UserJsonRepository(userStorage);
        VehicleRepository vehicleRepo = new VehicleJsonRepository(vehicleStorage);
        RentalRepository rentalRepo = new RentalJsonRepository(rentalStorage);

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