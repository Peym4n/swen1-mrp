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

/**
 * Repository for ratings.
 */
public final class RatingRepository {
    /** The database manager. */
    private final DatabaseManager databaseManager;

    /**
     * Constructor.
     */

    public RatingRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Calculates average rating.
     *
     * @param mediaId the media ID
     * @return the average rating
     */
    // CHECKSTYLE:OFF: MagicNumber
    public double calculateAverageRating(final int mediaId) {
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Gets most active users.
     *
     * @return the leaderboard
     */
    public List<LeaderboardEntryDTO> getMostActiveUsers() {
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        String sql = "SELECT u.username, COUNT(r.id) as count FROM ratings r"
               + " JOIN users u ON r.user_id = u.id GROUP BY u.username ORDER BY count DESC LIMIT 10";

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

    /**
     * Saves a rating.
     *
     * @param rating the rating
     * @return the ID
     */
    // CHECKSTYLE:OFF: MagicNumber
    public int save(final Rating rating) {
        String sql = "INSERT INTO ratings (user_id, media_id, stars, comment, is_confirmed)"
               + " VALUES (?, ?, ?, ?, ?)";
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Finds ratings by media ID.
     *
     * @param mediaId the media ID
     * @return list of ratings
     */
    // CHECKSTYLE:OFF: MagicNumber
    public List<Rating> findByMediaId(final int mediaId) {
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

    /**
     * Finds ratings by user ID.
     *
     * @param userId the user ID
     * @return list of ratings
     */
    // CHECKSTYLE:OFF: MagicNumber
    public List<Rating> findByUserId(final int userId) {
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

    /**
     * Updates a rating.
     *
     * @param rating the rating
     */
    // CHECKSTYLE:OFF: MagicNumber
    public void update(final Rating rating) {
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Deletes a rating.
     *
     * @param ratingId the rating ID
     */
    // CHECKSTYLE:OFF: MagicNumber
    public void delete(final int ratingId) {
        String sql = "DELETE FROM ratings WHERE id = ?";
        Connection conn = databaseManager.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete rating", e);
        }
    }

    /**
     * Checks if user has rated media.
     *
     * @param userId the user ID
     * @param mediaId the media ID
     * @return true if rated
     */
    // CHECKSTYLE:OFF: MagicNumber
    public boolean hasUserRatedMedia(final int userId, final int mediaId) {
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Finds rating by ID.
     *
     * @param id the ID
     * @return the rating
     */
    // CHECKSTYLE:OFF: MagicNumber
    public Optional<Rating> findById(final int id) {
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Confirms a rating.
     *
     * @param ratingId the rating ID
     */
    // CHECKSTYLE:OFF: MagicNumber
    public void confirmRating(final int ratingId) {
        String sql = "UPDATE ratings SET is_confirmed = TRUE WHERE id = ?";
        Connection conn = databaseManager.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to confirm rating", e);
        }
    }

    /**
     * Adds a like to a rating.
     *
     * @param userId the user ID
     * @param ratingId the rating ID
     */
    // CHECKSTYLE:OFF: MagicNumber
    public void addLike(final int userId, final int ratingId) {
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Maps result set to Rating.
     *
     * @param rs the result set
     * @return the rating
     * @throws SQLException if SQL error occurs
     */
    private Rating mapResultSetToRating(final ResultSet rs) throws SQLException {
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
