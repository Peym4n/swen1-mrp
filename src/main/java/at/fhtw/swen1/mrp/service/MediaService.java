package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.data.DatabaseConnection;
import at.fhtw.swen1.mrp.model.Media;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaService {

    public Media createMedia(Media media) {
        String sql = "INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, rating) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getReleaseYear());

            // Handle List<String> to SQL Array
            Array genresArray = conn.createArrayOf("text", media.getGenres().toArray());
            stmt.setArray(5, genresArray);

            stmt.setInt(6, media.getAgeRestriction());
            if (media.getRating() != null) {
                stmt.setDouble(7, media.getRating());
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating media failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    media.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating media failed, no ID obtained.");
                }
            }
            return media;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error", e);
        }
    }

    public List<Media> getMedia(String title, String genre, String mediaType, Integer releaseYear, Integer ageRestriction, Double rating, String sortBy) {
        List<Media> mediaList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM media WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + title.toLowerCase() + "%");
        }
        if (genre != null) {
            sql.append(" AND ? = ANY(genres)");
            params.add(genre);
        }
        if (mediaType != null) {
            sql.append(" AND media_type = ?");
            params.add(mediaType);
        }
        if (releaseYear != null) {
            sql.append(" AND release_year = ?");
            params.add(releaseYear);
        }
        if (ageRestriction != null) {
            sql.append(" AND age_restriction = ?");
            params.add(ageRestriction);
        }
        if (rating != null) {
            sql.append(" AND rating = ?");
            params.add(rating);
        }

        if (sortBy != null) {
            switch (sortBy) {
                case "title": sql.append(" ORDER BY title"); break;
                case "year": sql.append(" ORDER BY release_year"); break;
                case "score": sql.append(" ORDER BY rating DESC"); break;
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Media media = new Media();
                    media.setId(rs.getInt("id"));
                    media.setTitle(rs.getString("title"));
                    media.setDescription(rs.getString("description"));
                    media.setMediaType(rs.getString("media_type"));
                    media.setReleaseYear(rs.getInt("release_year"));

                    Array genresArray = rs.getArray("genres");
                    if (genresArray != null) {
                        String[] genres = (String[]) genresArray.getArray();
                        media.setGenres(Arrays.asList(genres));
                    }

                    media.setAgeRestriction(rs.getInt("age_restriction"));
                    media.setRating(rs.getObject("rating") != null ? rs.getDouble("rating") : null);
                    mediaList.add(media);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error", e);
        }
        return mediaList;
    }
}
