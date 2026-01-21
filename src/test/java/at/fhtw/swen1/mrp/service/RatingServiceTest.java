package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    
    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void testRateMedia_Success() {
        int userId = 1;
        int mediaId = 10;
        int stars = 5;
        String comment = "Great!";

        when(ratingRepository.hasUserRatedMedia(userId, mediaId)).thenReturn(false);
        // Mock average calculation
        when(ratingRepository.calculateAverageRating(mediaId)).thenReturn(5.0);

        Rating result = ratingService.rateMedia(userId, mediaId, stars, comment);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(mediaId, result.getMediaId());
        assertEquals(stars, result.getStars());
        assertFalse(result.getIsConfirmed()); // Should be false by default
        
        verify(ratingRepository).save(any(Rating.class));
        verify(mediaRepository).updateAverageRating(mediaId, 5.0);
    }

    @Test
    void testRateMedia_InvalidStars_TooLow() {
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.rateMedia(1, 10, 0, "Bad");
        });
    }

    @Test
    void testRateMedia_InvalidStars_TooHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.rateMedia(1, 10, 6, "Bad");
        });
    }

    @Test
    void testRateMedia_DuplicateRating() {
        int userId = 1;
        int mediaId = 10;
        
        when(ratingRepository.hasUserRatedMedia(userId, mediaId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            ratingService.rateMedia(userId, mediaId, 4, "Duplicate");
        });
    }
    
    @Test
    void testConfirmRating_Success() {
        int ratingId = 5;
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(new Rating.Builder().build()));
        
        ratingService.confirmRating(ratingId);
        
        verify(ratingRepository).confirmRating(ratingId);
    }
    
    @Test
    void testConfirmRating_NotFound() {
        int ratingId = 99;
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.confirmRating(ratingId);
        });
    }

    @Test
    void testUpdateRating_Success() {
        int userId = 1;
        int ratingId = 5;
        int mediaId = 10;
        Rating existing = new Rating.Builder().id(ratingId).userId(userId).mediaId(mediaId).stars(3).comment("Old").build();
        
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(existing));
        when(ratingRepository.calculateAverageRating(mediaId)).thenReturn(4.0);
        
        Rating updated = ratingService.updateRating(userId, ratingId, 5, "New");
        
        assertEquals(5, updated.getStars());
        assertEquals("New", updated.getComment());
        verify(ratingRepository).update(any(Rating.class));
        verify(mediaRepository).updateAverageRating(mediaId, 4.0);
    }

    @Test
    void testUpdateRating_Unauthorized() {
        int userId = 1;
        int otherUserId = 2;
        int ratingId = 5;
        Rating existing = new Rating.Builder().id(ratingId).userId(otherUserId).stars(3).build();
        
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(existing));
        
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.updateRating(userId, ratingId, 5, "New");
        });
    }

    @Test
    void testDeleteRating_Success() {
        int userId = 1;
        int ratingId = 5;
        int mediaId = 10;
        Rating existing = new Rating.Builder().id(ratingId).userId(userId).mediaId(mediaId).build();
        
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(existing));
        when(ratingRepository.calculateAverageRating(mediaId)).thenReturn(0.0);
        
        ratingService.deleteRating(userId, ratingId);
        
        verify(ratingRepository).delete(ratingId);
        verify(mediaRepository).updateAverageRating(mediaId, 0.0);
    }

    @Test
    void testDeleteRating_Unauthorized() {
        int userId = 1;
        int ratingId = 5;
        Rating existing = new Rating.Builder().id(ratingId).userId(99).build();
        
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(existing));
        
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.deleteRating(userId, ratingId);
        });
    }
}
