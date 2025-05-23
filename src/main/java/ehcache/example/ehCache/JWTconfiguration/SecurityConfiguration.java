package ehcache.example.ehCache.JWTconfiguration;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.sound.midi.Patch;

import static ehcache.example.ehCache.Entity.Role.ADMIN;
import static ehcache.example.ehCache.Entity.Role.SUPER_ADMIN;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {



    private final JwtAuthenticationFilter jwtAuthFilter;


    private final AuthenticationProvider authenticationProvider;


    private final LogoutHandler logoutHandler ;


    private static final String[] WHITE_LIST_URL ={
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html","/api/v1/**","https://ehcacheproject.onrender.com/**"    } ;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http ) throws Exception {


    http


            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req->


                           req.requestMatchers (
                                 WHITE_LIST_URL

                                         )
                         .permitAll()


                                 //our Secured EndPoint

                                 .requestMatchers(GET,"/api/C1/Book/**").hasAnyRole(ADMIN.name(),
                                         SUPER_ADMIN.name())

                                 .requestMatchers(POST,"/api/C1/Book/**").hasAnyRole(ADMIN.name(),
                                         SUPER_ADMIN.name())

                                 .requestMatchers(DELETE,"/api/C1/Book/**").hasAnyRole(ADMIN.name(),
                                         SUPER_ADMIN.name())

                                 .requestMatchers(PUT,"/api/C1/Book/**").hasAnyRole(ADMIN.name(),
                                         SUPER_ADMIN.name())


                                 .requestMatchers(GET, "/api/C2/user/**").hasAnyRole(ADMIN.name(),
                                         SUPER_ADMIN.name())

                                 .requestMatchers(POST, "/api/C2/user/**").hasRole(SUPER_ADMIN.name())

                                 .requestMatchers(DELETE, "/api/C2/user/**").hasRole(SUPER_ADMIN.name())

                                 .requestMatchers(PUT, "/api/C2/user/**").hasAnyRole(ADMIN.name()
                                         ,SUPER_ADMIN.name())

                                   .requestMatchers(PATCH,"/api/C2/user/changePassword" ).hasAnyRole(SUPER_ADMIN.name(),
                                   ADMIN.name()
                                   )

                 .anyRequest()
                 .authenticated()

            )



          .sessionManagement(session -> session.sessionCreationPolicy(STATELESS) )

            .authenticationProvider(authenticationProvider)

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

      .logout(

              logout->
            logout.logoutUrl("/api/v1/auth/logout")
                    .addLogoutHandler(logoutHandler)
        .logoutSuccessHandler(
                (request,response,authentication)->
        SecurityContextHolder.clearContext()

                )
      )
        ;

    return http.build() ;

}


}
