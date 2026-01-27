package at.fhtw.swen1.mrp.dto;

/**
 * Data Transfer Object for Leaderboard entries.
 */
public final class LeaderboardEntryDTO {
    /** The username of the user. */
    private String username;
    /** The number of ratings the user has given. */
    private int ratingCount;

    /**
     * Default constructor.
     */
    public LeaderboardEntryDTO() {
    }

    /**
     * Constructor with all fields.
     *
     * @param usernameVal the username
     * @param ratingCountVal the rating count
     */
    public LeaderboardEntryDTO(final String usernameVal, final int ratingCountVal) {
        this.username = usernameVal;
        this.ratingCount = ratingCountVal;
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
     * Gets the rating count.
     *
     * @return the rating count
     */
    public int getRatingCount() {
        return ratingCount;
    }

    /**
     * Sets the rating count.
     *
     * @param ratingCountVal the rating count to set
     */
    public void setRatingCount(final int ratingCountVal) {
        this.ratingCount = ratingCountVal;
    }
}
