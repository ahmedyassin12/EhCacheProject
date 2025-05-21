package ehcache.example.ehCache.authTests;

import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dao.VerificationTokenRepository;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.JWTconfiguration.JwtService;
import ehcache.example.ehCache.auth.Dto.*;
import ehcache.example.ehCache.auth.Service.AuthenticationService;
import ehcache.example.ehCache.auth.Service.EmailService;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import ehcache.example.ehCache.token.VerificationToken;
import ehcache.example.ehCache.validator.ObjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.cache.CacheManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenDAO tokenDAO;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private  ObjectValidator<RegisterRequest> registerRequestvalidator ;
    @Mock
    private  ObjectValidator<EmailRequest> emailRequestvalidator ;
    @Mock
    private  ObjectValidator<AuthenticationRequest> authRequestvalidator ;
    @Mock
    private  ObjectValidator<ChangePasswordRequest> changePasswordRequestvalidator ;

    @Mock
    private CacheManager cacheManager ;


    @InjectMocks
    private AuthenticationService authenticationService;


    private RegisterRequest validRegisterRequest;
    private User testUser;
    private User testUser2 ;
    private VerificationToken testVerificationToken;
    private Token mockToken ;
    @BeforeEach
    void setUp() {
        validRegisterRequest = RegisterRequest.builder()
                .username("adminUser")
                .email("admin@example.com")
                .password("securePassword")
                .name("Admin User")
                .role(Role.ADMIN)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("adminUser")
                .email("admin@example.com")
                .password("encodedPassword")
                .name("Admin User")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

         mockToken = Token.builder()
                .tokenType(TokenType.BEARER)
                .expired(false)
                .token("validToken")
                .revoked(false)
                .user(testUser)
                .build();

        testVerificationToken = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
    }

    @Test
    void register_NewAdminUser_ShouldCreateAndSendVerification() {
        // Arrange
        when(userDao.findUserByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDao.save(any(User.class))).thenReturn(testUser);
// Mock the void method
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
        String result = authenticationService.register(validRegisterRequest);

        // Assert
        assertNotNull(result);
       verify(userDao).save(any(User.class));
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendVerificationEmail(anyString(), anyString());
    }



    @Test
    void register_ExistingVerifiedUser_ShouldThrowException() {

        // Arrange
        User verifiedUser = testUser.toBuilder().enabled(true).build();
        when(userDao.findUserByEmail(anyString())).thenReturn(verifiedUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(validRegisterRequest);
        });
    }


    @Test
    void forgetPassword_InvalidEmail_ShouldReturnErrorMessage() {
        // Arrange
        EmailRequest emailRequest = new EmailRequest("invalid@example.com");
        when(userDao.findUserByEmail(anyString())).thenReturn(null);

        // Act
        String result = authenticationService.ForgetPassword(emailRequest);

        // Assert
        assertEquals("Invalid email", result);

    }

    @Test
    void revokeAllUserTokens_ShouldInvalidateExistingTokens() {
        // Arrange
        Token validToken = Token.builder()
                .token("validToken")
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        when(tokenDAO.findAllValidTokenByUser(anyLong()))
                .thenReturn(Collections.singletonList(validToken));

        // Act
        revokeAllUserToken(testUser,validToken);

        // Assert
        assertTrue(validToken.isExpired());
        assertTrue(validToken.isRevoked());
    }
    private void revokeAllUserToken(User user,Token validToken){

        when(tokenDAO.findAllValidTokenByUser(anyLong()))
                .thenReturn(Collections.singletonList(validToken));

        var validUserTokens = tokenDAO.findAllValidTokenByUser(user.getId()) ;

        if(validUserTokens.isEmpty()) return;

        validUserTokens.forEach(t->{

            t.setExpired(true);

            t.setRevoked(true);


        });


    }
}