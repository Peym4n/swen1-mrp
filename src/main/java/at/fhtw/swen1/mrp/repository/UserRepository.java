package at.fhtw.swen1.mrp.repository;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {
    private final DatabaseManager databaseManager;

    public UserRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, password, email, favorite_genre) VALUES (?, ?, ?, ?)";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFavoriteGenre());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User.Builder()
                        .id(rs.getInt("id"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .email(rs.getString("email"))
                        .favoriteGenre(rs.getString("favorite_genre"))
                        .token(rs.getString("token"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user", e);
        }
        return Optional.empty();
    }

    public void updateToken(Integer userId, String token) {
        String sql = "UPDATE users SET token = ? WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update token", e);
        }
    }

    public Optional<User> findByToken(String token) {
        String sql = "SELECT * FROM users WHERE token = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User.Builder()
                        .id(rs.getInt("id"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .email(rs.getString("email"))
                        .favoriteGenre(rs.getString("favorite_genre"))
                        .token(rs.getString("token"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by token", e);
        }
        return Optional.empty();
    }
    public void update(User user) {
        String sql = "UPDATE users SET email = ?, favorite_genre = ? WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFavoriteGenre());
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }
}
