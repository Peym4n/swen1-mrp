package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.MediaRepository;

import java.util.List;

public class RecommendationService {
    private final MediaRepository mediaRepository;

    public RecommendationService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Media> getRecommendations(int userId) {
        return mediaRepository.findRecommendations(userId);
    }
}
