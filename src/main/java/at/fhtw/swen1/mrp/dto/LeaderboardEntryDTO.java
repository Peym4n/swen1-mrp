package at.fhtw.swen1.mrp.dto;

public class LeaderboardEntryDTO {
    private String username;
    private int ratingCount;

    public LeaderboardEntryDTO() {}

    public LeaderboardEntryDTO(String username, int ratingCount) {
        this.username = username;
        this.ratingCount = ratingCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
