package ehcache.example.ehCache.JwtTests;

import ehcache.example.ehCache.JWTconfiguration.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(authority)
                .build();
    }

    @Test
    void testExtractUserName() {
        // Generate a token first
        String token = jwtService.generateToken(userDetails);

        // Test extraction
        String extractedUsername = jwtService.extractUserName(token);

        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    void testGenerateTokenWithUserDetails() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify the token can be parsed and contains expected claims
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(userDetails.getUsername(), claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.get("authorities") != null);
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        String token = jwtService.generateToken(extraClaims, userDetails);

        assertNotNull(token);

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("customValue", claims.get("customClaim"));
        assertEquals(userDetails.getUsername(), claims.getSubject());
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.IsTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_InvalidUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser = User.withUsername("differentUser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        boolean isValid = jwtService.IsTokenValid(token, differentUser);

        assertFalse(isValid);
    }

  /*
    //change method to Public inorder to test
    @Test
    void testIsTokenExpired_NotExpired() {
        String token = jwtService.generateToken(userDetails);
        boolean isExpired = jwtService.IsTokenExpired(token);

        assertFalse(isExpired);
    }
    //change method to Public inorder to test

    @Test
    void testIsTokenExpired_ExpiredToken() {
        // Fixed time for testing
        long fixedTime = System.currentTimeMillis();
        Clock fixedClock = Clock.fixed(Instant.ofEpochMilli(fixedTime), ZoneId.systemDefault());

        // Create a token with past expiration
        Date now = new Date();
        Date expiration = new Date(fixedTime + 1000); // 1 second in the past

        String expiredToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(fixedTime - 5000)) // 5 seconds ago
                .setExpiration(expiration)
                .signWith(getSigningKey())
                .compact();

        assertTrue(jwtService.IsTokenExpired(expiredToken));

        // Validate 2 seconds after expiration
                fixedClock = Clock.fixed(Instant.ofEpochMilli(fixedTime + 2000), ZoneId.systemDefault());

    }


 */

    @Test
    void testExtractClaim() {
        String token = jwtService.generateToken(userDetails);

        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);

        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals(userDetails.getUsername(), subject);

    }

    // u must change method in JWTService to public inorder to test this :
   /* @Test
    void testExtractAllClaims() {
        String token = jwtService.generateToken(userDetails);

        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals(userDetails.getUsername(), claims.getSubject());
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getIssuedAt());
    }
    */

    private SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }




    }




