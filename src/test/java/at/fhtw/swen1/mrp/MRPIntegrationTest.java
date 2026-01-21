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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
         // implementation (Phase 1 -> Phase 2).
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
        // Current Ratings:
        // User 1: 1 rating
        // User 2: 2 ratings (created 1, rated 1) -- Wait, create doesn't count as rating.
        // User 2 rated Matrix (1)
        // User 3 rated Matrix (1)
        
        // Let's make User 2 rate Godfather too so they are top
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
    @Order(7)
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
}
