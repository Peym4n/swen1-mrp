package at.fhtw.swen1.mrp.repository;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.model.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for favorites.
 */
public final class FavoriteRepository {
    /** The database manager. */
    private final DatabaseManager databaseManager;

    /**
     * Constructor.
     */
    public FavoriteRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Adds a favorite.
     *
     * @param userId the user ID
     * @param mediaId the media ID
     */
    // CHECKSTYLE:OFF: MagicNumber
    public void add(final int userId, final int mediaId) {
        String sql = "INSERT INTO favorites (user_id, media_id, created_at) "
                + "VALUES (?, ?, NOW()) ON CONFLICT DO NOTHING";
        Connection conn = databaseManager.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add favorite", e);
        }
    }
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Removes a favorite.
     *
     * @param userId the user ID
     * @param mediaId the media ID
     */
    // CHECKSTYLE:OFF: MagicNumber
    public void remove(final int userId, final int mediaId) {
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Checks if is favorite.
     *
     * @param userId the user ID
     * @param mediaId the media ID
     * @return true if favorite
     */
    // CHECKSTYLE:OFF: MagicNumber
    public boolean isFavorite(final int userId, final int mediaId) {
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Finds favorites by user ID.
     *
     * @param userId the user ID
     * @return list of favorites
     */
    // CHECKSTYLE:OFF: MagicNumber
    public List<Media> findByUserId(final int userId) {
        List<Media> mediaList = new ArrayList<>();
        // Join media + media_genres via favorites
        // Note: aggregation for genres
        String sql = "SELECT m.*, string_agg(mg.genre, ',') as genres "
                     + "FROM favorites f "
                     + "JOIN media m ON f.media_id = m.id "
                     + "LEFT JOIN media_genres mg ON m.id = mg.media_id "
                     + "WHERE f.user_id = ? "
                     + "GROUP BY m.id";

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
    // CHECKSTYLE:ON: MagicNumber
    private Media mapResultSetToMedia(final ResultSet rs) throws SQLException {
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
