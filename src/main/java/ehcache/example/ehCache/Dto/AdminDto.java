package ehcache.example.ehCache.Dto;

import ehcache.example.ehCache.Entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {



    private String name ;


    @UsernameValidator()
    private String username;


    //create AdminDto Object with the object Admin
    public static AdminDto returnAdminDto(Admin admin) {
        return AdminDto.builder()
                .name(admin.getName())
                .username(admin.getUsername())
                .build();
    }




}
