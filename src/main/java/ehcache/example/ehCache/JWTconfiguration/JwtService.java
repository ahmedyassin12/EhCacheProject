package ehcache.example.ehCache.JWTconfiguration;

import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Service.TokenService;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;

@Service
public class JwtService {


    private  final String secretKey="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private final long accessTokenExpiration= 120000 ;   // 86400000; //1 day in ms

    private final   long refreshTokenExpiration=604800000;//7 days in ms

    @Autowired
private TokenDAO tokenDAO ;
    @Autowired
    private TokenService tokenService;

    public String extractUserName(String token) {



        return extractClaim(token,Claims::getSubject) ;




    }

    public <T> T extractClaim(String token, Function<Claims,T>ClaimResolver){

        final  Claims claims= extractAllClaims(token)  ;

        return ClaimResolver.apply(claims) ;


    }

    public String generateToken(UserDetails userDetails){

        return generateToken(new HashMap<>(),userDetails) ;


    }
    public String generateRefreshToken(
            UserDetails userDetails
    ){

        Map<String, Object> extraClaims=new HashMap<>() ;


        extraClaims.put("typ", "refresh"); // or "access"

        return BuildToken(extraClaims,userDetails,refreshTokenExpiration) ;


    }


    public String generateToken(
            Map<String, Object> extraClaims,
                       UserDetails userDetails
                                ){
        extraClaims.put("typ", "access");

        extraClaims.put("authorities", userDetails.getAuthorities()); // Use authorities directly

        return BuildToken(extraClaims,userDetails,accessTokenExpiration) ;

    }

    private String BuildToken(Map<String, Object> extraClaims ,UserDetails userDetails,long expiration ){

        return  builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact() ;




    }

    public  boolean IsTokenValid(String token , UserDetails userDetails){

        final String username=extractUserName(token) ;

        return (username.equals(userDetails.getUsername())&&!IsTokenExpired(token))  ;

    }


    @CacheEvict(cacheNames = "JwtTokens", key = "#token")
    public void evictTokenFromCache(String token) {
        // Empty method: @CacheEvict annotation handles the eviction.

    }


    //change
    private boolean IsTokenExpired(String token) {


        boolean isTokenExpired =extractExpiration(token).before(new Date()) ;



        return  isTokenExpired ;


    }




    private Date extractExpiration(String token) {


        return extractClaim(token,Claims::getExpiration) ;


    }

    //to change :
    public Claims extractAllClaims(String token){




        Claims claims= Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();


        return claims ;

    }

    //make sure our JWT won't change through the way  :

    public SecretKey getSigningKey() {


        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes) ;

    }




}




