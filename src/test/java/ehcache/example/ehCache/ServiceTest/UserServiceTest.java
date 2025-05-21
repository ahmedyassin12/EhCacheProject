package ehcache.example.ehCache.ServiceTest;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Dao.TokenDAO;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Service.UserService;
import ehcache.example.ehCache.auth.Dto.ChangePasswordRequest;
import ehcache.example.ehCache.mapper.UserMapper;
import ehcache.example.ehCache.token.Token;
import ehcache.example.ehCache.token.TokenType;
import ehcache.example.ehCache.validator.ObjectValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private UserDao userDao;

    @Mock
    private Bookdao bookdao;
    @Mock
    private ObjectValidator<UserDto> UserDtoValidator;
    @Mock
    private ObjectValidator<CreateUserDto> CreateUserDtoValidator;

    @Mock
    private  ObjectValidator<ChangePasswordRequest> changePasswordRequestvalidator ;

    @Mock
    private TokenDAO tokenDAO;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private User user1;
        @BeforeEach
        void setUp() {

             user = User.builder().name("waho").username("traw10").email("qsmdlfkj@smdfq.com").password("mqldsjkfml").role(Role.ADMIN).enabled(true).books(Collections.emptyList()).build() ;
             user1 = User.builder().name("ex").username("rja41").email("qsdmflj@mjni.com").password("smdlfkj").role(Role.ADMIN).enabled(true).books(Collections.emptyList()).build() ;



        }

    @Test
    void UserService_getAllUsers_returnsAllUsersDto() {

        Mockito.when(userDao.findAll()).thenReturn(List.of(user, user1)) ;

       List <UserDto> userDtos = userService.getAllUsers() ;


       Assertions.assertThat(userDtos).isNotNull() ;
        Assertions.assertThat(userDtos.size()).isEqualTo(2) ;
        Assertions.assertThat(userDtos.get(0).getName()).isEqualTo(user.getName()) ;
        Assertions.assertThat(userDtos.get(0).getEmail()).isEqualTo(user.getEmail()) ;

        Assertions.assertThat(userDtos.get(1).getName()).isEqualTo(user1.getName()) ;
        Assertions.assertThat(userDtos.get(1).getEmail()).isEqualTo(user1.getEmail()) ;


    }

    @Test
    void UserService_getUserById_ReturnsUserDto() {


        when(userDao.findById(1l)).thenReturn(Optional.ofNullable(user)) ;


        UserDto userDto = userService.getUserById(1l) ;


        // System.out.println(findBook.getId()+);

        Assertions.assertThat(userDto).isNotNull();
        Assertions.assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(userDto.getName()).isEqualTo(user.getName());



    }

    @Test
    void UserService_getUserByname_ReturnsUserDto() {


        when(userDao.findUserByName("hh")).thenReturn(Optional.ofNullable(user1)) ;


        UserDto userDto = userService.getUserByname("hh") ;



        Assertions.assertThat(userDto).isNotNull();
        Assertions.assertThat(userDto.getEmail()).isEqualTo(user1.getEmail());
        Assertions.assertThat(userDto.getName()).isEqualTo(user1.getName());




    }

    @Test
    void UserService_getUserByUsername_ReturnsUserDto() {

        when(userDao.findUserByUsername("1l")).thenReturn(Optional.ofNullable(user)) ;


        UserDto userDto = userService.getUserByUsername("1l") ;


        // System.out.println(findBook.getId()+);

        Assertions.assertThat(userDto).isNotNull();
        Assertions.assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(userDto.getName()).isEqualTo(user.getName());


    }

    @Test
    void UserService_createNewUser_ReturnsUserDto() {

            when(userDao.save(user)).thenReturn(user) ;

            UserDto userDto = userService.createNewUser(
                    UserMapper.returnCreateUserDto(user)
            );
        Assertions.assertThat(userDto).isNotNull();
        Assertions.assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(userDto.getName()).isEqualTo(user.getName());


    }

    @Test
    void remUser() {

//arrange
        Token mockToken = Token.builder()
                .tokenType(TokenType.BEARER)
                .expired(false)
                .token("valid")
                .revoked(false)
                .user(user)
                .build();

        when(tokenDAO.findAllValidTokenByUser(user.getId()))
                .thenReturn(List.of(mockToken)) ;
        when(cacheManager.getCache("JwtTokens"))
                .thenReturn(cache);
        doNothing().when(userDao).deleteById(user.getId());
            when(userDao.existsById(user.getId())).thenReturn(true );
         //act
        String check= userService.remUser(user.getId());

        //assert :
        verify(tokenDAO, times(1)).findAllValidTokenByUser(user.getId());
        verify(cacheManager, times(1)).getCache("JwtTokens");
        verify(cache, times(1)).remove(mockToken.getToken());
        Assertions.assertThat(check).isEqualTo("User removed");



    }

    @Test
    void UserService_updateUser_returnsUserDto() {

            //arrange
        User updatedUser = User.builder()
                .id(1L)
                .name("New Name")
                .email("newUsername")
                .password("secret")
                .build();
        UserDto updateDto = UserDto.builder()
                .name("New Name")
                .email("newUsername")
                .id(1L)
                .build();

            when(userDao.findById(1l)).thenReturn(Optional.ofNullable(user));
        when(userDao.save(any(User.class))).thenReturn(updatedUser) ;




//act
        UserDto result = userService.updateUser(updateDto) ;


        //assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getEmail()).isEqualTo(updatedUser.getEmail());
        Assertions.assertThat(result.getName()).isEqualTo(updatedUser.getName());



    }

    @Test
    void changePassword_ValidRequest_ShouldUpdatePassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
                "mqldsjkfml", "newPassword", "newPassword"
        );

        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken(user, null);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        // Act
        userService.changePassword(request, principal);

        // Assert
        verify(userDao).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(userCaptor.capture());
        assertEquals("newEncodedPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void changePassword_WrongCurrentPassword_ShouldThrowException() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
                "wrongPassword", "newPassword", "newPassword"
        );
        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken(user, null);

        //   doNothing().when( changePasswordRequestvalidator).validate(request);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword(request, principal);
        });
    }


}