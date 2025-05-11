package ehcache.example.ehCache.Service;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.mapper.UserMapper;
import ehcache.example.ehCache.validator.ObjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


//Use DTOs inorder to make my Inputs and Outputs Safe

@Service
@Transactional  // Default for all methods
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private  PasswordEncoder passwordEncoder ;
    @Autowired
    private Bookdao bookdao;

    @Autowired
    private ObjectValidator<CreateUserDto> Uservalidator ;

    @Autowired
    private ObjectValidator<UserDto> UserDtovalidator ;

    @Transactional(readOnly = true)  // Override for reads
    @Cacheable(cacheNames ="AlluserDtos", key ="2L")
    public List<UserDto> getAllUsers() {


        System.out.println("fetching from db");

        Iterable<User> users= userDao.findAll();

        List<UserDto>userDtos=new ArrayList<>();

        users.forEach(user -> userDtos.add(UserMapper.returnUserDto(user)));

        return userDtos ;



    }



    @Transactional(readOnly = true)  // Override for reads
    @Cacheable(cacheNames = "userDtos",key = "#id")
    public UserDto getUserById(Long id) {

        System.out.println("fetching from db");
        User user= userDao.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));

        UserDto userDto= UserMapper.returnUserDto(user);

         return userDto;

    }

    public void InitAdmin(){

        User admin = User.builder().password(passwordEncoder.encode("T9x!qP4@vZ7#fLba123"))
                .username("holahola123").name("hiya").id(1l) .email("fqmsldk@gmail.com")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userDao.save(admin);



    }


    @Transactional(readOnly = true)  // Override for reads
    public UserDto getUserByname(String name) {

        User user= userDao.findUserByName(name).orElseThrow(() -> new EntityNotFoundException("User not found for name: " + name));

        return UserMapper.returnUserDto(user);

    }

    @Transactional(readOnly = true)  // Override for reads
    public UserDto getUserByUsername(String username) {

        User user= userDao.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found for username: " + username));

        return UserMapper.returnUserDto(user) ;

    }

    @CacheEvict(cacheNames = "AlluserDtos", key = "2L")
    public UserDto createNewUser(CreateUserDto createUserDto) {


        Uservalidator.validate(createUserDto) ;

        Iterable<Book> books=bookdao.findAllById(createUserDto.getBookIds());

        User user= UserMapper.returnUser(createUserDto, (List<Book>) books);
        user.setEnabled(true);
        user.setRole(Role.ADMIN);
        user.setEnabled(true);
        userDao.save(user);


        return UserMapper.returnUserDto(user);



    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "AlluserDtos", key = "2L"),
            @CacheEvict(cacheNames = "userDtos",key = "#id")
    })
    public String remUser(Long id) {

        if (!userDao.existsById(id)) {
            throw new EntityNotFoundException("user with ID " + id + " not found");
        }
        userDao.deleteById(id) ;

        return "User removed" ;

    }





    //this updates only name and username
    @CacheEvict(cacheNames = "AlluserDtos", key = "2L")
    @CachePut(cacheNames = "userDtos",key = "#id")
    public UserDto updateUser(Long id , UserDto userDto) {

        UserDtovalidator.validate(userDto) ;

        User user= userDao.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));


        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

         userDao.save(user);

         return UserMapper.returnUserDto(user) ;

    }






}
