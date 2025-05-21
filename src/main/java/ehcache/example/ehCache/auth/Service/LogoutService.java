package ehcache.example.ehCache.auth.Service;

import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.JWTconfiguration.JwtService;
import ehcache.example.ehCache.Service.TokenService;
import ehcache.example.ehCache.Service.UserService;
import ehcache.example.ehCache.token.Token;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.cache.CacheManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {


    private final JwtService jwtService ;

    private final TokenDAO tokenDAO;
    private final TokenService tokenService;

    private final UserService userService ;
    private final CacheManager cacheManager;

    //@CacheEvict(cacheNames = "JwtTokens", key ="#request.getHeader('Authorization').substring(7)")
    @Transactional
    @Override
    public void logout(
            HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication
    ) {

        final String authHeader=request.getHeader("Authorization") ;
        final String jwt  ;


        if(authHeader==null||!authHeader.startsWith("Bearer ")  ){

            return  ;

        }

        jwt=authHeader.substring(7) ;
        var storedToken=tokenDAO.findByToken(jwt);



        try{
             String username = jwtService.extractUserName(jwt);

            UserDto user = userService.getUserByUsername(username);

            List<Token> tokens = tokenDAO.findAllValidTokenByUser(user.getId()) ;

            tokens.forEach(t->cacheManager.getCache("JwtTokens").remove( t.getToken() ) );


            tokenDAO.revokeAllTokensByUser(user.getId());

        }
        catch (EntityNotFoundException e){
             return;
        }
        catch (NullPointerException e){
            return;
        }




    }

}
