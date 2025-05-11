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



    //create AdminDto Object with the object Admin
    public static UserDto returnUserDto(User admin) {
        return UserDto.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .username(admin.getUsername())
                .build();


    }

    public static CreateUserDto returnCreateUserDto(User admin) {
        return CreateUserDto.builder()
                .bookIds(Collections.emptyList())
                .email(admin.getEmail())
                .name(admin.getName())
                .password(admin.getPassword())
                .username(admin.getUsername())
                .build();


    }




}
