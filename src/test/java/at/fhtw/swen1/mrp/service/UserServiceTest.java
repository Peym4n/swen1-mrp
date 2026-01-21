package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.repository.UserRepository;
import at.fhtw.swen1.mrp.dto.UserProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private at.fhtw.swen1.mrp.repository.MediaRepository mediaRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, mediaRepository);
    }

    @Test
    void testRegister_Success() {
        User user = new User.Builder().username("testuser").password("password").build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        userService.register(user);

        verify(userRepository).save(any(User.class));
        assertNotEquals("password", user.getPassword()); // Should be hashed
    }

    @Test
    void testRegister_UserAlreadyExists() {
        User user = new User.Builder().username("testuser").password("password").build();
        User existingUser = new User.Builder().username("testuser").build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> userService.register(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateProfile_Success() {
        int userId = 1;
        String newEmail = "new@test.com";
        String newGenre = "Action";
        
        User result = userService.updateProfile(userId, newEmail, newGenre);
        
        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        assertEquals(newGenre, result.getFavoriteGenre());
        assertEquals(userId, result.getId());
        
        verify(userRepository).update(any(User.class));
    }

    @Test
    void testGetUserProfile_Success() {
        int userId = 1;
        UserProfileDTO mockProfile = new UserProfileDTO();
        mockProfile.setId(userId);
        mockProfile.setUsername("testuser");
        mockProfile.setEmail("test@email.com");
        mockProfile.setFavoriteGenre("Action");
        mockProfile.setTotalRatings(5);
        mockProfile.setAverageScore(4.2);
        
        when(userRepository.getProfileWithStats(userId)).thenReturn(mockProfile);
        
        UserProfileDTO result = userService.getUserProfile(userId);
        
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals(5, result.getTotalRatings());
        assertEquals(4.2, result.getAverageScore());
        
        verify(userRepository).getProfileWithStats(userId);
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        int userId = 999;
        when(userRepository.getProfileWithStats(userId)).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class, () -> userService.getUserProfile(userId));
    }

    @Test
    void testLogin_Success() {
        String username = "testuser";
        String password = "password";
        // SHA-256 of "password"
        String hashedPassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        
        User user = new User.Builder()
                .id(1)
                .username(username)
                .password(hashedPassword)
                .build();
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        
        String token = userService.login(username, password);
        
        assertNotNull(token);
        assertTrue(token.startsWith("mrp-token-"));
        
        verify(userRepository).updateToken(eq(1), anyString());
    }

    @Test
    void testLogin_InvalidCredentials() {
        String username = "testuser";
        String password = "password";
        // SHA-256 of "password"
        String hashedPassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        
        User user = new User.Builder()
                .id(1)
                .username(username)
                .password(hashedPassword)
                .build();
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        
        // Try to login with wrong password
        assertThrows(RuntimeException.class, () -> userService.login(username, "wrongpass"));
        verify(userRepository, never()).updateToken(anyInt(), any());
    }

    @Test
    void testGetUserByToken_Success() {
        String token = "Bearer mrp-token-123";
        String cleanToken = "mrp-token-123";
        User user = new User.Builder().username("test").build();
        
        when(userRepository.findByToken(cleanToken)).thenReturn(Optional.of(user));
        
        Optional<User> result = userService.getUserByToken(token);
        
        assertTrue(result.isPresent());
        assertEquals("test", result.get().getUsername());
    }

    @Test
    void testGetUserByToken_InvalidFormat() {
        String token = "InvalidFormat";
        
        Optional<User> result = userService.getUserByToken(token);
        
        assertTrue(result.isEmpty());
        verify(userRepository, never()).findByToken(anyString());
    }
}
