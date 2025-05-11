package ehcache.example.ehCache.Dto;

import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import ehcache.example.ehCache.CustomeAnnotation.UsernameValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserDto {





    @NotNull
    @NotEmpty
    private String name ;

    @UsernameValidator
    private String username;

    @StrongPassword
    private String password ;

    @Email
    private String email  ;


    private List<Long> bookIds; // just simple references, no full objects





}
