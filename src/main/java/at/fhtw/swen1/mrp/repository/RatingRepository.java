package at.fhtw.swen1.mrp.repository;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO;
import at.fhtw.swen1.mrp.model.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RatingRepository {
    private final DatabaseManager databaseManager;

    public RatingRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public double calculateAverageRating(int mediaId) {
        String sql = "SELECT AVG(stars) FROM ratings WHERE media_id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1); // Returns 0.0 if NULL
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate average rating", e);
        }
        return 0.0;
    }

    public List<at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO> getMostActiveUsers() {
        List<at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        String sql = "SELECT u.username, COUNT(r.id) as count FROM ratings r JOIN users u ON r.user_id = u.id GROUP BY u.username ORDER BY count DESC LIMIT 10";
        
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                leaderboard.add(new LeaderboardEntryDTO(
                    rs.getString("username"),
                    rs.getInt("count")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get leaderboard", e);
        }
        return leaderboard;
    }

    public int save(Rating rating) {
        String sql = "INSERT INTO ratings (user_id, media_id, stars, comment, is_confirmed) VALUES (?, ?, ?, ?, ?)";
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, rating.getUserId());
            stmt.setInt(2, rating.getMediaId());
            stmt.setInt(3, rating.getStars());
            stmt.setString(4, rating.getComment());
            stmt.setBoolean(5, rating.getIsConfirmed() != null ? rating.getIsConfirmed() : false);
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating rating failed, no ID obtained.");
                }
            }
            
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
                    ratings.add(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ratings", e);
        }
        return ratings;
    }

    public List<Rating> findByUserId(int userId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE user_id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user ratings", e);
        }
        return ratings;
    }

    public void update(Rating rating) {
        String sql = "UPDATE ratings SET stars = ?, comment = ? WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getStars());
            stmt.setString(2, rating.getComment());
            stmt.setInt(3, rating.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update rating", e);
        }
    }

    public void delete(int ratingId) {
        String sql = "DELETE FROM ratings WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete rating", e);
        }
    }

    public boolean hasUserRatedMedia(int userId, int mediaId) {
        String sql = "SELECT 1 FROM ratings WHERE user_id = ? AND media_id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existing rating", e);
        }
    }

    public Optional<Rating> findById(int id) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find rating", e);
        }
        return Optional.empty();
    }

    public void confirmRating(int ratingId) {
        String sql = "UPDATE ratings SET is_confirmed = TRUE WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to confirm rating", e);
        }
    }

    public void addLike(int userId, int ratingId) {
        String sql = "INSERT INTO rating_likes (user_id, rating_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to like rating", e);
        }
    }

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        return new Rating.Builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .mediaId(rs.getInt("media_id"))
            .stars(rs.getInt("stars"))
            .comment(rs.getString("comment"))
            .isConfirmed(rs.getBoolean("is_confirmed"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();
    }
}
