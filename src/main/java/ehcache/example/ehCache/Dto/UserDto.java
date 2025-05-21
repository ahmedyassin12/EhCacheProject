package ehcache.example.ehCache.Dto;

import ehcache.example.ehCache.CustomeAnnotation.UsernameValidator;
import ehcache.example.ehCache.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {


    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Long id;

    @Email
    private String email;

    @UsernameValidator
    private String username;






}
