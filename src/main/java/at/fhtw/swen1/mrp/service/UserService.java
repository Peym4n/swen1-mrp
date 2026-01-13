package at.fhtw.swen1.mrp.service;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.repository.UserRepository;

import java.util.Optional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        
        String hashedPassword = hashPassword(user.getPassword());
        
        user.setPassword(hashedPassword);
                
        userRepository.save(user);
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedInput = hashPassword(password);
            
            if (user.getPassword().equals(hashedInput)) {
                // Generate token with random part
                String token = "mrp-token-" + java.util.UUID.randomUUID().toString();
                
                // Store/Update token in DB
                userRepository.updateToken(user.getId(), token);
                
                return token; 
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            // Convert byte array to signum representation
            BigInteger number = new BigInteger(1, hash);
            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(16));
            // Pad with leading zeros
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }
    
    public java.util.Optional<User> getUserByToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String cleanToken = token.substring("Bearer ".length());
            return userRepository.findByToken(cleanToken);
        }
        return java.util.Optional.empty();
    }
    
    public User updateProfile(int userId, String email, String favoriteGenre) {
        User user = new User.Builder()
                .id(userId)
                .email(email)
                .favoriteGenre(favoriteGenre)
                .build();
                
        userRepository.update(user);
        
        return user;
    }
}
