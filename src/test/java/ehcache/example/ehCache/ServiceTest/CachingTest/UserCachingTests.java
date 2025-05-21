package ehcache.example.ehCache.ServiceTest.CachingTest;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Service.UserService;
import ehcache.example.ehCache.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import javax.cache.CacheManager;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class UserCachingTests {

    @Autowired
    private UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    private CacheManager cacheManager;

    private Long userId;
    private User user;
    @Autowired
    private Bookdao bookdao;

    @Autowired
    private TokenDAO tokenDAO;

    @BeforeEach
    void setUp() {
        bookdao.deleteAll();
       userDao.deleteAll();
       tokenDAO.deleteAll();
       //bookD
        user = User.builder()
                .role(Role.ADMIN)
                .password("SecureP@ss123")
                .username("userKing")
                .name("Test User")
                .email("user@example.com")
                .books(Collections.emptyList())
                .build();
        userDao.save(user );
        userId = user.getId();

        // Clear cache before each test
        cacheManager.getCache("userDtos").clear();
        cacheManager.getCache("AlluserDtos").clear();
    }


    @Test
    public void UserService_getAllUsersCache_ReturnsAllUsersDto() throws Exception {
        // Add second user
        User user2 = User.builder()
                .password("AnotherSecureP@ss123P@ss")
                .role(Role.ADMIN)
                .username("secondUser")
                .name("Second User")
                .email("user2@example.com")
                .build();
        userService.createNewUser(UserMapper.returnCreateUserDto(user2 ));

        List<UserDto> userDtos = userService.getAllUsers();
        List<UserDto> cachedUsers = (List<UserDto>) cacheManager.getCache("AlluserDtos").get(2L);

        // Verify initial cache state
        Assertions.assertThat(userDtos.size()).isEqualTo(cachedUsers.size());
        assertUserFieldsMatch(userDtos.get(0), cachedUsers.get(0));

        // Add third user and verify cache eviction
        User user3 = User.builder()
                .password("ThirdPSecureP@ss123@ss")
                .username("thirdUser")
                .name("Third User")
                .email("user3@example.com")
                .role(Role.ADMIN)
                .build();
        userService.createNewUser(UserMapper.returnCreateUserDto(user3));

        List<UserDto> updatedList = userService.getAllUsers();
        List<UserDto> updatedCache = (List<UserDto>) cacheManager.getCache("AlluserDtos").get(2L);

        Assertions.assertThat(updatedList.size()).isEqualTo(updatedCache.size()).isEqualTo(3);
        assertUserFieldsMatch(updatedList.get(2), updatedCache.get(2));

        List<UserDto> deletedList=userService.getAllUsers();
        Long i ;
        for (UserDto dto : deletedList) {
            userService.remUser(dto.getId());
        }



    }





    @Test
    public void UserService_UpdateUserCache_returnsUserDto() {
        // Get initial user
        UserDto initialDto = userService.getUserById(userId);
        UserDto cachedDto = (UserDto) cacheManager.getCache("userDtos").get(userId);

        // Verify initial cache state
        assertUserFieldsMatch(initialDto, cachedDto);

        // Update user
        initialDto.setName("Updated Name");
        initialDto.setEmail("updated@example.com");
        UserDto updatedDto = userService.updateUser( initialDto);

        // Verify updated cache
        UserDto updatedCache = (UserDto) cacheManager.getCache("userDtos").get(userId);
        assertUserFieldsMatch(updatedDto, updatedCache);

        // Verify Allusers cache update
        List<UserDto> allUsers = userService.getAllUsers();
        Assertions.assertThat(allUsers.get( 0).getName()).isEqualTo("Updated Name");
        Assertions.assertThat(allUsers.get(0).getEmail()).isEqualTo("updated@example.com");
        List<UserDto> allUsersCache = (List<UserDto>) cacheManager.getCache("AlluserDtos").get(2L);
        assertUserFieldsMatch(allUsers.get(0), allUsersCache.get(0));

        //
        List<UserDto> deletedList=userService.getAllUsers();
        Long i ;
        for (UserDto dto : deletedList) {
            userService.remUser(dto.getId());
        }

    }











    @Test
    public void UserService_GetUserByIdCache_returnUserDto() {
        UserDto userDto = userService.getUserById(userId);
        UserDto cachedDto = (UserDto) cacheManager.getCache("userDtos").get(userId);

        Assertions.assertThat(cachedDto).isNotNull();
        assertUserFieldsMatch(userDto, cachedDto);

    }




   @Test
    public void UserService_DeleteUserCache_returnNull() {
        // Prime cache
        userService.getUserById(userId);

        // Delete user
        userService.remUser(userId);

        List<UserDto> userDto=userService.getAllUsers();
        // Verify cache clearance
        assertThat(cacheManager.getCache("userDtos").get(userId)).isNull();

        // Verify Allusers cache update
       assertThat(cacheManager.getCache("AlluserDtos").get(2L)).asList().isEmpty();


   }

    private void assertUserFieldsMatch(UserDto actual, UserDto expected) {
        Assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        Assertions.assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
    }



}