package ehcache.example.ehCache.auth.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Dao.VerificationTokenRepository;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.JWTconfiguration.JwtService;
import ehcache.example.ehCache.Service.TokenService;
import ehcache.example.ehCache.auth.Dto.*;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import ehcache.example.ehCache.token.VerificationToken;
import ehcache.example.ehCache.validator.ObjectValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.cache.CacheManager;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    //private final AuthenticationService self ;
    private final UserDao repository ;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenDAO tokenDAO ;
    private final TokenService tokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService ;

    private final ObjectValidator<RegisterRequest> registerRequestvalidator ;
    private final ObjectValidator<EmailRequest> emailRequestvalidator ;
    private final ObjectValidator<AuthenticationRequest> authRequestvalidator ;

    private final CacheManager cacheManager;


    public String register(RegisterRequest request) {


        registerRequestvalidator.validate(request);
        User user;
        if (request.getRole().name().equals("ADMIN")||request.getRole().name().equals(Role.SUPER_ADMIN.name())) {

            user = User.builder().username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .role(request.getRole())
                    .enabled(false)
                    .build();
        }


         else {
            throw new IllegalArgumentException("Invalid user type");
        }

            User CheckUser = repository.findUserByEmail(user.getEmail()) ;

        if (CheckUser!=null) {

            //use isEnabled to know if he is Verified
           if(   CheckUser.isEnabled() )
           {
               throw new IllegalArgumentException("User already verified") ;

           }

           //if user exist but didnt verified
           else {

               //user exist, but it's not verified so we go and verify him :
                return createAndSendVerificationToken(CheckUser);

}
        }



 //new User :


       var savedUser= repository.save(user) ;

return createAndSendVerificationToken(savedUser);


    }

    private String createAndSendVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();

        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), token);
        return token;
    }
//qqqqqqqqqqqqq
public String ForgetPassword(EmailRequest  email){

    emailRequestvalidator.validate(email);

        User user = repository.findUserByEmail(email.getEmail());

        if (user==null ) {
            return "Invalid email";
        }

    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = VerificationToken.builder()
            .token(token)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(30))
            .user(user)
            .build();

    verificationTokenRepository.save(verificationToken);


    emailService.sendForgotPasswordEmail(email.getEmail(),token);

return token;





}

//qqqqqqqqqqqqqqqqqqqqqq
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws BadCredentialsException {

        authRequestvalidator.validate(request);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );


        } catch (BadCredentialsException e) {

                throw new RuntimeException("Invalid username or password");
        }




        var user = repository.findUserByUsername(request.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username : " + request.getUsername())
        );

        var jwtToken = jwtService.generateToken(user);
        var refreshToken=jwtService.generateRefreshToken(user);

        //Evict my cache  manually
        List<Token> tokens = tokenDAO.findAllValidTokenByUser(user.getId()) ;

        tokens.forEach(t->cacheManager.getCache("JwtTokens").remove( t.getToken() ) );

        tokenDAO.revokeAllTokensByUser(user.getId());

        saveUserToken(user,jwtToken,TokenType.BEARER);
        saveUserToken(user,refreshToken,TokenType.REFRESHTOKEN);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }

    private void saveUserToken(User user, String jwtToken,TokenType tokenType) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .revoked(false)
                .expired(false)
                .build() ;
        tokenDAO.save(token) ;
    }






    //aaaaaaaaaaaaaaaaaaaaaa



        @Transactional
        public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {



            final String authHeader = request.getHeader("Authorization");
            final String refreshToken;
            final String Username;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                //Skip JWT logic and pass the request down the chain.

                return null;
            }

            refreshToken = authHeader.substring(7);

            Username = jwtService.extractUserName(refreshToken);


            if (Username != null ) {

                var user = this.repository.findUserByUsername(Username).orElseThrow();

              Token checkToken=  tokenService.getToken(refreshToken);


                boolean isTokenValid ;

                if( ! checkToken.isRevoked()) {

                    isTokenValid=true ;

                }
                else{
                    isTokenValid=false;
                }


                if (jwtService.IsTokenValid(refreshToken, user)
                        && isTokenValid
                    ) {

                    var accessToken=jwtService.generateToken(user) ;


                    tokenDAO.revokeBearerTokensByUser(user.getId());

                    List<Token> tokens = tokenDAO.findAllValidBearerTokenByUser(user.getId()) ;

                    tokens.forEach(t-> cacheManager.getCache("JwtTokens").remove( t.getToken() ) );

                    saveUserToken(user,accessToken,TokenType.BEARER);

                   var  authResponse= AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();

                    return  authResponse ;
                }


            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED) ;
            return  null;
        }




}