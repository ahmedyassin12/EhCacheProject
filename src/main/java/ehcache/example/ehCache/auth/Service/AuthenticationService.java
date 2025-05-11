package ehcache.example.ehCache.auth.Service;


import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Dao.VerificationTokenRepository;
import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.JWTconfiguration.JwtService;
import ehcache.example.ehCache.auth.Dto.*;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import ehcache.example.ehCache.token.VerificationToken;
import ehcache.example.ehCache.validator.ObjectValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserDao repository ;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenDAO tokenDAO ;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService ;

    private final ObjectValidator<RegisterRequest> registerRequestvalidator ;
    private final ObjectValidator<EmailRequest> emailRequestvalidator ;
    private final ObjectValidator<AuthenticationRequest> authRequestvalidator ;
    private final ObjectValidator<ChangePasswordRequest> changePasswordRequestvalidator ;



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

        revokeAllUserTokens(user);

        saveUserToken(user,jwtToken);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build() ;
        tokenDAO.save(token) ;
    }

    private void revokeAllUserTokens(User user){


        var validUserTokens = tokenDAO.findAllValidTokenByUser(user.getId()) ;
        if(validUserTokens.isEmpty()) return;

        validUserTokens.forEach(t->{

            t.setExpired(true);

            t.setRevoked(true);


        });

        tokenDAO.saveAll(validUserTokens);

    }

    //aaaaaaaaaaaaaaaaaaaaaa
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {


        changePasswordRequestvalidator.validate(request);

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal() ;


        //check if current pass is correct :
        if( !passwordEncoder.matches(request.getCurrentPassword(),user.getPassword() ) ){

            throw new IllegalArgumentException("wrong password") ;

        }


        //check if password are the same :
        if (! request.getConfirmationPassword().equals(request.getNewPassword()))
            throw new IllegalArgumentException("password are not the same") ;


        //update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        //save pass
        repository.save(user) ;


    }

}