package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.MediaRepository;

import java.util.List;
import java.util.Optional;

public class MediaService {
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media createMedia(Media media, int creatorId) {
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

    public List<Media> getMedia(String title, String mediaType, Integer releaseYear, Integer ageRestriction) {
        return mediaRepository.findAll(title, mediaType, releaseYear, ageRestriction);
    }
    
    public Optional<Media> getMediaById(int id) {
        return mediaRepository.findById(id);
    }

    public Media updateMedia(int id, Media mediaUpdates, int userId) {
        Optional<Media> existingOpt = mediaRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }
        Media existing = existingOpt.get();
        if (existing.getCreatorId() != userId) {
             throw new IllegalArgumentException("User is not the creator of this media");
        }
        
        Media updated = new Media.Builder()
                .id(existing.getId())
                .creatorId(existing.getCreatorId())
                .averageRating(existing.getAverageRating())
                .createdAt(existing.getCreatedAt())
                // Updated fields
                .title(mediaUpdates.getTitle() != null ? mediaUpdates.getTitle() : existing.getTitle())
                .description(mediaUpdates.getDescription() != null ? mediaUpdates.getDescription() : existing.getDescription())
                .mediaType(mediaUpdates.getMediaType() != null ? mediaUpdates.getMediaType() : existing.getMediaType())
                .releaseYear(mediaUpdates.getReleaseYear() != null ? mediaUpdates.getReleaseYear() : existing.getReleaseYear())
                .ageRestriction(mediaUpdates.getAgeRestriction() != null ? mediaUpdates.getAgeRestriction() : existing.getAgeRestriction())
                .genres(mediaUpdates.getGenres() != null ? mediaUpdates.getGenres() : existing.getGenres())
                .build();
                
        mediaRepository.update(updated);
        return updated;
    }

    public void deleteMedia(int id, int userId) {
        Optional<Media> existingOpt = mediaRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }
        Media existing = existingOpt.get();
        if (existing.getCreatorId() != userId) {
             throw new IllegalArgumentException("User is not the creator of this media");
        }
        mediaRepository.delete(id);
    }
}
