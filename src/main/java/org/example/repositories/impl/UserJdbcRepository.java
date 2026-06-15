package org.example.repositories.impl;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class UserJdbcRepository implements UserRepository{

    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){
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
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
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
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }


    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){

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
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
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

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getLogin());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getRole().name());

            pstmt.executeLargeUpdate();
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Błąd zapisu użytkownika JDB: " + e.getMessage());
        }finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
