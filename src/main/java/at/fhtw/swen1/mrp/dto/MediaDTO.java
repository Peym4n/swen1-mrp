package at.fhtw.swen1.mrp.dto;

import java.util.List;

/**
 * Data Transfer Object for Media.
 */
public final class MediaDTO {
    /** The title of the media. */
    private String title;
    /** The description of the media. */
    private String description;
    /** The type of the media (e.g., Movie, Series). */
    private String mediaType;
    /** The release year of the media. */
    private Integer releaseYear;
    /** The age restriction of the media. */
    private Integer ageRestriction;
    /** The list of genres associated with the media. */
    private List<String> genres;

    /**
     * Default constructor.
     */
    public MediaDTO() {
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
     * Sets the title.
     *
     * @param titleVal the title to set
     */
    public void setTitle(final String titleVal) {
        this.title = titleVal;
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
     * Sets the description.
     *
     * @param descriptionVal the description to set
     */
    public void setDescription(final String descriptionVal) {
        this.description = descriptionVal;
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
     * Sets the media type.
     *
     * @param mediaTypeVal the media type to set
     */
    public void setMediaType(final String mediaTypeVal) {
        this.mediaType = mediaTypeVal;
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
     * Sets the release year.
     *
     * @param releaseYearVal the release year to set
     */
    public void setReleaseYear(final Integer releaseYearVal) {
        this.releaseYear = releaseYearVal;
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
     * Sets the age restriction.
     *
     * @param ageRestrictionVal the age restriction to set
     */
    public void setAgeRestriction(final Integer ageRestrictionVal) {
        this.ageRestriction = ageRestrictionVal;
    }

    /**
     * Gets the genres.
     *
     * @return the genres
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * Sets the genres.
     *
     * @param genresVal the genres to set
     */
    public void setGenres(final List<String> genresVal) {
        this.genres = genresVal;
    }
}
