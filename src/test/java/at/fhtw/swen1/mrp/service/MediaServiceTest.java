package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class MediaServiceTest {
    @Mock
    private MediaRepository mediaRepository;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mediaService = new MediaService(mediaRepository);
    }
    
    @Test
    void testCreateMedia_Success() {
        int userId = 1;
        Media input = new Media.Builder().title("New Movie").mediaType("movie").build();
        
        Media result = mediaService.createMedia(input, userId);
        
        assertNotNull(result);
        assertEquals("New Movie", result.getTitle());
        assertEquals(userId, result.getCreatorId());
        assertEquals(0.0, result.getAverageRating());
        
        verify(mediaRepository).save(any(Media.class));
    }

    @Test
    void testGetMedia_Success() {
        Media media = new Media.Builder().id(1).title("Test").build();
        when(mediaRepository.findAll(any(), any(), any(), any())).thenReturn(List.of(media));
        
        List<Media> result = mediaService.getMedia(null, null, null, null);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
    }

    @Test
    void testGetMediaById_Found() {
        int mediaId = 1;
        Media media = new Media.Builder().id(mediaId).title("Test").build();
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        
        Optional<Media> result = mediaService.getMediaById(mediaId);
        
        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getTitle());
    }

    @Test
    void testGetMediaById_NotFound() {
        int mediaId = 99;
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());
        
        Optional<Media> result = mediaService.getMediaById(mediaId);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateMedia_Success() {
        int mediaId = 1;
        int userId = 10;
        Media existing = new Media.Builder().id(mediaId).creatorId(userId).title("Old").build();
        Media updates = new Media.Builder().title("New").build();
        
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(existing));
        
        Media result = mediaService.updateMedia(mediaId, updates, userId);
        
        assertEquals("New", result.getTitle());
        verify(mediaRepository).update(any(Media.class));
    }

    @Test
    void testUpdateMedia_Unauthorized() {
        int mediaId = 1;
        int userId = 10;
        int otherUser = 99;
        Media existing = new Media.Builder().id(mediaId).creatorId(userId).title("Old").build();
        Media updates = new Media.Builder().title("New").build();
        
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(existing));
        
        assertThrows(IllegalArgumentException.class, () -> {
            mediaService.updateMedia(mediaId, updates, otherUser);
        });
        
        verify(mediaRepository, never()).update(any(Media.class));
    }

    @Test
    void testUpdateMedia_NotFound() {
        int mediaId = 99;
        Media updates = new Media.Builder().title("New").build();
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            mediaService.updateMedia(mediaId, updates, 1);
        });
    }

    @Test
    void testDeleteMedia_Success() {
        int mediaId = 1;
        int userId = 10;
        Media existing = new Media.Builder().id(mediaId).creatorId(userId).build();
        
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(existing));
        
        mediaService.deleteMedia(mediaId, userId);
        
        verify(mediaRepository).delete(mediaId);
    }
    
    @Test
    void testDeleteMedia_Unauthorized() {
        int mediaId = 1;
        int userId = 10;
        int otherUser = 99;
        Media existing = new Media.Builder().id(mediaId).creatorId(userId).build();
        
        // Ensure the mock returns the existing media
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(existing));
        
        // Log to debug if needed (not possible here, but logic helps)
        // userId 10 != otherUser 99, so expected exception.
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mediaService.deleteMedia(mediaId, otherUser);
        });
        
        assertEquals("User is not the creator of this media", exception.getMessage());
        
        // Verify delete was NEVER called
        verify(mediaRepository, never()).delete(anyInt());
    }
}
