package org.example.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.db.JdbcConnectionManager;
import org.example.models.Role;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.hibernate.engine.transaction.jta.platform.internal.OC4JJtaPlatform;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
@Profile("jdbc")
public class VehicleJdbcRepository implements VehicleRepository {
    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
    private final DataSource dataSource;

    public VehicleJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try(PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()){

            while (rs.next()){
                String attrJson = rs.getString("attributes");
                Map<String, Object> attributes = gson.fromJson(attrJson, new TypeToken<Map<String, Object>>(){}.getType());

                Vehicle vehicle = Vehicle.builder()
                        .id(rs.getString("id"))
                        .category(rs.getString("category"))
                        .brand(rs.getString("brand"))
                        .model(rs.getString("model"))
                        .year(rs.getInt("year"))
                        .plate(rs.getString("plate"))
                        .price(rs.getDouble("price"))
                        .attributes(attributes != null ? attributes : new HashMap<>())
                        .build();
                list.add(vehicle);
            }
        }catch (SQLException e){
            throw new RuntimeException("Blad odczytu pojazdow", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return list;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, id);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()) {
                    String attrJson = rs.getString("attributes");
                    Map<String, Object> attributes = gson.fromJson(attrJson, new TypeToken<Map<String, Object>>() {}.getType());

                    Vehicle vehicle = Vehicle.builder()
                            .id(rs.getString("id"))
                            .category(rs.getString("category"))
                            .brand(rs.getString("brand"))
                            .model(rs.getString("model"))
                            .year(rs.getInt("year"))
                            .plate(rs.getString("plate"))
                            .price(rs.getDouble("price"))
                            .attributes(attributes != null ? attributes : new HashMap<>())
                            .build();
                    return Optional.of(vehicle);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("blad odczytu pojazdu", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        String sql = """
                INSERT INTO vehicle (id, brand, model, year, plate, price, category, attributes)
                Values(?, ?, ?, ?, ?, ?, ?, ?::jsonb)
                ON CONFLICT (id) DO UPDATE SET
                    brand = EXCLUDED.brand,
                    model = EXCLUDED.model,
                    year = EXCLUDED.year, 
                    plate = EXCLUDED.plate,
                    price = EXCLUDED.price,
                    category = EXCLUDED.category,
                    attributes = EXCLUDED.attributes;
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, vehicle.getId());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setString(5, vehicle.getPlate());
            pstmt.setDouble(6, vehicle.getPrice());
            pstmt.setString(7, vehicle.getCategory());
            pstmt.setString(8, gson.toJson(vehicle.getAttributes()));

            pstmt.executeUpdate();
            return vehicle;
        } catch (SQLException e){
            throw new RuntimeException("Blad zapisu pojazdu: " + e.getMessage(), e);
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Blad usuwania pojazu", e);
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}
