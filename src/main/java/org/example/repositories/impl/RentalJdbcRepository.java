package org.example.repositories.impl;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {

    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RentalJdbcRepository(DataSource dataSource, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()) {
                rentals.add(Rental.builder()
                        .id(rs.getString("id"))
                        .vehicle(Vehicle.builder().id(rs.getString("vehicle_id")).build())
                        .user(User.builder().id(rs.getString("user_id")).build())
                        .rentDateTime(rs.getString("rent_date"))
                        .returnDateTime(rs.getString("return_date"))
                        .build());
            }
        }catch (SQLException e){
            throw new RuntimeException("Blad odczytu wypozyczen", e);
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
       String sql = "SELECT * FROM rental WHERE id = ?";

       Connection connection =DataSourceUtils.getConnection(dataSource);
       try(PreparedStatement pstmt = connection.prepareStatement(sql)){

           pstmt.setString(1,id);
           try(ResultSet rs = pstmt.executeQuery()){
               if(rs.next()){
                   return Optional.of(Rental.builder()
                           .id(rs.getString("id"))
                           .vehicle(Vehicle.builder().id(rs.getString("vehicle_id")).build())
                           .user(User.builder().id(rs.getString("user_id")).build())
                           .rentDateTime(rs.getString("rent_date"))
                           .returnDateTime(rs.getString("return_date"))
                           .build());
               }
           }
       }catch (SQLException e){
           throw new RuntimeException("Blad odczytu wypozyczenia po ID", e);
       } finally {
           DataSourceUtils.releaseConnection(connection, dataSource);
       }
       return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        String sql = """
                INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    return_date = EXCLUDED.return_date;
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, rental.getId());
            pstmt.setString(2, rental.getVehicleId());
            pstmt.setString(3, rental.getUserId());
            pstmt.setString(4, rental.getRentDateTime());
            pstmt.setString(5, rental.getReturnDateTime());

            pstmt.executeUpdate();
            return rental;
        }catch (SQLException e){
            throw new RuntimeException("Blad zapisu wypozyczenia: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("blad usuwania wypozyczenia", e);
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL LIMIT 1";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, vehicleId);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return Optional.of(Rental.builder()
                            .id(rs.getString("id"))
                            .vehicle(Vehicle.builder().id(rs.getString("vehicle_id")).build())
                            .user(User.builder().id(rs.getString("user_id")).build())
                            .rentDateTime(rs.getString("rent_date"))
                            .returnDateTime(rs.getString("return_date"))
                            .build());
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Blad odczytu dla danego pojazdu", e);
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }
}
