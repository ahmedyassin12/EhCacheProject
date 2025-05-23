package ehcache.example.ehCache.auth.Controller;

import ehcache.example.ehCache.auth.Dto.*;
import ehcache.example.ehCache.auth.Service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
@Builder
public class AuthenticationController {

    private final AuthenticationService service;



    @PostMapping("/register")
    public ResponseEntity<String> register ( @RequestBody  RegisterRequest request ){

       //

return ResponseEntity.ok(service.register(request)) ;


    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate
            ( @RequestBody  AuthenticationRequest request ) throws BadCredentialsException {

        //
        return ResponseEntity.ok(service.authenticate(request)) ;




    }





    @PostMapping("/forgetPassword")
    public ResponseEntity<String> ForgetPassword( @RequestBody  EmailRequest request){

        service.ForgetPassword(request);
        return ResponseEntity.ok("Reset password email sent. Please check your inbox.");


    }

    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(HttpServletRequest request,
                                               HttpServletResponse response) throws IOException {


       return service.refreshToken(request,response) ;


    }





}
