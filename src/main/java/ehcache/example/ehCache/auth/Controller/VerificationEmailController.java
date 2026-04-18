package ehcache.example.ehCache.auth.Controller;


import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dao.VerificationTokenRepository;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.auth.Dto.ForgetPasswordRequest;
import ehcache.example.ehCache.auth.Dto.RegisterRequest;
import ehcache.example.ehCache.token.VerificationToken;
import ehcache.example.ehCache.validator.ObjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VerificationEmailController {

    private final VerificationTokenRepository tokenRepository;
    private final UserDao userDao;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ObjectValidator<ForgetPasswordRequest> forgetPasswordRequestObjectValidator ;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {

        VerificationToken verificationToken = getValidTokenOrThrow(token);


        return "Email verified successfully! 🎉 Now you can login.";
    }




    private VerificationToken getValidTokenOrThrow(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token."));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Token expired.");
        }
        var user = verificationToken.getUser();
        user.setEnabled(true);
        userDao.save(user);

        tokenRepository.delete(verificationToken);

        return verificationToken;
    }

    @GetMapping("/Email-reset-password")
    public ResponseEntity<String > resetPassword(@RequestParam("token") String token) {

        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token."));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Token expired.");
        }


        return ResponseEntity.ok(

                "Token valid. Now use POST /reset-password with this token + new password."

        ) ;
    }


    //after validate his email we make him change his password :
    @PostMapping("/reset-password")
    public ResponseEntity<String> ChangePassword(
                                                 @RequestBody ForgetPasswordRequest request

    ) {
        forgetPasswordRequestObjectValidator.validate(request);
        VerificationToken verificationToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(" Token expired! Please request a new verification email.");

        }


        User user=verificationToken.getUser() ;

        if (!request.getNewPassword().equals(request.getConfirmationPassword()))
            return ResponseEntity.badRequest().body("Passwords do not match.");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        if (!user.isEnabled()) {
            user.setEnabled(true);
        }


        userDao.save(user);

        tokenRepository.delete(verificationToken);


        return ResponseEntity.ok().body( "Password  changed successfully! 🎉 Now you can login with new Password.");

    }


}
