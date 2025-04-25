package ehcache.example.ehCache.auth;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
@Builder
public class AuthenticationController {

    private final AuthenticationService service;



    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register ( @RequestBody @Valid RegisterRequest request ){

       //

return ResponseEntity.ok(service.register(request)) ;


    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate
            ( @RequestBody @Valid AuthenticationRequest request ) throws BadCredentialsException {

        //
        return ResponseEntity.ok(service.authenticate(request)) ;


    }





}
