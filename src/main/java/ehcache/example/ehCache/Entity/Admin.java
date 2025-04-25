package ehcache.example.ehCache.Entity;
import ehcache.example.ehCache.Dto.CreateAdminDto;
import ehcache.example.ehCache.Dto.StrongPassword;
import ehcache.example.ehCache.Dto.UsernameValidator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Admin implements UserDetails {





    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id ;

    @Column(name = "name", nullable = false)
    private String name ;

    @UsernameValidator
    @Column(name="username"/*,nullable = false,unique = true*/)
    private String username;

    @StrongPassword
    @Column(name="password"/*,nullable = false*/)
    private String password ;

    @Column(name="email",nullable = false)
    @Email
    private String email;

    private String verificationToken;

    private boolean IsVerified;

    @Column(name = "reset_token")
    private String resetToken;



    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "admin")
    private List<Book> books ;


    private boolean enabled = true;



    //create Admin Object with the object createAdminDto
    public static Admin returnAdmin(CreateAdminDto createAdminDto) {
        return Admin.builder()
                .name(createAdminDto.getName())
                .username(createAdminDto.getUsername())
                .password(createAdminDto.getPassword())
                .build();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }







}
