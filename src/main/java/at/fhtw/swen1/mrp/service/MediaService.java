package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.MediaRepository;

import java.util.List;

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
}
