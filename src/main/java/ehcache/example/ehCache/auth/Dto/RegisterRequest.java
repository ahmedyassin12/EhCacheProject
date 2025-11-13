package ehcache.example.ehCache.auth.Dto;

import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import ehcache.example.ehCache.CustomeAnnotation.UsernameValidator;
import ehcache.example.ehCache.Entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {



    @NotEmpty
    private String name;

    @UsernameValidator
    private String username;

    @StrongPassword
    private String password ;

    @Email
    private String email;

    @NotNull
    private Role role;



}
