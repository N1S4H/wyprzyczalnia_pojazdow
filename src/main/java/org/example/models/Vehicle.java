package org.example.models;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "vehicle")

public class Vehicle{

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;

    @Column(columnDefinition = "NUMERIC")
    private double price;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Object> attributes;

    @Builder
    public Vehicle(String id, String category, String brand, String model, int year, String plate, double price, Map<String, Object> attributes){
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.attributes = attributes == null ? new HashMap<>(): new HashMap<>(attributes);
    }

    public Map<String,Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }
    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

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