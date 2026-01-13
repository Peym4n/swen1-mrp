package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
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
}
