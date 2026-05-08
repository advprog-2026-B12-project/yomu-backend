package id.ac.ui.cs.advprog.yomubackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    // Provide a valid Base64 encoded secret key for HS256 (at least 256 bits / 32 bytes).
    private final String testSecretKey = "404E635266556A586E327235753878214125442A472D4B6150645367566B5970"; 
    private final long testExpiration = 1000 * 60 * 60 * 24; // 1 day

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        
        assertNotNull(token);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        String username = "testuser";
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ROLE_USER");
        
        String token = jwtService.generateToken(extraClaims, username);
        
        assertNotNull(token);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
        
        // We could also extract claims manually, but since extractClaim uses claimsResolver we can test it
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ROLE_USER", role);
    }

    @Test
    void testIsTokenValid() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        
        boolean isValid = jwtService.isTokenValid(token, username);
        assertTrue(isValid);
    }
    
    @Test
    void testIsTokenInvalidDueToDifferentUsername() {
        String token = jwtService.generateToken("testuser");
        
        boolean isValid = jwtService.isTokenValid(token, "wronguser");
        assertFalse(isValid);
    }

    @Test
    void generateToken_WithPelajarRoleClaim_RoleIsExtractable() {
        String username = "testuser";
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ROLE_PELAJAR");

        String token = jwtService.generateToken(extraClaims, username);

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ROLE_PELAJAR", role);
    }

    @Test
    void generateToken_WithAdminRoleClaim_RoleIsExtractable() {
        String username = "adminuser";
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ROLE_ADMIN");

        String token = jwtService.generateToken(extraClaims, username);

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ROLE_ADMIN", role);
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    void testIsTokenExpiredThrowsException() throws InterruptedException {
        // Set an extremely short expiration time (1ms)
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        String token = jwtService.generateToken("testuser");
        
        // Sleep slightly to guarantee expiration
        Thread.sleep(10);
        
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, "testuser");
        });
    }
}
