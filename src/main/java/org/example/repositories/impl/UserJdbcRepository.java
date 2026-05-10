package org.example.repositories.impl;

import org.example.db.JdbcConnectionManager;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJdbcRepository implements UserRepository{


    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = JdbcConnectionManager.getInstance().getConection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                users.add(User.builder()
                        .id(rs.getString("id"))
                        .login(rs.getString("login"))
                        .passwordHash(rs.getString("password"))
                        .role(Role.valueOf(rs.getString("role")))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, id);
            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return Optional.of(User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build());
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users (id, login, password, role)
                VALUES(?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    login = EXCLUDED.login,
                    password = EXCLUDED.password,
                    role = EXCLUDED.role;
                """;

        try (Connection conn = JdbcConnectionManager.getInstance().getConection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getLogin());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getRole().name());

            pstmt.executeLargeUpdate();
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Błąd zapisu użytkownika JDB: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
