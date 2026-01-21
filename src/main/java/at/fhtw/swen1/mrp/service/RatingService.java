package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.repository.RatingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RatingService {
    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository; // Full qualified to avoid collision if any loop, or just import

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
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

        int ratingId = ratingRepository.save(rating);
        
        // Rebuild with ID
        rating = new Rating.Builder()
                .id(ratingId)
                .userId(userId)
                .mediaId(mediaId)
                .stars(stars)
                .comment(comment)
                .isConfirmed(false)
                .createdAt(rating.getCreatedAt())
                .build();
        
        // Update Average
        double newAverage = ratingRepository.calculateAverageRating(mediaId);
        mediaRepository.updateAverageRating(mediaId, newAverage);
        
        return rating;
    }
    
    public void confirmRating(int ratingId) {
        // Could add check if admin here, but for now just business logic
        if (ratingRepository.findById(ratingId).isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        ratingRepository.confirmRating(ratingId);
    }
    
    public List<Rating> getUserRatings(int userId) {
        return ratingRepository.findByUserId(userId);
    }

    public void likeRating(int userId, int ratingId) {
        // Ensure rating exists
        if (ratingRepository.findById(ratingId).isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        ratingRepository.addLike(userId, ratingId);
    }

    public Rating updateRating(int userId, int ratingId, int stars, String comment) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }

        Optional<Rating> existing = ratingRepository.findById(ratingId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        
        Rating rating = existing.get();
        if (rating.getUserId() != userId) {
            throw new IllegalArgumentException("User does not own this rating");
        }

        // Create updated object
        Rating updatedRating = new Rating.Builder()
                .id(rating.getId())
                .userId(rating.getUserId())
                .mediaId(rating.getMediaId())
                .stars(stars)
                .comment(comment)
                .isConfirmed(rating.getIsConfirmed()) // Keep original status
                .createdAt(rating.getCreatedAt())
                .build();
        
        ratingRepository.update(updatedRating);
        
        // Update Average
        double newAverage = ratingRepository.calculateAverageRating(rating.getMediaId());
        mediaRepository.updateAverageRating(rating.getMediaId(), newAverage);
        
        return updatedRating;
    }

    public List<at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO> getLeaderboard() {
        return ratingRepository.getMostActiveUsers();
    }

    public void deleteRating(int userId, int ratingId) {
        Optional<Rating> existing = ratingRepository.findById(ratingId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        
        Rating rating = existing.get();
        if (rating.getUserId() != userId) {
            throw new IllegalArgumentException("User does not own this rating");
        }
        
        int mediaId = rating.getMediaId();
        ratingRepository.delete(ratingId);
        
        // Update Average
        double newAverage = ratingRepository.calculateAverageRating(mediaId);
        mediaRepository.updateAverageRating(mediaId, newAverage);
    }
}
