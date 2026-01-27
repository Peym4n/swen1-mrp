package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.FavoriteRepository;
import at.fhtw.swen1.mrp.repository.MediaRepository;

import java.util.List;

/**
 * Service for managing favorites.
 */
public final class FavoriteService {
    /** The favorite repository. */
    private final FavoriteRepository favoriteRepository;
    /** The media repository. */
    private final MediaRepository mediaRepository;

    /**
     * Constructor.
     *
     * @param favoriteRepositoryArg the favorite repository
     * @param mediaRepositoryArg the media repository
     */
    public FavoriteService(final FavoriteRepository favoriteRepositoryArg,
                           final MediaRepository mediaRepositoryArg) {
        this.favoriteRepository = favoriteRepositoryArg;
        this.mediaRepository = mediaRepositoryArg;
    }

    /**
     * Adds a favorite.
     *
     * @param userId the user ID
     * @param mediaId the media ID
     */
    public void addFavorite(final int userId, final int mediaId) {
        if (mediaRepository.findById(mediaId).isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }
        favoriteRepository.add(userId, mediaId);
    }

    /**
     * Removes a favorite.
     *
     * @param userId the user ID
     * @param mediaId the media ID
     */
    public void removeFavorite(final int userId, final int mediaId) {
        favoriteRepository.remove(userId, mediaId);
    }

    /**
     * Gets user favorites.
     *
     * @param userId the user ID
     * @return list of favorite media
     */
    public List<Media> getFavorites(final int userId) {
        return favoriteRepository.findByUserId(userId);
    }
}
