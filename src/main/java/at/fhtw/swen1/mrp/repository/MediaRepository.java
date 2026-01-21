package at.fhtw.swen1.mrp.repository;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.model.Media;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaRepository {
    private final DatabaseManager databaseManager;

    public MediaRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void save(Media media) {
        String sql = "INSERT INTO media (title, description, media_type, release_year, age_restriction, creator_id, average_rating) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setInt(6, media.getCreatorId());
            stmt.setDouble(7, media.getAverageRating() != null ? media.getAverageRating() : 0.0);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    media.setId(generatedKeys.getInt(1));
                }
            }
            
            // Insert genres
            if (media.getGenres() != null && !media.getGenres().isEmpty()) {
                String genreSql = "INSERT INTO media_genres (media_id, genre) VALUES (?, ?)";
                try (PreparedStatement genreStmt = conn.prepareStatement(genreSql)) {
                    for (String genre : media.getGenres()) {
                        genreStmt.setInt(1, media.getId());
                        genreStmt.setString(2, genre);
                        genreStmt.addBatch();
                    }
                    genreStmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save media", e);
        }
    }

    public List<Media> findAll(String title, String mediaType, Integer releaseYear, Integer ageRestriction, String genre, Double minRating) {
        List<Media> mediaList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT m.*, string_agg(mg.genre, ',') as genres FROM media m LEFT JOIN media_genres mg ON m.id = mg.media_id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            sql.append(" AND LOWER(m.title) LIKE ?");
            params.add("%" + title.toLowerCase() + "%");
        }
        if (mediaType != null && !mediaType.isEmpty()) {
            sql.append(" AND m.media_type = ?");
            params.add(mediaType);
        }
        if (releaseYear != null) {
            sql.append(" AND m.release_year = ?");
            params.add(releaseYear);
        }
        if (ageRestriction != null) {
            sql.append(" AND m.age_restriction = ?");
            params.add(ageRestriction);
        }
        if (minRating != null) {
            sql.append(" AND m.average_rating >= ?");
            params.add(minRating);
        }
        if (genre != null && !genre.isEmpty()) {
            // Filter by genre using subquery to avoid messing up the main join
            sql.append(" AND m.id IN (SELECT media_id FROM media_genres WHERE genre = ?)");
            params.add(genre);
        }
        
        sql.append(" GROUP BY m.id");

        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find media", e);
        }
        return mediaList;
    }

    public Optional<Media> findById(int id) {
        String sql = "SELECT m.*, string_agg(mg.genre, ',') as genres FROM media m LEFT JOIN media_genres mg ON m.id = mg.media_id WHERE m.id = ? GROUP BY m.id";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find media by id", e);
        }
        return Optional.empty();
    }

    public List<Media> findRecommendations(int userId) {
        // Recommend media based on:
        // 1. User's favorite genre (fetched via subquery if possible, or just passed, but let's try SQL)
        // 2. Genres of media the user rated highly (>= 4 stars)
        // 3. Exclude media already rated by user
        // 4. Order by average_rating
        
        // Note: For pure JDBC, we can do this in one query or multiple.
        // Let's assume we can get favorite genre from users table.
        
        String sql = "SELECT m.*, string_agg(mg.genre, ',') as genres " +
                     "FROM media m " +
                     "JOIN media_genres mg ON m.id = mg.media_id " +
                     "WHERE m.id NOT IN (SELECT media_id FROM ratings WHERE user_id = ?) " +
                     "AND ( " +
                     "   mg.genre = (SELECT favorite_genre FROM users WHERE id = ?) " +
                     "   OR mg.genre IN ( " +
                     "       SELECT DISTINCT mg2.genre " +
                     "       FROM media_genres mg2 " +
                     "       JOIN ratings r ON mg2.media_id = r.media_id " +
                     "       WHERE r.user_id = ? AND r.stars >= 4 " +
                     "   ) " +
                     ") " +
                     "GROUP BY m.id, m.average_rating " +
                     "ORDER BY m.average_rating DESC " +
                     "LIMIT 10";

        List<Media> mediaList = new ArrayList<>();
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get recommendations", e);
        }
        return mediaList;
    }

    public void update(Media media) {
        String sql = "UPDATE media SET title = ?, description = ?, media_type = ?, release_year = ?, age_restriction = ? WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setInt(6, media.getId());
            
            stmt.executeUpdate();
            
            // Update genres: Delete all and re-insert
            if (media.getGenres() != null) {
                String deleteGenresSql = "DELETE FROM media_genres WHERE media_id = ?";
                try (PreparedStatement delStmt = conn.prepareStatement(deleteGenresSql)) {
                    delStmt.setInt(1, media.getId());
                    delStmt.executeUpdate();
                }
                
                if (!media.getGenres().isEmpty()) {
                    String insertGenreSql = "INSERT INTO media_genres (media_id, genre) VALUES (?, ?)";
                    try (PreparedStatement insStmt = conn.prepareStatement(insertGenreSql)) {
                        for (String genre : media.getGenres()) {
                            insStmt.setInt(1, media.getId());
                            insStmt.setString(2, genre);
                            insStmt.addBatch();
                        }
                        insStmt.executeBatch();
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update media", e);
        }
    }

    public void updateAverageRating(int mediaId, double averageRating) {
        String sql = "UPDATE media SET average_rating = ? WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, averageRating);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update average rating", e);
        }
    }

    public void delete(int id) {
        // ON DELETE CASCADE is set in DB schema for media_genres
        String sql = "DELETE FROM media WHERE id = ?";
        Connection conn = databaseManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete media", e);
        }
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
