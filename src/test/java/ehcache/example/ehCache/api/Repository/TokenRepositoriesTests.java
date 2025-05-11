package ehcache.example.ehCache.api.Repository;


import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TokenRepositoriesTests {


    @Autowired
    private TokenDAO tokenDAO;

    @Autowired
    private UserDao adminDao;

    private User user;
    private User user1;


    private  Token token ;
    private  Token token2 ;




    @BeforeEach
    void setUp() {


        user = User.builder().name("waho")
                .username("traw10")
                .email("traw10@gmail.com")
                .role(Role.ADMIN)
                .password("\"T9x!qP4@vZ7#fLba1").build();

        adminDao.save(user);


//Set up first token :

        // Instead of calling jwtService, just set a fake token
        String fakeJwtToken = "fake-jwt-token";

         token = Token.builder()
                .user(user)
                .token(fakeJwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build() ;
        tokenDAO.save(token) ;


 //Set up 2nd token :
        token2 = Token.builder()
                .user(user)
                .token("fakeJwtToken-qsdfmlkj")
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build() ;
        tokenDAO.save(token2) ;





    }

    @Test
    public void TokenDao_FindByToken_returnsToken(){


        //act
        Optional<Token>findToken=tokenDAO.findByToken(token2.getToken()) ;

        //assert:

        Assertions.assertThat(findToken.isPresent()).isTrue() ;

        Assertions.assertThat(findToken.get().getUser().getName()).isEqualTo(user.getName());

        Assertions.assertThat(findToken.get().getUser().getId()).isEqualTo(user.getId());

        Assertions.assertThat(findToken.get().isExpired()).isFalse();
        Assertions.assertThat(findToken.get().isRevoked()).isFalse();


    }


    @Test
    public void TokenDao_findAllValidTokenByUser_returnsValidTokens(){


        //act
        List<Token> validTokens=tokenDAO.findAllValidTokenByUser(user.getId()) ;

        //assert:

                    //check if if works fine :
        Assertions.assertThat(validTokens.size()).isEqualTo(2);
        Assertions.assertThat(validTokens.get(0).getToken().equals(token.getToken()) );

                    //check if expired works
        token.setExpired(true);
        validTokens=tokenDAO.findAllValidTokenByUser(user.getId()) ;
        Assertions.assertThat(validTokens.size()).isEqualTo(1);
        Assertions.assertThat(validTokens.get(0).getToken().equals(token2.getToken()) );

                    //check if revoked works :
        token2.setRevoked(true);
        validTokens=tokenDAO.findAllValidTokenByUser(user.getId()) ;
        Assertions.assertThat(validTokens.size()).isEqualTo(0);


    }


}
