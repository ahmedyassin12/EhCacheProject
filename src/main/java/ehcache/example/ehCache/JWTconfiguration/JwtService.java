package ehcache.example.ehCache.JWTconfiguration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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


    private static final String SECRET_KEY="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" ;



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
    public String generateToken(
            Map<String, Object> extraClaims,
                       UserDetails userDetails
                                ){

        extraClaims.put("authorities", userDetails.getAuthorities()); // Use authorities directly

        return  builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact() ;



    }

    public  boolean IsTokenValid(String token , UserDetails userDetails){

        final String username=extractUserName(token) ;

        return (username.equals(userDetails.getUsername())&&!IsTokenExpired(token))  ;

    }

    //change
    private boolean IsTokenExpired(String token) {


        return  extractExpiration(token).before(new Date()) ;


    }

    private Date extractExpiration(String token) {

        System.out.println("currentTimeMillis date ="+new Date(System.currentTimeMillis()));
        System.out.println("expiration Date = " +extractClaim(token,Claims::getExpiration));
        return extractClaim(token,Claims::getExpiration) ;


    }

    //to change :
    private Claims extractAllClaims(String token){

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();


    }

    //make sure our JWT won't change through the way  :

    public SecretKey getSigningKey() {


        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes) ;

    }




}




