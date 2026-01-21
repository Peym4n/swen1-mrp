package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.dto.MediaDTO;
import at.fhtw.swen1.mrp.dto.RatingDTO;
import at.fhtw.swen1.mrp.dto.UserDTO;
import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MRPIntegrationTest {

    private static HttpServer server;
    private static final int PORT = 8082;
    private static final String BASE_URI = "http://localhost:" + PORT + "/api";
    
    // Store tokens/Ids for sharing between tests
    private static String tokenUser1;
    private static String tokenUser2;
    private static String tokenUser3;
    private static int user1Id;
    private static int user2Id;
    private static int user3Id;
    
    private static int mediaIdMatrix;
    private static int mediaIdInception;
    private static int mediaIdGodfather;

    @BeforeAll
    public static void startServer() throws IOException {
        // Initialize DB Connection
        DatabaseManager.getInstance();
        
        // Start Server
        server = Main.startServer(PORT);
        RestAssured.baseURI = BASE_URI;
    }

    @AfterAll
    public static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @BeforeEach
    public void setUp() {
         // We do NOT reset DB before every test method here if we want to build up state
         // If we wanted independent tests, we would reset and recreate data.
         // Given the complexity (Leaderboard depends on ratings), we will run ordered tests 1..N 
         // on a single clean DB started at Test 1.
    }
    
    @Test
    @Order(1)
    public void testDatabaseResetAndUserRegistration() {
        // 1. Reset Database
        DatabaseManager.getInstance().resetDatabase();
        
        // 2. Register Users (5 users)
        // User 1: Sci-Fi fan
        user1Id = registerUser("user1", "pass", "u1@test.com", "sci-fi");
        // User 2: Action fan
        user2Id = registerUser("user2", "pass", "u2@test.com", "action");
        // User 3: Drama fan
        user3Id = registerUser("user3", "pass", "u3@test.com", "drama");
        registerUser("user4", "pass", "u4@test.com", "comedy");
        registerUser("user5", "pass", "u5@test.com", "horror");
        
        // 3. Login
        tokenUser1 = loginUser("user1", "pass");
        tokenUser2 = loginUser("user2", "pass");
        tokenUser3 = loginUser("user3", "pass");
        
        System.out.println("Users Registered and Logged In. User1 ID: " + user1Id);
    }
    
    @Test
    @Order(2)
    public void testMediaCreation() {
        // Create Media items
        // Matrix (Sci-Fi, Action) - Created by User 1
        MediaDTO matrix = new MediaDTO();
        matrix.setTitle("The Matrix");
        matrix.setMediaType("movie");
        matrix.setReleaseYear(1999);
        matrix.setAgeRestriction(16);
        matrix.setGenres(Arrays.asList("sci-fi", "action"));
        matrix.setDescription("Simulated reality");
        
        mediaIdMatrix = createMedia(matrix, tokenUser1);
        
        // Inception (Sci-Fi, Thriller) - Created by User 2
        MediaDTO inception = new MediaDTO();
        inception.setTitle("Inception");
        inception.setMediaType("movie");
        inception.setReleaseYear(2010);
        inception.setAgeRestriction(12);
        inception.setGenres(Arrays.asList("sci-fi", "thriller"));
        
        mediaIdInception = createMedia(inception, tokenUser2);
        
        // Godfather (Crime, Drama) - Created by User 3
        MediaDTO godfather = new MediaDTO();
        godfather.setTitle("The Godfather");
        godfather.setMediaType("movie");
        godfather.setReleaseYear(1972);
        godfather.setAgeRestriction(16);
        godfather.setGenres(Arrays.asList("crime", "drama"));
        
        mediaIdGodfather = createMedia(godfather, tokenUser3);
    }
    
    @Test
    @Order(3)
    public void testRatingAndCalculation() {
        // Rate Matrix
        // User 2 gives 5 stars
        rateMedia(mediaIdMatrix, 5, "Amazing", tokenUser2);
        // User 3 gives 4 stars
        rateMedia(mediaIdMatrix, 4, "Good", tokenUser3);
        
        // Rate Inception
        // User 1 gives 5 stars
        rateMedia(mediaIdInception, 5, "Dreamy", tokenUser1);
        
        // Verify Average of Matrix (4.5)
        given()
            .header("Authorization", "Bearer " + tokenUser1) // Any user can view
        .when()
            .get("/media/" + mediaIdMatrix)
        .then()
            .statusCode(200)
            .body("averageRating", equalTo(4.5f));
            
        System.out.println("Ratings Verified");
    }
    
    @Test
    @Order(4)
    public void testLeaderboard() {
        // Current Ratings (from previous tests):
        // User 1: 1 rating (Inception)
        // User 2: 1 rating (Matrix)
        // User 3: 1 rating (Matrix)
        
        // Make User 2 rate Godfather to have the most ratings
        rateMedia(mediaIdGodfather, 3, "Ok", tokenUser2);
        
        // Now User 2 has 2 ratings. User 1 has 1. User 3 has 1.
        
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/leaderboard")
        .then()
            .statusCode(200)
            .body("[0].username", equalTo("user2")) // Most active
            .body("[0].ratingCount", equalTo(2));
    }
    
    @Test
    @Order(5)
    public void testFiltering() {
        // Filter by Genre = sci-fi
        // Should find Matrix and Inception, not Godfather
        given()
            .header("Authorization", "Bearer " + tokenUser1)
            .queryParam("genre", "sci-fi")
        .when()
            .get("/media")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("title", hasItems("The Matrix", "Inception"))
            .body("title", not(hasItem("The Godfather")));
    }
    
    @Test
    @Order(6)
    public void testGetUserProfile() {
        // User 1 has rated 1 movie (Inception) with 5 stars
        // Should return: totalRatings=1, averageScore=5.0
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/users/" + user1Id + "/profile")
        .then()
            .statusCode(200)
            .body("id", equalTo(user1Id))
            .body("username", equalTo("user1"))
            .body("email", equalTo("u1@test.com"))
            .body("favoriteGenre", equalTo("sci-fi"))
            .body("totalRatings", equalTo(1))
            .body("averageScore", equalTo(5.0f));
            
        // User 2 has rated 2 movies (Matrix: 5, Godfather: 3) = avg 4.0
        given()
            .header("Authorization", "Bearer " + tokenUser2)
        .when()
            .get("/users/" + user2Id + "/profile")
        .then()
            .statusCode(200)
            .body("username", equalTo("user2"))
            .body("totalRatings", equalTo(2))
            .body("averageScore", equalTo(4.0f));
    }
    
    @Test
    @Order(7)
    public void testRecommendations() {
        // Recommendations for User 1 (Fav Genre: sci-fi)
        // User 1 rated Inception (sci-fi).
        // User 1 has NOT rated Matrix (sci-fi). Matrix is highly rated (4.5).
        // Expect Matrix to be recommended.
        
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/users/" + user1Id + "/recommendations")
        .then()
            .statusCode(200)
            // Matrix should be recommended
            .body("find { it.id == " + mediaIdMatrix + " }.title", equalTo("The Matrix"));
            
        // Test Recommendations for User 3 (Fav Genre: drama)
        // User 3 rated Matrix (sci-fi/action). 
        // User 3 has NOT rated Godfather (crime/drama).
        // Godfather matches 'drama' genre.
        // Expect Godfather.
        
         given()
            .header("Authorization", "Bearer " + tokenUser3)
        .when()
            .get("/users/" + user3Id + "/recommendations")
        .then()
            .statusCode(200)
            .body("find { it.id == " + mediaIdGodfather + " }.title", equalTo("The Godfather"));
    }
    
    @Test
    @Order(8)
    public void testFavorites() {
        // User 1 favorites Godfather
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .post("/media/" + mediaIdGodfather + "/favorite")
        .then()
            .statusCode(201);
            
        // Check favorites
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/users/" + user1Id + "/favorites")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].id", equalTo(mediaIdGodfather));
            
        // Remove
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .delete("/media/" + mediaIdGodfather + "/favorite")
        .then()
            .statusCode(200);
            
        // Check empty
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/users/" + user1Id + "/favorites")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    @Order(9)
    public void testRatingUpdateAndDelete() {
        // User 3 updates their rating of Matrix from 4 to 5 stars
        // First, get the rating ID (we'll use user3's rating of Matrix)
        // For simplicity, we'll create a new rating first
        
        // Create a rating we can update
        int ratingId = rateMediaAndGetId(mediaIdGodfather, 2, "Initial comment", tokenUser3);
        
        // Update the rating
        RatingDTO updateDTO = new RatingDTO();
        updateDTO.setStars(5);
        updateDTO.setComment("Updated to 5 stars!");
        
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + tokenUser3)
            .body(updateDTO)
        .when()
            .put("/ratings/" + ratingId)
        .then()
            .statusCode(200);
        
        // Verify Godfather's average rating updated
        // Previous: User2 gave 3, User3 gave 2 → avg was 2.5
        // After update: User2 gave 3, User3 gave 5 → avg should be 4.0
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/media/" + mediaIdGodfather)
        .then()
            .statusCode(200)
            .body("averageRating", equalTo(4.0f));
        
        // Delete the rating
        given()
            .header("Authorization", "Bearer " + tokenUser3)
        .when()
            .delete("/ratings/" + ratingId)
        .then()
            .statusCode(204);
        
        // Verify average updated after delete (only User2's 3 stars remains)
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/media/" + mediaIdGodfather)
        .then()
            .statusCode(200)
            .body("averageRating", equalTo(3.0f));
    }

    @Test
    @Order(10)
    public void testDuplicateRatingPrevention() {
        // User 1 already rated Inception (in test 3)
        // Try to rate it again - should fail
        RatingDTO dto = new RatingDTO();
        dto.setStars(3);
        dto.setComment("Duplicate attempt");
        
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + tokenUser1)
            .body(dto)
        .when()
            .post("/media/" + mediaIdInception + "/rate")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(11)
    public void testRatingLikes() {
        // Get a rating ID (User2 rated Matrix)
        // Create a new rating to like
        int ratingId = rateMediaAndGetId(mediaIdInception, 4, "Likeable comment", tokenUser3);
        
        // User 1 likes this rating
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .post("/ratings/" + ratingId + "/like")
        .then()
            .statusCode(200);
        
        // User 2 also likes it
        given()
            .header("Authorization", "Bearer " + tokenUser2)
        .when()
            .post("/ratings/" + ratingId + "/like")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(12)
    public void testCommentModeration() {
        // Create a rating with a comment
        int ratingId = rateMediaAndGetId(mediaIdMatrix, 3, "Needs moderation", tokenUser1);
        
        // Confirm the rating (admin/moderator action)
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .post("/ratings/" + ratingId + "/confirm")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(13)
    public void testMediaUpdate() {
        // Update Matrix (created by User1)
        MediaDTO updateDTO = new MediaDTO();
        updateDTO.setTitle("The Matrix Reloaded");
        updateDTO.setDescription("Updated description");
        updateDTO.setMediaType("movie");
        updateDTO.setReleaseYear(2003);
        updateDTO.setAgeRestriction(16);
        updateDTO.setGenres(Arrays.asList("sci-fi", "action"));
        
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + tokenUser1)
            .body(updateDTO)
        .when()
            .put("/media/" + mediaIdMatrix)
        .then()
            .statusCode(200);
        
        // Verify update
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/media/" + mediaIdMatrix)
        .then()
            .statusCode(200)
            .body("title", equalTo("The Matrix Reloaded"))
            .body("releaseYear", equalTo(2003));
    }

    @Test
    @Order(14)
    public void testOwnershipEnforcement() {
        // User2 tries to update User1's media (Matrix)
        MediaDTO updateDTO = new MediaDTO();
        updateDTO.setTitle("Hacked Title");
        updateDTO.setMediaType("movie");
        updateDTO.setReleaseYear(1999);
        updateDTO.setAgeRestriction(16);
        updateDTO.setGenres(Arrays.asList("sci-fi"));
        
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + tokenUser2)
            .body(updateDTO)
        .when()
            .put("/media/" + mediaIdMatrix)
        .then()
            .statusCode(400); // Service returns 400 for ownership violations
        
        // User2 tries to delete User1's media
        given()
            .header("Authorization", "Bearer " + tokenUser2)
        .when()
            .delete("/media/" + mediaIdMatrix)
        .then()
            .statusCode(400); // Service returns 400 for ownership violations
    }

    @Test
    @Order(15)
    public void testAdvancedFiltering() {
        // Filter by title
        given()
            .header("Authorization", "Bearer " + tokenUser1)
            .queryParam("title", "god")
        .when()
            .get("/media")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("title", hasItem(containsString("Godfather")));
        
        // Filter by mediaType
        given()
            .header("Authorization", "Bearer " + tokenUser1)
            .queryParam("mediaType", "movie")
        .when()
            .get("/media")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(3));
        
        // Filter by ageRestriction
        given()
            .header("Authorization", "Bearer " + tokenUser1)
            .queryParam("ageRestriction", 12)
        .when()
            .get("/media")
        .then()
            .statusCode(200)
            .body("title", hasItem("Inception"));
        
        // Filter by minRating (average >= 3.0)
        given()
            .header("Authorization", "Bearer " + tokenUser1)
            .queryParam("rating", 3.0)
        .when()
            .get("/media")
        .then()
            .statusCode(200);
        
        // Combined filters: sci-fi movies with age >= 16
        given()
            .header("Authorization", "Bearer " + tokenUser1)
            .queryParam("genre", "sci-fi")
            .queryParam("ageRestriction", 16)
        .when()
            .get("/media")
        .then()
            .statusCode(200)
            .body("title", hasItem(containsString("Matrix")));
    }

    @Test
    @Order(16)
    public void testErrorHandling() {
        // Invalid login credentials
        UserDTO invalidUser = new UserDTO();
        invalidUser.setUsername("user1");
        invalidUser.setPassword("wrongpassword");
        
        given()
            .contentType(ContentType.JSON)
            .body(invalidUser)
        .when()
            .post("/users/login")
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(401)));
        
        // No auth token - media is public, should return 200
        given()
        .when()
            .get("/media/" + mediaIdMatrix)
        .then()
            .statusCode(200);
        
        // Invalid rating (stars out of range)
        RatingDTO invalidRating = new RatingDTO();
        invalidRating.setStars(10); // Invalid
        invalidRating.setComment("Invalid");
        
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + tokenUser1)
            .body(invalidRating)
        .when()
            .post("/media/" + mediaIdMatrix + "/rate")
        .then()
            .statusCode(400);
        
        // Non-existent media ID
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/media/99999")
        .then()
            .statusCode(404);
        
        // Non-existent user profile
        given()
            .header("Authorization", "Bearer " + tokenUser1)
        .when()
            .get("/users/99999/profile")
        .then()
            .statusCode(404);
    }


    /// HELPERS ///

    private int registerUser(String username, String password, String email, String genre) {
        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setEmail(email);
        dto.setFavoriteGenre(genre);

        return given()
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/users/register")
        .then()
            .statusCode(201)
            .extract().path("id");
    }

    private String loginUser(String username, String password) {
        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        dto.setPassword(password);

        return given()
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/users/login")
        .then()
            .statusCode(200)
            .extract().path("token");
    }
    
    private int createMedia(MediaDTO media, String token) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(media)
        .when()
            .post("/media")
        .then()
            .statusCode(201)
            .extract().path("id");
    }
    
    private void rateMedia(int mediaId, int stars, String comment, String token) {
        RatingDTO dto = new RatingDTO();
        dto.setStars(stars);
        dto.setComment(comment);
        
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(dto)
        .when()
            .post("/media/" + mediaId + "/rate")
        .then()
            .statusCode(201);
    }
    
    private int rateMediaAndGetId(int mediaId, int stars, String comment, String token) {
        RatingDTO dto = new RatingDTO();
        dto.setStars(stars);
        dto.setComment(comment);
        
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(dto)
        .when()
            .post("/media/" + mediaId + "/rate")
        .then()
            .statusCode(201)
            .extract().path("id");
    }
}
