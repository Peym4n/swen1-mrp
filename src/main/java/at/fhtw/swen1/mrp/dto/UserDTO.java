package at.fhtw.swen1.mrp.dto;

/**
 * Data Transfer Object for User registration/login.
 */
public final class UserDTO {
    /** The username. */
    private String username;
    /** The password. */
    private String password;
    /** The email address. */
    private String email;
    /** The user's favorite genre. */
    private String favoriteGenre;

    /**
     * Default constructor.
     */
    public UserDTO() {
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
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param passwordVal the password to set
     */
    public void setPassword(final String passwordVal) {
        this.password = passwordVal;
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
}
