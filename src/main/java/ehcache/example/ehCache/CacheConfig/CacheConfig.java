    package ehcache.example.ehCache.CacheConfig;

    import ehcache.example.ehCache.Dto.BookDto;
    import ehcache.example.ehCache.Dto.UserDto;
    import org.ehcache.config.CacheConfiguration;
    import org.ehcache.config.builders.CacheConfigurationBuilder;
    import org.ehcache.config.builders.ResourcePoolsBuilder;
    import org.ehcache.jsr107.Eh107Configuration;
    import org.springframework.cache.annotation.EnableCaching;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    import javax.cache.CacheManager;
    import javax.cache.Caching;
    import javax.cache.spi.CachingProvider;
    import java.util.List;


    @Configuration
        @EnableCaching
        public class CacheConfig {

            @Bean
            public CacheManager ehCacheManager() {
                CachingProvider provider = Caching.getCachingProvider();
                CacheManager cacheManager = provider.getCacheManager();

                // Cache for Book class
                CacheConfiguration<Long, BookDto> Bookconfiguration =
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Long.class,   // Key type
                               // String.class,
                                BookDto.class,  // Value type
                                ResourcePoolsBuilder.heap(100) // Max entries in heap

                        ).build(); //

                CacheConfiguration<Long, UserDto> Userconfiguration =
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Long.class,   // Key type
                                UserDto.class,  // Value type
                                ResourcePoolsBuilder.heap(100) // Max entries in heap

                        ).build();

                CacheConfiguration<Long, List > GetAllUserconfiguration =
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                 Long.class , // Key type
                                List.class,  // Value type
                                ResourcePoolsBuilder.heap(100) // Max entries in heap

                        ).build();
                CacheConfiguration<Long, List > GetAllBookconfiguration =
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Long.class , // Key type
                                List.class,  // Value type
                                ResourcePoolsBuilder.heap(1000) // Max entries in heap

                        ).build();

                if (cacheManager.getCache("bookDtos", Long.class, BookDto.class) == null) {
                    cacheManager.createCache("bookDtos", // e to match @Cacheable
                            Eh107Configuration.fromEhcacheCacheConfiguration(Bookconfiguration));

                }

                if (cacheManager.getCache("userDtos", Long.class, UserDto.class) == null) {

                    cacheManager.createCache("userDtos",
                            Eh107Configuration.fromEhcacheCacheConfiguration(Userconfiguration)
                    );

                }
                if (cacheManager.getCache("AlluserDtos", Long.class, List.class) == null) {

                    cacheManager.createCache("AlluserDtos",
                            Eh107Configuration.fromEhcacheCacheConfiguration(GetAllUserconfiguration)
                    );
                }
                if (cacheManager.getCache("AllbookDtos", Long.class, List.class) == null) {

                    cacheManager.createCache("AllbookDtos",
                            Eh107Configuration.fromEhcacheCacheConfiguration(GetAllBookconfiguration)
                    );
                }

                return cacheManager;

            }
        }
