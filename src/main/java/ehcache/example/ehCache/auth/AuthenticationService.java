package ehcache.example.ehCache.auth;


import ehcache.example.ehCache.Dao.AdminDao;
import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Entity.Admin;
import ehcache.example.ehCache.JWTconfiguration.JwtService;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final AdminDao repository ;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenDAO tokenDAO ;
    public AuthenticationResponse register(RegisterRequest  request) {
        Admin admin;
        if (request.getRole().name().equals("ADMIN")) {

            admin=Admin.builder().username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .role(request.getRole())
                    .build();

        } else {
            throw new IllegalArgumentException("Invalid user type");
        }

        Admin CheckUser = repository.findAdminByEmail(admin.getEmail()) ;

        if (CheckUser!=null) {

           if(   CheckUser.isIsVerified() )
           {
               throw new IllegalArgumentException("User already verified") ;

           }

           else {

               //send Token  to Email  :



           }

        }





       var savedUser= repository.save(admin) ;


var jwtToken=jwtService.generateToken(admin) ;


        saveUserToken(savedUser, jwtToken);



        return AuthenticationResponse.builder()

        .token(jwtToken)
        .build();

    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) throws BadCredentialsException {

        System.out.println("username = "+request.getUsername() +" pass = "+request.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );


        } catch (BadCredentialsException e) {
            System.out.println("username = "+request.getUsername() +" pass = "+request.getPassword());

                throw new RuntimeException("Invalid username or password");
        }


        var admin = repository.findAdminByUsername(request.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username : " + request.getUsername())
        );

        var jwtToken = jwtService.generateToken(admin);

        revokeAllUserTokens(admin);

        saveUserToken(admin,jwtToken);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    private void saveUserToken(Admin admin, String jwtToken) {
        var token = Token.builder()
                .admin(admin)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build() ;
        tokenDAO.save(token) ;
    }

    private void revokeAllUserTokens(Admin user){


        var validUserTokens = tokenDAO.findAllValidTokenByUser(user.getId()) ;
        if(validUserTokens.isEmpty()) return;

        validUserTokens.forEach(t->{

            t.setExpired(true);

            t.setRevoked(true);


        });

        tokenDAO.saveAll(validUserTokens);

    }

}