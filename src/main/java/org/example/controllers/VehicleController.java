package org.example.controllers;

import org.example.models.Vehicle;
import org.example.services.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> List(@RequestParam(name = "available", required = false, defaultValue = "false") boolean available){
        return available ? vehicleService.findAvailableVehicles() : vehicleService.findAllVehicles();
    }

    @GetMapping("/{id}")
    public Vehicle get(@PathVariable String id) {
        return vehicleService.findById(id);
    }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vehicleService.removeVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
