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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
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



    @InjectMocks
    private AuthenticationService authenticationService;


    private RegisterRequest validRegisterRequest;
    private User testUser;
    private VerificationToken testVerificationToken;

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
                .enabled(false)
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
    void authenticate_ValidCredentials_ShouldReturnToken() {

        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("adminUser", "password");

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser, null));
        when(userDao.findUserByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response.getToken());
        verify(tokenDAO).save(any(Token.class));
        verify(tokenDAO).findAllValidTokenByUser(anyLong());
    }

    @Test
    void authenticate_InvalidCredentials_ShouldThrowException() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("adminUser", "wrongPassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationService.authenticate(request);
        });
    }

    @Test
    void changePassword_ValidRequest_ShouldUpdatePassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword", "newPassword", "newPassword"
        );

        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken(testUser, null);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        // Act
        authenticationService.changePassword(request, principal);

        // Assert
        verify(userDao).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(userCaptor.capture());
        assertEquals("newEncodedPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void changePassword_WrongCurrentPassword_ShouldThrowException() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
                "wrongPassword", "newPassword", "newPassword"
        );
        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken(testUser, null);

     //   doNothing().when( changePasswordRequestvalidator).validate(request);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.changePassword(request, principal);
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