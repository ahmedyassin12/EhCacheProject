package ehcache.example.ehCache.auth.Dto;

import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import ehcache.example.ehCache.CustomeAnnotation.UsernameValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {


    @UsernameValidator
    private String username ;

    @StrongPassword
    private String password ;


}
