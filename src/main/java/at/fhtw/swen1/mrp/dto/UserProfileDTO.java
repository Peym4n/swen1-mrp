package at.fhtw.swen1.mrp.dto;

/**
 * Data Transfer Object for User Profile.
 */
public final class UserProfileDTO {
    /** The user ID. */
    private int id;
    /** The username. */
    private String username;
    /** The email address. */
    private String email;
    /** The favorite genre. */
    private String favoriteGenre;
    /** Total number of ratings given. */
    private int totalRatings;
    /** Average rating score given. */
    private double averageScore;

    /**
     * Default constructor.
     */
    public UserProfileDTO() {
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param idVal the user ID to set
     */
    public void setId(final int idVal) {
        this.id = idVal;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param usernameVal the username to set
     */
    public void setUsername(final String usernameVal) {
        this.username = usernameVal;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param emailVal the email to set
     */
    public void setEmail(final String emailVal) {
        this.email = emailVal;
    }

    /**
     * Gets the favorite genre.
     *
     * @return the favorite genre
     */
    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    /**
     * Sets the favorite genre.
     *
     * @param favoriteGenreVal the favorite genre to set
     */
    public void setFavoriteGenre(final String favoriteGenreVal) {
        this.favoriteGenre = favoriteGenreVal;
    }

    /**
     * Gets the total ratings.
     *
     * @return the total ratings
     */
    public int getTotalRatings() {
        return totalRatings;
    }

    /**
     * Sets the total ratings.
     *
     * @param totalRatingsVal the total ratings to set
     */
    public void setTotalRatings(final int totalRatingsVal) {
        this.totalRatings = totalRatingsVal;
    }

    /**
     * Gets the average score.
     *
     * @return the average score
     */
    public double getAverageScore() {
        return averageScore;
    }

    /**
     * Sets the average score.
     *
     * @param averageScoreVal the average score to set
     */
    public void setAverageScore(final double averageScoreVal) {
        this.averageScore = averageScoreVal;
    }
}
