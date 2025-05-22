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
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);

		// ✅ ALLOW localhost (add Render domain in deployment)
		corsConfiguration.setAllowedOrigins(Arrays.asList(
				"http://localhost:9099",
				"https://your-app-name.onrender.com" // Add your Render domain here
		));

		// ✅ Minimal but sufficient allowed headers
		corsConfiguration.setAllowedHeaders(Arrays.asList(
				"Origin", "Content-Type", "Accept", "Authorization"
		));

		// ✅ Expose only headers that client needs
		corsConfiguration.setExposedHeaders(Arrays.asList(
				"Authorization", "Content-Type"
		));

		// ✅ Common HTTP methods
		corsConfiguration.setAllowedMethods(Arrays.asList(
				"GET", "POST", "PUT", "DELETE", "OPTIONS"
		));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}



}





