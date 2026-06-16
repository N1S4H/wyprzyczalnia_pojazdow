package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile({"json", "jdbc", "jpa"})
public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {

    private final JsonFileStorage<VehicleCategoryConfig> storage;
    private final List<VehicleCategoryConfig> configs;

    public VehicleCategoryConfigJsonRepository(@Value("${carrent.json.categories-file:categories.json}") String filename) {
        this.storage = new JsonFileStorage<>(filename, VehicleCategoryConfig.class);

        List<VehicleCategoryConfig> loaded = storage.load();
        this.configs = (loaded != null) ? new ArrayList<>(loaded) : new ArrayList<>();
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        List<VehicleCategoryConfig> copy = new ArrayList<>();
        for (VehicleCategoryConfig config : configs) {
            copy.add(config.copy());
        }
        return copy;
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return configs.stream()
                .filter(c -> c.getCategory() != null)
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .map(VehicleCategoryConfig::copy);
    }
}