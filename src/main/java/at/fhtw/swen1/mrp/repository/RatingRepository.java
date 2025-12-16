package at.fhtw.swen1.mrp.repository;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.model.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository {
    private final DatabaseManager databaseManager;

    public RatingRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void save(Rating rating) {
        String sql = "INSERT INTO ratings (user_id, media_id, stars, comment, is_confirmed) VALUES (?, ?, ?, ?, ?)";
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rating.getUserId());
            stmt.setInt(2, rating.getMediaId());
            stmt.setInt(3, rating.getStars());
            stmt.setString(4, rating.getComment());
            stmt.setBoolean(5, rating.getIsConfirmed() != null ? rating.getIsConfirmed() : false);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save rating", e);
        }
    }

    public List<Rating> findByMediaId(int mediaId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE media_id = ?";
        
        Connection conn = databaseManager.getConnection();
             
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, mediaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(new Rating.Builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getInt("user_id"))
                        .mediaId(rs.getInt("media_id"))
                        .stars(rs.getInt("stars"))
                        .comment(rs.getString("comment"))
                        .isConfirmed(rs.getBoolean("is_confirmed"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ratings", e);
        }
        return ratings;
    }
}
