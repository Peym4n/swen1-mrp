package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.MediaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing media.
 */
public final class MediaService {
    /** The media repository. */
    private final MediaRepository mediaRepository;

    /**
     * Constructor.
     *
     * @param mediaRepositoryArg the media repository
     */
    public MediaService(final MediaRepository mediaRepositoryArg) {
        this.mediaRepository = mediaRepositoryArg;
    }

    /**
     * Creates new media.
     *
     * @param media the media to create
     * @param creatorId the creator's user ID
     * @return the created media
     */
    public Media createMedia(final Media media, final int creatorId) {
        Media newMedia = new Media.Builder()
                .title(media.getTitle())
                .description(media.getDescription())
                .mediaType(media.getMediaType())
                .releaseYear(media.getReleaseYear())
                .ageRestriction(media.getAgeRestriction())
                .creatorId(creatorId)
                .averageRating(0.0)
                .genres(media.getGenres())
                .build();
        mediaRepository.save(newMedia);
        return newMedia;
    }

    /**
     * Retrieves media with filtering options.
     *
     * @param title the title filter
     * @param mediaType the media type filter
     * @param releaseYear the release year filter
     * @param ageRestriction the age restriction filter
     * @param genre the genre filter
     * @param minRating the minimum rating filter
     * @return filtered list of media
     */
    public List<Media> getMedia(final String title, final String mediaType,
                                final Integer releaseYear,
                                final Integer ageRestriction,
                                final String genre, final Double minRating) {
        return mediaRepository.findAll(title, mediaType, releaseYear,
                ageRestriction, genre, minRating);
    }

    /**
     * Gets media by ID.
     *
     * @param id the media ID
     * @return optional media
     */
    public Optional<Media> getMediaById(final int id) {
        return mediaRepository.findById(id);
    }

    /**
     * Updates existing media.
     *
     * @param id the media ID
     * @param mediaUpdates the updates
     * @param userId the user ID performing the update
     * @return the updated media
     */
    public Media updateMedia(final int id,
                             final Media mediaUpdates,
                             final int userId) {
        Optional<Media> existingOpt = mediaRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }
        Media existing = existingOpt.get();
        if (existing.getCreatorId() != userId) {
            throw new IllegalArgumentException(
                    "User is not the creator of this media");
        }

        Media updated = new Media.Builder()
                .id(existing.getId())
                .creatorId(existing.getCreatorId())
                .averageRating(existing.getAverageRating())
                .createdAt(existing.getCreatedAt())
                // Updated fields
                .title(mediaUpdates.getTitle() != null
                        ? mediaUpdates.getTitle() : existing.getTitle())
                .description(mediaUpdates.getDescription() != null
                        ? mediaUpdates.getDescription()
                        : existing.getDescription())
                .mediaType(mediaUpdates.getMediaType() != null
                        ? mediaUpdates.getMediaType() : existing.getMediaType())
                .releaseYear(mediaUpdates.getReleaseYear() != null
                        ? mediaUpdates.getReleaseYear() : existing.getReleaseYear())
                .ageRestriction(mediaUpdates.getAgeRestriction() != null
                        ? mediaUpdates.getAgeRestriction()
                        : existing.getAgeRestriction())
                .genres(mediaUpdates.getGenres() != null
                        ? mediaUpdates.getGenres() : existing.getGenres())
                .build();
        mediaRepository.update(updated);
        return updated;
    }

    /**
     * Deletes media by ID.
     *
     * @param id the media ID
     * @param userId the user ID requesting deletion
     */
    public void deleteMedia(final int id, final int userId) {
        Optional<Media> existingOpt = mediaRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }
        Media existing = existingOpt.get();
        if (existing.getCreatorId() != userId) {
            throw new IllegalArgumentException(
                    "User is not the creator of this media");
        }
        mediaRepository.delete(id);
    }
}
