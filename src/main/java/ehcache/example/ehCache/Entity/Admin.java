package ehcache.example.ehCache.Entity;


import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Dto.CreateAdminDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Admin {





    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id ;

    @Column(name = "name", nullable = false)
    private String name ;

    @Column(name="username"/*,nullable = false,unique = true*/)
    private String username;

    @Column(name="password"/*,nullable = false*/)
    private String password ;


    //create Admin Object with the object createAdminDto
    public static Admin returnAdmin(CreateAdminDto createAdminDto) {
        return Admin.builder()
                .name(createAdminDto.getName())
                .username(createAdminDto.getUsername())
                .password(createAdminDto.getPassword())
                .build();
    }


}
