package at.fhtw.swen1.mrp.model;

import java.time.LocalDateTime;

/**
 * Rating model class.
 */
public final class Rating {
    /** The ID of the rating. */
    private final Integer id;
    /** The ID of the user. */
    private final Integer userId;
    /** The ID of the media. */
    private final Integer mediaId;
    /** The number of stars. */
    private final Integer stars;
    /** The comment. */
    private final String comment;
    /** Whether the rating is confirmed. */
    private final Boolean isConfirmed;
    /** The creation timestamp. */
    private final LocalDateTime createdAt;

    /**
     * Constructor using builder.
     *
     * @param builder the builder
     */
    private Rating(final Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.mediaId = builder.mediaId;
        this.stars = builder.stars;
        this.comment = builder.comment;
        this.isConfirmed = builder.isConfirmed;
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
     * Gets the user ID.
     *
     * @return the user ID
     */
    public Integer getUserId() {
        return userId;
    }
    /**
     * Gets the media ID.
     *
     * @return the media ID
     */
    public Integer getMediaId() {
        return mediaId;
    }
    /**
     * Gets the stars.
     *
     * @return the stars
     */
    public Integer getStars() {
        return stars;
    }
    /**
     * Gets the comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }
    /**
     * Gets whether is confirmed.
     *
     * @return true if confirmed
     */
    public Boolean getIsConfirmed() {
        return isConfirmed;
    }
    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Builder for Rating.
     */
    public static final class Builder {
        /** The ID. */
        private Integer id;
        /** The user ID. */
        private Integer userId;
        /** The media ID. */
        private Integer mediaId;
        /** The stars. */
        private Integer stars;
        /** The comment. */
        private String comment;
        /** Whether is confirmed. */
        private Boolean isConfirmed;
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
         * Sets the user ID.
         *
         * @param userIdArg the user ID
         * @return the builder
         */
        public Builder userId(final Integer userIdArg) {
            this.userId = userIdArg;
            return this;
        }

        /**
         * Sets the media ID.
         *
         * @param mediaIdArg the media ID
         * @return the builder
         */
        public Builder mediaId(final Integer mediaIdArg) {
            this.mediaId = mediaIdArg;
            return this;
        }

        /**
         * Sets the stars.
         *
         * @param starsArg the stars
         * @return the builder
         */
        public Builder stars(final Integer starsArg) {
            this.stars = starsArg;
            return this;
        }

        /**
         * Sets the comment.
         *
         * @param commentArg the comment
         * @return the builder
         */
        public Builder comment(final String commentArg) {
            this.comment = commentArg;
            return this;
        }

        /**
         * Sets isConfirmed.
         *
         * @param isConfirmedArg is confirmed
         * @return the builder
         */
        public Builder isConfirmed(final Boolean isConfirmedArg) {
            this.isConfirmed = isConfirmedArg;
            return this;
        }

        /**
         * Sets creation time.
         *
         * @param createdAtArg the creation time
         * @return the builder
         */
        public Builder createdAt(final LocalDateTime createdAtArg) {
            this.createdAt = createdAtArg;
            return this;
        }

        /**
         * Builds the Rating object.
         *
         * @return the Rating object
         */
        public Rating build() {
            return new Rating(this);
        }
    }
}
