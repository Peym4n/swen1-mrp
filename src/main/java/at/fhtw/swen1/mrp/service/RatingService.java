package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.repository.RatingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing ratings.
 */
public final class RatingService {
    /** The rating repository. */
    private final RatingRepository ratingRepository;
    /** The media repository. */
    private final MediaRepository mediaRepository;

    /**
     * Constructor.
     *
     * @param ratingRepositoryArg the rating repository
     * @param mediaRepositoryArg the media repository
     */
    public RatingService(final RatingRepository ratingRepositoryArg,
                         final MediaRepository mediaRepositoryArg) {
        this.ratingRepository = ratingRepositoryArg;
        this.mediaRepository = mediaRepositoryArg;
    }

    /**
     * Adds a rating.
     *
     * @param userId the user ID
     * @param mediaId the media ID
     * @param stars the stars
     * @param comment the comment
     * @return the created rating
     */
    // CHECKSTYLE:OFF: MagicNumber
    public Rating rateMedia(final int userId, final int mediaId,
                            final int stars, final String comment) {
        // Validation
        final int maxStars = 5;
        if (stars < 1 || stars > maxStars) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }

        if (ratingRepository.hasUserRatedMedia(userId, mediaId)) {
            throw new IllegalStateException(
                    "User has already rated this media");
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
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Confirms a rating.
     *
     * @param ratingId the rating ID
     * @param userId the user ID confirming (must be media creator)
     */
    public void confirmRating(final int ratingId, final int userId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }

        Rating rating = ratingOpt.get();
        Optional<Media> mediaOpt =
                mediaRepository.findById(rating.getMediaId());

        if (mediaOpt.isEmpty()) {
            throw new IllegalArgumentException("Associated media not found");
        }

        Media media = mediaOpt.get();
        if (media.getCreatorId() != userId) {
            throw new IllegalArgumentException(
                    "User is not the owner of this media");
        }

        ratingRepository.confirmRating(ratingId);
    }
    /**
     * Gets user ratings.
     *
     * @param userId the user ID
     * @return list of ratings
     */
    public List<Rating> getUserRatings(final int userId) {
        return ratingRepository.findByUserId(userId);
    }

    /**
     * Likes a rating.
     *
     * @param userId the user ID
     * @param ratingId the rating ID
     */
    public void likeRating(final int userId, final int ratingId) {
        if (ratingRepository.findById(ratingId).isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        ratingRepository.addLike(userId, ratingId);
    }

    /**
     * Updates a rating.
     *
     * @param userId the user ID
     * @param ratingId the rating ID
     * @param stars the stars
     * @param comment the comment
     * @return the updated rating
     */
    // CHECKSTYLE:OFF: MagicNumber
    public Rating updateRating(final int userId, final int ratingId,
                               final int stars, final String comment) {
        final int maxStars = 5;
        if (stars < 1 || stars > maxStars) {
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
        // Update Average
        double newAverage = ratingRepository
                .calculateAverageRating(rating.getMediaId());
        mediaRepository.updateAverageRating(rating.getMediaId(), newAverage);
        return updatedRating;
    }
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Gets the leaderboard.
     *
     * @return list of leaderboard entries
     */
    public List<at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO> getLeaderboard() {
        return ratingRepository.getMostActiveUsers();
    }

    /**
     * Deletes a rating.
     *
     * @param userId the user ID
     * @param ratingId the rating ID
     */
    public void deleteRating(final int userId, final int ratingId) {
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
