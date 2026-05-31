package org.example.services;

import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository repository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository repository) {
        this.repository = repository;
    }

    public List<VehicleCategoryConfig> findAllCategories() {
        return repository.findAll();
    }

    public VehicleCategoryConfig getByCategory(String category) {
        return repository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Kategoria '" + category + "' nie jest obsługiwana."));
    }
}