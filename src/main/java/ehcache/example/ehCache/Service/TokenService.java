package ehcache.example.ehCache.Service;

import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.token.Token;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class TokenService {




    @Autowired
    private TokenDAO tokenDAO ;


    @Cacheable(cacheNames = "JwtTokens", key ="#token")
    public Token getToken(String token){


        System.out.println("fetching from database hohoho ");

        Token token1= tokenDAO.findByToken(token).orElseThrow
                (() ->new EntityNotFoundException(("Token not found for token :  " +token) ) ) ;


        return token1 ;





    }







}
