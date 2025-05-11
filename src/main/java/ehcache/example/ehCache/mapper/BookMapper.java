package ehcache.example.ehCache.mapper;

import ehcache.example.ehCache.Dto.BookDto;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Entity.User;

import java.util.List;

public class BookMapper {

    public static Book returnBook(CreateBookDto createBookDto , User user) {

        return Book.builder()
                .name(createBookDto.getName())
                .author(createBookDto.getAuthor())

                .edition(createBookDto.getEdition())
                .publisher(createBookDto.getPublisher())
                .category(createBookDto.getCategory())
                .user(user)
                .build();

    }


    public static BookDto returnBookDto(Book book) {

      return   BookDto.builder()
              .id(book.getId())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .name(book.getName())
                .edition(book.getEdition())
                .category(book.getCategory())
                .build()
         ;

    }

    public static CreateBookDto returnCreateBookDto(BookDto bookDto,Long userId) {

        return   CreateBookDto.builder()
                .publisher(bookDto.getPublisher())
                .name(bookDto.getName())
                .edition(bookDto.getEdition())
                .author(bookDto.getAuthor())
                .category(bookDto.getCategory())
                .user_Id(userId)
                .build()
                ;

    }
}
