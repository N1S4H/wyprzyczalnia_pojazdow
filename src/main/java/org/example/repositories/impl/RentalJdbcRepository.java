package org.example.repositories.impl;

import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJdbcRepository implements RentalRepository {

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";

        try(Connection conn = JdbcConnectionManager.getInstance().getConection();
            Statement stmt = conn.createStatement();
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
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
       String sql = "SELECT * FROM rental WHERE id = ?";

       try(Connection conn = JdbcConnectionManager.getInstance().getConection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

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

        try(Connection conn = JdbcConnectionManager.getInstance().getConection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, rental.getId());
            pstmt.setString(2, rental.getVehicleId());
            pstmt.setString(3, rental.getUserId());
            pstmt.setString(4, rental.getRentDateTime());
            pstmt.setString(5, rental.getReturnDateTime());

            pstmt.executeUpdate();
            return rental;
        }catch (SQLException e){
            throw new RuntimeException("Blad zapisu wypozyczenia: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        try(Connection conn = JdbcConnectionManager.getInstance().getConection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("blad usuwania wypozyczenia", e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL LIMIT 1";

        try(Connection conn = JdbcConnectionManager.getInstance().getConection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

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
        }
        return Optional.empty();
    }
}
