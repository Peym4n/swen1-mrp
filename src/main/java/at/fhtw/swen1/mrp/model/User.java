package at.fhtw.swen1.mrp.model;

import java.time.LocalDateTime;

/**
 * User model class.
 */
public final class User {
    /** The ID of the user. */
    private Integer id;
    /** The username. */
    private String username;
    /** The password. */
    private String password;
    /** The email. */
    private String email;
    /** The favorite genre. */
    private String favoriteGenre;
    /** The auth token. */
    private String token;
    /** The creation timestamp. */
    private LocalDateTime createdAt;

    /**
     * Constructor using builder.
     *
     * @param builder the builder
     */
    private User(final Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.favoriteGenre = builder.favoriteGenre;
        this.token = builder.token;
        this.createdAt = builder.createdAt;
    }

    // Getters
    // Getters
    /**
     * Gets the ID.
     *
     * @return the ID
     */
    public Integer getId() {
        return id;
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
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
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
     * Gets the favorite genre.
     *
     * @return the favorite genre
     */
    public String getFavoriteGenre() {
        return favoriteGenre;
    }
    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }
    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters (for non-builder updates)
    /**
     * Sets the ID.
     *
     * @param idArg the ID
     */
    public void setId(final Integer idArg) {
        this.id = idArg;
    }

    /**
     * Sets the password.
     *
     * @param passwordArg the password
     */
    public void setPassword(final String passwordArg) {
        this.password = passwordArg;
    }

    /**
     * Sets the token.
     *
     * @param tokenArg the token
     */
    public void setToken(final String tokenArg) {
        this.token = tokenArg;
    }

    /**
     * Builder for User.
     */
    public static final class Builder {
        /** The ID. */
        private Integer id;
        /** The username. */
        private String username;
        /** The password. */
        private String password;
        /** The email. */
        private String email;
        /** The favorite genre. */
        private String favoriteGenre;
        /** The token. */
        private String token;
        /** The creation timestamp. */
        private LocalDateTime createdAt;

        /** Default constructor. */
        public Builder() {
        }

        /**
         * Sets the ID.
         *
         * @param idArg the ID
         * @return the builder
         */
        public Builder id(final Integer idArg) {
            this.id = idArg;
            return this;
        }

        /**
         * Sets the username.
         *
         * @param usernameArg the username
         * @return the builder
         */
        public Builder username(final String usernameArg) {
            this.username = usernameArg;
            return this;
        }

        /**
         * Sets the password.
         *
         * @param passwordArg the password
         * @return the builder
         */
        public Builder password(final String passwordArg) {
            this.password = passwordArg;
            return this;
        }

        /**
         * Sets the email.
         *
         * @param emailArg the email
         * @return the builder
         */
        public Builder email(final String emailArg) {
            this.email = emailArg;
            return this;
        }

        /**
         * Sets the favorite genre.
         *
         * @param favoriteGenreArg the favorite genre
         * @return the builder
         */
        public Builder favoriteGenre(final String favoriteGenreArg) {
            this.favoriteGenre = favoriteGenreArg;
            return this;
        }

        /**
         * Sets the token.
         *
         * @param tokenArg the token
         * @return the builder
         */
        public Builder token(final String tokenArg) {
            this.token = tokenArg;
            return this;
        }

        /**
         * Sets the creation timestamp.
         *
         * @param createdAtArg the creation timestamp
         * @return the builder
         */
        public Builder createdAt(final LocalDateTime createdAtArg) {
            this.createdAt = createdAtArg;
            return this;
        }

        /**
         * Builds the User object.
         *
         * @return the User object
         */
        public User build() {
            return new User(this);
        }
    }
}
