package ehcache.example.ehCache;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@SpringBootApplication
@EnableCaching

public class EhCacheApplication  {//implements CommandLineRunner




	public static void main(String[] args) {
		SpringApplication.run(EhCacheApplication.class, args);

		System.out.printf("hello babiess");





	}



}





