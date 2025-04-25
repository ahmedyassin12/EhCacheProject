package ehcache.example.ehCache.auth;

import ehcache.example.ehCache.Dto.StrongPassword;
import ehcache.example.ehCache.Dto.UsernameValidator;
import ehcache.example.ehCache.Entity.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {



    private String name;

    @UsernameValidator
    private String username;
    @StrongPassword
    private String password ;

    @Email
    private String email;
    private Role role;



}
