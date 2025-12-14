package at.fhtw.swen1.mrp.model;

import java.time.LocalDateTime;

public class Rating {
    private Integer id;
    private Integer userId;
    private Integer mediaId;
    private Integer stars;
    private String comment;
    private Boolean isConfirmed;
    private LocalDateTime createdAt;

    private Rating(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.mediaId = builder.mediaId;
        this.stars = builder.stars;
        this.comment = builder.comment;
        this.isConfirmed = builder.isConfirmed;
        this.createdAt = builder.createdAt;
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getMediaId() { return mediaId; }
    public Integer getStars() { return stars; }
    public String getComment() { return comment; }
    public Boolean getIsConfirmed() { return isConfirmed; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static class Builder {
        private Integer id;
        private Integer userId;
        private Integer mediaId;
        private Integer stars;
        private String comment;
        private Boolean isConfirmed;
        private LocalDateTime createdAt;

        public Builder() {}

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder userId(Integer userId) { this.userId = userId; return this; }
        public Builder mediaId(Integer mediaId) { this.mediaId = mediaId; return this; }
        public Builder stars(Integer stars) { this.stars = stars; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }
        public Builder isConfirmed(Boolean isConfirmed) { this.isConfirmed = isConfirmed; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Rating build() {
            return new Rating(this);
        }
    }
}
