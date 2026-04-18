package ehcache.example.ehCache.auth.Dto;


import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ForgetPasswordRequest {

    @NotEmpty
   private String token;

    @StrongPassword
    private String newPassword ;

    @StrongPassword
    private String ConfirmationPassword ;

}
