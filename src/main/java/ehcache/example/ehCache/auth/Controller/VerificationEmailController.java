package ehcache.example.ehCache.auth.Controller;


import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dao.VerificationTokenRepository;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.token.VerificationToken;
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
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken = getValidTokenOrThrow(token);



        return "Email verified successfully! 🎉 Now you can login.";
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> validateResetToken(@RequestParam("token") String token) {
        getValidTokenOrThrow(token);

        return ResponseEntity.ok("Token valid. You can now set a new password.");
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




    //after validate his email we make him change his password :
    @PostMapping("/reset-password")
    public ResponseEntity<String> ChangePassword(@RequestParam("token")String token,
                                                 @RequestParam @StrongPassword String newPassword,
                                                 @RequestParam @StrongPassword String ConfirmationPassword

    ) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(" Token expired! Please request a new verification email.");

        }


        User user=verificationToken.getUser() ;

        if (!newPassword.equals(ConfirmationPassword))
            return ResponseEntity.badRequest().body("Passwords do not match.");

        user.setPassword(passwordEncoder.encode(newPassword));

        if (!user.isEnabled()) {
            user.setEnabled(true);
        }


        userDao.save(user);

        tokenRepository.delete(verificationToken);


        return ResponseEntity.badRequest().body( "Password  changed successfully! 🎉 Now you can login with new Password.");

    }


}
