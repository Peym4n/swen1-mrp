package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.repository.UserRepository;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.dto.UserProfileDTO;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

/**
 * Service for managing users.
 */
public final class UserService {
    /** The user repository. */
    private final UserRepository userRepository;
    /** The media repository. */
    private final MediaRepository mediaRepository;

    /**
     * Constructor.
     *
     * @param userRepositoryArg the user repository
     * @param mediaRepositoryArg the media repository
     */
    public UserService(final UserRepository userRepositoryArg,
                       final MediaRepository mediaRepositoryArg) {
        this.userRepository = userRepositoryArg;
        this.mediaRepository = mediaRepositoryArg;
    }

    /**
     * Constructor for tests that don't need mediaRepository.
     *
     * @param userRepositoryArg the user repository
     */
    public UserService(final UserRepository userRepositoryArg) {
        this(userRepositoryArg, null);
    }

    /**
     * Registers a user.
     *
     * @param user the user to register
     * @return the registered user
     */
    public User register(final User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String hashedPassword = hashPassword(user.getPassword());

        user.setPassword(hashedPassword);

        userRepository.save(user);
        return user;
    }

    /**
     * Logs in a user.
     *
     * @param username the username
     * @param password the password
     * @return the auth token
     */
    public String login(final String username, final String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedInput = hashPassword(password);

            if (user.getPassword().equals(hashedInput)) {
                // Generate token with random part
                String token = "mrp-token-" + UUID.randomUUID().toString();

                // Store/Update token in DB
                userRepository.updateToken(user.getId(), token);

                return token;
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    // CHECKSTYLE:OFF: MagicNumber
    private String hashPassword(final String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            // Convert byte array to signum representation
            final int signum = 1;
            BigInteger number = new BigInteger(signum, hash);
            // Convert message digest into hex value
            final int hexRadix = 16;
            StringBuilder hexString = new StringBuilder(number.toString(hexRadix));
            // Pad with leading zeros
            final int hashLength = 32;
            while (hexString.length() < hashLength) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Gets user by token.
     *
     * @param token the auth token
     * @return optional user
     */
    public Optional<User> getUserByToken(final String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String cleanToken = token.substring("Bearer ".length());
            return userRepository.findByToken(cleanToken);
        }
        return Optional.empty();
    }

    /**
     * Updates user profile.
     *
     * @param userId the user ID
     * @param email the email
     * @param favoriteGenre the favorite genre
     * @return the updated user
     */
    public User updateProfile(final int userId, final String email, final String favoriteGenre) {
        User user = new User.Builder()
                .id(userId)
                .email(email)
                .favoriteGenre(favoriteGenre)
                .build();


        userRepository.update(user);

        return user;
    }

    /**
     * Gets user profile with stats.
     *
     * @param userId the user ID
     * @return the profile DTO
     */
    public UserProfileDTO getUserProfile(final int userId) {
        UserProfileDTO profile = userRepository.getProfileWithStats(userId);
        if (profile == null) {
            throw new IllegalArgumentException("User not found");
        }
        return profile;
    }

    /**
     * Gets recommendations for user.
     *
     * @param userId the user ID
     * @return list of recommended media
     */
    public List<Media> getRecommendations(final int userId) {
        if (mediaRepository == null) {
            throw new IllegalStateException("MediaRepository not initialized");
        }
        return mediaRepository.findRecommendations(userId);
    }
}

