package ehcache.example.ehCache.CacheConfig;

import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Entity.Admin;
import ehcache.example.ehCache.Entity.Book;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ch.qos.logback.classic.spi.ThrowableProxyVO.build;
import static org.ehcache.expiry.ExpiryPolicy.*;


    @Configuration
    @EnableCaching
    public class CacheConfig {

        @Bean
        public CacheManager ehCacheManager() {
            CachingProvider provider = Caching.getCachingProvider();
            CacheManager cacheManager = provider.getCacheManager();

            // Cache for Book class
            CacheConfiguration<Long, Book> Bookconfiguration =
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            Long.class,   // Key type
                           // String.class,
                            Book.class,  // Value type
                            ResourcePoolsBuilder.heap(1000) // Max entries in heap

                    ).build(); //

            CacheConfiguration<Long, AdminDto> Adminconfiguration =
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            Long.class,   // Key type
                            AdminDto.class,  // Value type
                            ResourcePoolsBuilder.heap(1000) // Max entries in heap

                    ).build();

            CacheConfiguration<Long, List > GetAllAdminconfiguration =
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                             Long.class , // Key type
                            List.class,  // Value type
                            ResourcePoolsBuilder.heap(1000) // Max entries in heap

                    ).build();
            CacheConfiguration<Long, List > GetAllBookconfiguration =
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            Long.class , // Key type
                            List.class,  // Value type
                            ResourcePoolsBuilder.heap(1000) // Max entries in heap

                    ).build();
            cacheManager.createCache("books", // e to match @Cacheable
                    Eh107Configuration.fromEhcacheCacheConfiguration(Bookconfiguration));

            cacheManager.createCache("admins",
                    Eh107Configuration.fromEhcacheCacheConfiguration(Adminconfiguration)
            );

            cacheManager.createCache("Alladmins",
                    Eh107Configuration.fromEhcacheCacheConfiguration(GetAllAdminconfiguration)
            );
            cacheManager.createCache("Allbooks",
                    Eh107Configuration.fromEhcacheCacheConfiguration(GetAllBookconfiguration)
            );
            return cacheManager;
        }
    }
