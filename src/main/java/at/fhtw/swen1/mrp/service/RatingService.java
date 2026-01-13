package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.repository.RatingRepository;

import java.time.LocalDateTime;

public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating rateMedia(int userId, int mediaId, int stars, String comment) {
        // Validation
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }

        // Check if user already rated this media
        if (ratingRepository.hasUserRatedMedia(userId, mediaId)) {
            throw new IllegalStateException("User has already rated this media");
        }

        Rating rating = new Rating.Builder()
                .userId(userId)
                .mediaId(mediaId)
                .stars(stars)
                .comment(comment)
                .isConfirmed(false)
                .createdAt(LocalDateTime.now())
                .build();

        ratingRepository.save(rating);
        return rating;
    }
    
    public void confirmRating(int ratingId) {
        // Could add check if admin here, but for now just business logic
        if (ratingRepository.findById(ratingId).isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        ratingRepository.confirmRating(ratingId);
    }
    
    public void likeRating(int userId, int ratingId) {
        // Ensure rating exists
        if (ratingRepository.findById(ratingId).isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        ratingRepository.addLike(userId, ratingId);
    }
}
