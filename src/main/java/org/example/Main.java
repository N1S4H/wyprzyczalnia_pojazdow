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
        boolean jdbc = false;
        boolean hibernate = true;

        VehicleRepository vehicleRepo = null;
        UserRepository userRepo = null;
        RentalRepository rentalRepo = null;

        RentalService oldRentalService = null;
        RentalHibernateService newHibernateRentalService = null;

        if (hibernate) {
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new RuntimeException("Brak zmiennej środowiskowej DB_URL!");
            }
            RentalHibernateRepository rentalRepoHib = new RentalHibernateRepository();
            VehicleHibernateRepository vehicleRepoHib = new VehicleHibernateRepository();
            UserHibernateRepository userRepoHib = new UserHibernateRepository();

            rentalRepo = rentalRepoHib;
            vehicleRepo = vehicleRepoHib;
            userRepo = userRepoHib;

            newHibernateRentalService = new RentalHibernateService(rentalRepoHib, vehicleRepoHib, userRepoHib);

        } else if (jdbc) {
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new RuntimeException("Brak zmiennej środowiskowej DB_URL!");
            }
            vehicleRepo = new VehicleJdbcRepository();
            userRepo = new UserJdbcRepository();
            rentalRepo = new RentalJdbcRepository();

            oldRentalService = new RentalService(rentalRepo, vehicleRepo);

        } else {
            JsonFileStorage<User> userStorage = new JsonFileStorage<>("users.json", new TypeToken<List<User>>() {}.getType());
            JsonFileStorage<Vehicle> vehicleStorage = new JsonFileStorage<>("vehicles.json", new TypeToken<List<Vehicle>>() {}.getType());
            JsonFileStorage<Rental> rentalStorage = new JsonFileStorage<>("rentals.json", new TypeToken<List<Rental>>() {}.getType());

            userRepo = new UserJsonRepository(userStorage);
            vehicleRepo = new VehicleJsonRepository(vehicleStorage);
            rentalRepo = new RentalJsonRepository(rentalStorage);

            oldRentalService = new RentalService(rentalRepo, vehicleRepo);
        }

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);

        UserService userService = new UserService(userRepo, oldRentalService);

        RentalServiceInterface rentalServiceForUI = hibernate ? newHibernateRentalService : oldRentalService;

        UI ui = new UI(authService, vehicleService, rentalServiceForUI, userService, configService);
        ui.start();
    }
}