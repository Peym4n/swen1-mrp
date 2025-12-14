package at.fhtw.swen1.mrp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Media {
    private Integer id;
    private Integer creatorId;
    private String title;
    private String description;
    private String mediaType;
    private Integer releaseYear;
    private Integer ageRestriction;
    private Double averageRating;
    private LocalDateTime createdAt;
    private List<String> genres;

    private Media(Builder builder) {
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
    public Integer getId() { return id; }
    public Integer getCreatorId() { return creatorId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getMediaType() { return mediaType; }
    public Integer getReleaseYear() { return releaseYear; }
    public Integer getAgeRestriction() { return ageRestriction; }
    public Double getAverageRating() { return averageRating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<String> getGenres() { return genres; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public static class Builder {
        private Integer id;
        private Integer creatorId;
        private String title;
        private String description;
        private String mediaType;
        private Integer releaseYear;
        private Integer ageRestriction;
        private Double averageRating;
        private LocalDateTime createdAt;
        private List<String> genres = new ArrayList<>();

        public Builder() {}

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder creatorId(Integer creatorId) { this.creatorId = creatorId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder mediaType(String mediaType) { this.mediaType = mediaType; return this; }
        public Builder releaseYear(Integer releaseYear) { this.releaseYear = releaseYear; return this; }
        public Builder ageRestriction(Integer ageRestriction) { this.ageRestriction = ageRestriction; return this; }
        public Builder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder genres(List<String> genres) { this.genres = genres; return this; }

        public Media build() {
            return new Media(this);
        }
    }
}
