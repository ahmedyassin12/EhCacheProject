package ehcache.example.ehCache.mapper;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

public class UserMapper {



    //create user Object with the object createUserDto
    public static User returnUser(CreateUserDto createUserDto ,List<Book> books) {

        return User.builder()
                .name(createUserDto.getName())
                .username(createUserDto.getUsername())
                .password(createUserDto.getPassword())
                .email(createUserDto.getEmail())
                .books(books)
                .build();

    }



    //create UserDto Object with the object User
    public static UserDto returnUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();


    }

    public static CreateUserDto returnCreateUserDto(User user) {
        return CreateUserDto.builder()
                .bookIds(Collections.emptyList())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .username(user.getUsername())
                .build();


    }




}
