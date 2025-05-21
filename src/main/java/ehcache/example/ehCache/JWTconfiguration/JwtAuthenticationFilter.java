package ehcache.example.ehCache.JWTconfiguration;

import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Service.TokenService;
import ehcache.example.ehCache.exceptions.ObjectNotValidException;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService ;
    private final UserDetailsService userDetailsService ;
    private final TokenDAO tokenDAO;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain)
            throws ServletException, IOException {


        if (request.getServletPath().startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }



        final String authHeader=request.getHeader("Authorization") ;
        final String jwt  ;
        final String Username;


        if(authHeader==null|| !authHeader.startsWith("Bearer ")  ){

            //Skip JWT logic and pass the request down the chain.
            filterChain.doFilter(request,response);
            return ;

        }


        jwt=authHeader.substring(7) ;

        Token token = tokenService.getToken(authHeader.substring(7) );

        if( token.getTokenType()!=TokenType.BEARER)     {


            filterChain.doFilter(request,response);
            return;
        }






        Username=jwtService.extractUserName(jwt) ;


        if(     Username!=null

                && SecurityContextHolder.getContext().getAuthentication()==null

        ){


            UserDetails userDetails =this.userDetailsService.loadUserByUsername(Username) ;

            boolean isTokenValid;
            if (token.isRevoked()==true) isTokenValid=false ;

            else isTokenValid=true;


            if(jwtService.IsTokenValid(jwt,userDetails) && isTokenValid) {



                //needed inorder to update our SecurityContextHolder
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()




                );

                authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)

                );

                SecurityContextHolder.getContext().setAuthentication(authToken);


            }

            else {

                throw new ObjectNotValidException(Set.of( "Token is not valid !!  "));


            }




        }

        System.out.println("yes we are here ");

        filterChain.doFilter(request,response);


    }





}
