package ehcache.example.ehCache.Entity;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.CustomeAnnotation.StrongPassword;
import ehcache.example.ehCache.CustomeAnnotation.UsernameValidator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Table(name = "users")
public class User implements UserDetails {





    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id ;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name ;

    @Column(name="username",nullable = false,unique = true)
    private String username;

    @Column(name="password",nullable = false)
    private String password ;

    @Column(name="email",nullable = false,unique = true)
    @Email
    private String email;



    @Column(name = "reset_token")
    private String resetToken;

    @NotNull
    @Column(name = "role",nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;



    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Book> books=new ArrayList<>() ;


    //Enable Default = false till our User Verified
    private boolean enabled = false;






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
