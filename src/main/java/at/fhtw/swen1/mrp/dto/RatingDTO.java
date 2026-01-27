package at.fhtw.swen1.mrp.dto;

/**
 * Data Transfer Object for Rating.
 */
public final class RatingDTO {
    /** The number of stars (1-5). */
    private int stars;
    /** The comment accompanying the rating. */
    private String comment;

    /**
     * Gets the stars.
     *
     * @return the stars
     */
    public int getStars() {
        return stars;
    }

    /**
     * Sets the stars.
     *
     * @param starsVal the stars to set
     */
    public void setStars(final int starsVal) {
        this.stars = starsVal;
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
     * Sets the comment.
     *
     * @param commentVal the comment to set
     */
    public void setComment(final String commentVal) {
        this.comment = commentVal;
    }
}
