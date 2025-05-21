package ehcache.example.ehCache.ServiceTest.CachingTest;


import com.jayway.jsonpath.spi.cache.Cache;
import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Service.TokenService;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import net.bytebuddy.pool.TypePool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.cache.CacheManager;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TokenServiceTest {


    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TokenDAO tokenDAO ;

    @Autowired
    private TokenService tokenService ;

    @Autowired
    private UserDao userDao ;
    private User user ;


    @BeforeEach
    void setUp() {

        user= User.builder().password("hohoho")
                .username("dqsmlfj").name("hiya").id(1l) .email("fqmsldk@gmail.com")
                .role(Role.ADMIN)
                .enabled(true)
                .books(Collections.emptyList())
                .build() ;


        userDao.save(user);

    }

    @Test
    public void whenGetTokenCalledTwiceWithSameToken_thenCacheResult() {
        String validToken = "validToken123";


        Token mockToken = Token.builder()
                .tokenType(TokenType.BEARER)
                .expired(false)
                .token(validToken)
                .revoked(false)
                .user(user)
                .build();

        tokenDAO.save(mockToken);

        // First call (should hit the database)
        Token result1 = tokenService.getToken(validToken);
        assertThat(result1.getToken()).isEqualTo(mockToken.getToken());

        // Check cache content
        Token cache = (Token) cacheManager.getCache("JwtTokens").get(validToken);


        assertThat(cache).isNotNull();

        assertThat(cache.getToken()).isEqualTo(mockToken.getToken());


    }

}