package at.fhtw.swen1.mrp.repository;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.model.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {
    private final DatabaseManager databaseManager;

    public FavoriteRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void add(int userId, int mediaId) {
        String sql = "INSERT INTO favorites (user_id, media_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add favorite", e);
        }
    }

    public void remove(int userId, int mediaId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND media_id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove favorite", e);
        }
    }

    public boolean isFavorite(int userId, int mediaId) {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND media_id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check favorite", e);
        }
    }

    public List<Media> findByUserId(int userId) {
        List<Media> mediaList = new ArrayList<>();
        // Join media + media_genres via favorites
        // Note: aggregation for genres
        String sql = "SELECT m.*, string_agg(mg.genre, ',') as genres " +
                     "FROM favorites f " +
                     "JOIN media m ON f.media_id = m.id " +
                     "LEFT JOIN media_genres mg ON m.id = mg.media_id " +
                     "WHERE f.user_id = ? " +
                     "GROUP BY m.id";
                     
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find favorites", e);
        }
        return mediaList;
    }
    
    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        String genresStr = rs.getString("genres");
        List<String> genres = new ArrayList<>();
        if (genresStr != null && !genresStr.isEmpty()) {
            for (String g : genresStr.split(",")) {
                genres.add(g);
            }
        }

        return new Media.Builder()
                .id(rs.getInt("id"))
                .creatorId(rs.getInt("creator_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .mediaType(rs.getString("media_type"))
                .releaseYear(rs.getInt("release_year"))
                .ageRestriction(rs.getInt("age_restriction"))
                .averageRating(rs.getDouble("average_rating"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .genres(genres)
                .build();
    }
}
