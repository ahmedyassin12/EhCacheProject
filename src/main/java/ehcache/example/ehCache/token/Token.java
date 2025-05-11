package ehcache.example.ehCache.token;

import ehcache.example.ehCache.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Token {


    @Id
    @GeneratedValue
    private Long id ;

    @Column(name = "token", nullable = false, length = 1024)
    private String token ;

    @Enumerated(EnumType.STRING)
        private TokenType tokenType ;

    private boolean expired ;   

    private boolean revoked ;


    @ManyToOne
    @JoinColumn(name="User_id")
    private User user;


    }
