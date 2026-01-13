package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.Rating;
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

    @InjectMocks
    private RatingService ratingService;

    @Test
    void testRateMedia_Success() {
        int userId = 1;
        int mediaId = 10;
        int stars = 5;
        String comment = "Great!";

        // Mock: No existing rating
        when(ratingRepository.hasUserRatedMedia(userId, mediaId)).thenReturn(false);

        Rating result = ratingService.rateMedia(userId, mediaId, stars, comment);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(mediaId, result.getMediaId());
        assertEquals(stars, result.getStars());
        assertFalse(result.getIsConfirmed()); // Should be false by default
        
        verify(ratingRepository).save(any(Rating.class));
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
        
        // Mock: Already rated
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
}
