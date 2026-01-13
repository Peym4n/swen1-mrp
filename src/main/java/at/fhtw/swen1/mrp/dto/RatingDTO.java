package at.fhtw.swen1.mrp.dto;

public class RatingDTO {
    private int stars;
    private String comment;

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
