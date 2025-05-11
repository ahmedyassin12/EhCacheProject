package ehcache.example.ehCache.auth.Dto;

import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class ChangePasswordRequest {


    @StrongPassword
    private String currentPassword ;
    @StrongPassword
    private String newPassword ;

    @StrongPassword
    private String ConfirmationPassword ;




}
