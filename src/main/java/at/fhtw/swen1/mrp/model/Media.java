package at.fhtw.swen1.mrp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Media model class.
 */
public final class Media {
    /** The ID of the media. */
    private Integer id;
    /** The ID of the creator. */
    private final Integer creatorId;
    /** The title of the media. */
    private final String title;
    /** The description of the media. */
    private final String description;
    /** The type of the media (e.g., Movie, Song). */
    private final String mediaType;
    /** The release year. */
    private final Integer releaseYear;
    /** The age restriction. */
    private final Integer ageRestriction;
    /** The average rating. */
    private Double averageRating;
    /** The creation timestamp. */
    private final LocalDateTime createdAt;
    /** The genres of the media. */
    private final List<String> genres;

    /**
     * Constructor using builder.
     *
     * @param builder the builder
     */
    private Media(final Builder builder) {
        this.id = builder.id;
        this.creatorId = builder.creatorId;
        this.title = builder.title;
        this.description = builder.description;
        this.mediaType = builder.mediaType;
        this.releaseYear = builder.releaseYear;
        this.ageRestriction = builder.ageRestriction;
        this.averageRating = builder.averageRating;
        this.createdAt = builder.createdAt;
        this.genres = builder.genres;
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
     * Gets the creator ID.
     *
     * @return the creator ID
     */
    public Integer getCreatorId() {
        return creatorId;
    }
    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * Gets the media type.
     *
     * @return the media type
     */
    public String getMediaType() {
        return mediaType;
    }
    /**
     * Gets the release year.
     *
     * @return the release year
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }
    /**
     * Gets the age restriction.
     *
     * @return the age restriction
     */
    public Integer getAgeRestriction() {
        return ageRestriction;
    }
    /**
     * Gets the average rating.
     *
     * @return the average rating
     */
    public Double getAverageRating() {
        return averageRating;
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
     * Gets the genres.
     *
     * @return the genres
     */
    public List<String> getGenres() {
        return genres;
    }

    // Setters
    /**
     * Sets the ID.
     *
     * @param idArg the ID
     */
    public void setId(final Integer idArg) {
        this.id = idArg;
    }

    /**
     * Sets the average rating.
     *
     * @param averageRatingArg the average rating
     */
    public void setAverageRating(final Double averageRatingArg) {
        this.averageRating = averageRatingArg;
    }

    /**
     * Builder for Media.
     */
    public static final class Builder {
        /** The ID. */
        private Integer id;
        /** The creator ID. */
        private Integer creatorId;
        /** The title. */
        private String title;
        /** The description. */
        private String description;
        /** The media type. */
        private String mediaType;
        /** The release year. */
        private Integer releaseYear;
        /** The age restriction. */
        private Integer ageRestriction;
        /** The average rating. */
        private Double averageRating;
        /** The creation timestamp. */
        private LocalDateTime createdAt;
        /** The genres. */
        private List<String> genres = new ArrayList<>();

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
         * Sets the creator ID.
         *
         * @param creatorIdArg the creator ID
         * @return the builder
         */
        public Builder creatorId(final Integer creatorIdArg) {
            this.creatorId = creatorIdArg;
            return this;
        }

        /**
         * Sets the title.
         *
         * @param titleArg the title
         * @return the builder
         */
        public Builder title(final String titleArg) {
            this.title = titleArg;
            return this;
        }

        /**
         * Sets the description.
         *
         * @param descriptionArg the description
         * @return the builder
         */
        public Builder description(final String descriptionArg) {
            this.description = descriptionArg;
            return this;
        }

        /**
         * Sets the media type.
         *
         * @param mediaTypeArg the media type
         * @return the builder
         */
        public Builder mediaType(final String mediaTypeArg) {
            this.mediaType = mediaTypeArg;
            return this;
        }

        /**
         * Sets the release year.
         *
         * @param releaseYearArg the release year
         * @return the builder
         */
        public Builder releaseYear(final Integer releaseYearArg) {
            this.releaseYear = releaseYearArg;
            return this;
        }

        /**
         * Sets the age restriction.
         *
         * @param ageRestrictionArg the age restriction
         * @return the builder
         */
        public Builder ageRestriction(final Integer ageRestrictionArg) {
            this.ageRestriction = ageRestrictionArg;
            return this;
        }

        /**
         * Sets the average rating.
         *
         * @param averageRatingArg the average rating
         * @return the builder
         */
        public Builder averageRating(final Double averageRatingArg) {
            this.averageRating = averageRatingArg;
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
         * Sets the genres.
         *
         * @param genresArg the genres
         * @return the builder
         */
        public Builder genres(final List<String> genresArg) {
            this.genres = genresArg;
            return this;
        }

        /**
         * Builds the Media object.
         *
         * @return the Media object
         */
        public Media build() {
            return new Media(this);
        }
    }
}
