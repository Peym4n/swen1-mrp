package at.fhtw.swen1.mrp.model;

import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String favoriteGenre;
    private LocalDateTime createdAt;

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.favoriteGenre = builder.favoriteGenre;
        this.createdAt = builder.createdAt;
    }

    // Getters
    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFavoriteGenre() { return favoriteGenre; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters (for non-builder updates)
    public void setId(Integer id) { this.id = id; }
    public void setPassword(String password) { this.password = password; }

    public static class Builder {
        private Integer id;
        private String username;
        private String password;
        private String email;
        private String favoriteGenre;
        private LocalDateTime createdAt;

        public Builder() {}

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder favoriteGenre(String favoriteGenre) {
            this.favoriteGenre = favoriteGenre;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
