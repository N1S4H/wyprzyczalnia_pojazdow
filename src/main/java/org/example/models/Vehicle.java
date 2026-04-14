package org.example.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Vehicle implements Serializable{
    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;
    private Map<String, Object> attributes;


    public Vehicle copy() {
        return Vehicle.builder().id(this.id)
                .category(this.category)
                .brand(this.brand)
                .model(this.model)
                .year(this.year)
                .plate(this.plate)
                .price(this.price)
                .attributes(this.attributes != null ? new HashMap<>(this.attributes): new HashMap<>())
                .build();
    }

    public Map<String,Object> getAttributes() {
        return attributes != null? Collections.unmodifiableMap(attributes):Collections.emptyMap();
    }
    public Object getAttribute(String attributeName) {
        if(attributes == null) return null;
        return attributes.get(attributeName);
    }
    public void addAtribute(String key, Object value) {
        if(this.attributes == null){
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }
    public void removeAtribute(String key) {
        if(attributes != null){
            this.attributes.remove(key);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("[%s] %s %s %s (%d) | Rejestracja: %s| Cena: %.2f zł",
                category, id, brand, model, year, plate, price));
        if(attributes!=null && !attributes.isEmpty()) {
            builder.append("|Dodatki:").append(attributes);
        }
        return builder.toString();
    }

}
