package ehcache.example.ehCache.auth.Controller;

import ehcache.example.ehCache.auth.Dto.*;
import ehcache.example.ehCache.auth.Service.AuthenticationService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/changePassword")
    public ResponseEntity<?>changePaassword(
            @RequestBody  ChangePasswordRequest request
            ,
            Principal connectedUser
    )
    {



        service.changePassword(request,connectedUser) ;
        return  ResponseEntity.ok().build() ;

    }



    @PostMapping("/forgetPassword")
    public ResponseEntity<String> ForgetPassword( @RequestBody  EmailRequest request){

        service.ForgetPassword(request);
        return ResponseEntity.ok("Reset password email sent. Please check your inbox.");


    }





}
