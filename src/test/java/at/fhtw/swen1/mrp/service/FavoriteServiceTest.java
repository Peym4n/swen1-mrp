package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.FavoriteRepository;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;
    
    @Mock
    private MediaRepository mediaRepository;

    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        favoriteService = new FavoriteService(favoriteRepository, mediaRepository);
    }

    @Test
    void testAddFavorite_Success() {
        int userId = 1;
        int mediaId = 10;
        
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(new Media.Builder().id(mediaId).build()));
        
        favoriteService.addFavorite(userId, mediaId);
        
        verify(favoriteRepository).add(userId, mediaId);
    }
    
    @Test
    void testAddFavorite_MediaNotFound() {
        int userId = 1;
        int mediaId = 99;
        
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> favoriteService.addFavorite(userId, mediaId));
        verify(favoriteRepository, never()).add(anyInt(), anyInt());
    }

    @Test
    void testRemoveFavorite() {
        int userId = 1;
        int mediaId = 10;
        
        favoriteService.removeFavorite(userId, mediaId);
        
        verify(favoriteRepository).remove(userId, mediaId);
    }

    @Test
    void testGetFavorites() {
        int userId = 1;
        Media media = new Media.Builder().id(10).title("Fav Movie").build();
        when(favoriteRepository.findByUserId(userId)).thenReturn(List.of(media));
        
        List<Media> result = favoriteService.getFavorites(userId);
        
        assertEquals(1, result.size());
        assertEquals("Fav Movie", result.get(0).getTitle());
    }
}
