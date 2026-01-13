package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.FavoriteRepository;
import at.fhtw.swen1.mrp.repository.MediaRepository;

import java.util.List;

public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository; // Strictly to check if media exists?

    public FavoriteService(FavoriteRepository favoriteRepository, MediaRepository mediaRepository) {
        this.favoriteRepository = favoriteRepository;
        this.mediaRepository = mediaRepository;
    }

    public void addFavorite(int userId, int mediaId) {
        if (mediaRepository.findById(mediaId).isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }
        favoriteRepository.add(userId, mediaId);
    }

    public void removeFavorite(int userId, int mediaId) {
        // Idempotent? Or check existence? 
        // Let's just remove.
        favoriteRepository.remove(userId, mediaId);
    }

    public List<Media> getFavorites(int userId) {
        return favoriteRepository.findByUserId(userId);
    }
}
