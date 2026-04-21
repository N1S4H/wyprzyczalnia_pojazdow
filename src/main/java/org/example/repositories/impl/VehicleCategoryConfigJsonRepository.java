package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {

    private final JsonFileStorage<VehicleCategoryConfig> storage =
            new JsonFileStorage<>("categories.json",
                    new TypeToken<List<VehicleCategoryConfig>>() {}.getType());

    private final List<VehicleCategoryConfig> configs;

    public VehicleCategoryConfigJsonRepository() {
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