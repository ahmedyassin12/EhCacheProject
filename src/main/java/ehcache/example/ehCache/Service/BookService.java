package ehcache.example.ehCache.Service;


import ehcache.example.ehCache.Dao.Bookdao;

import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.BookDto;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.mapper.BookMapper;
import ehcache.example.ehCache.validator.ObjectValidator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional  // Default for all methods
public class BookService {

    @Autowired
    private Bookdao bookRepository;

    @Autowired
    private UserDao userDao;

    @Autowired
    private  ObjectValidator< CreateBookDto> Bookvalidator ;

    public void InitBook(){

        Book book=Book.builder().edition("hh").id(1).name("hiya").author("who").category("kids").publisher("moi")

                .build();
        Book book2=Book.builder().edition("king").name("me").author("idk").category("mmm").publisher("moi")

                .build();

        bookRepository.save(book);

        bookRepository.save(book2);


    }

    @CacheEvict(cacheNames = "AllbookDtos", key = "1L")
    public BookDto addBook(CreateBookDto createBookDto) {

       Bookvalidator.validate(createBookDto) ;


        User user = userDao.findById(createBookDto.getUser_Id()).orElseThrow(() -> new EntityNotFoundException("User not found for username: ") ) ;

        Book book = BookMapper.returnBook(createBookDto, user);
         bookRepository.save(book);

         return BookMapper.returnBookDto(book);

    }



    @CacheEvict(cacheNames = "AllbookDtos", key = "1L")
    @CachePut(cacheNames = "bookDtos",key = "#id")
    public BookDto updateBookById(CreateBookDto createBookDto,Long id) {

        Bookvalidator.validate(createBookDto) ;

        Book findBook=bookRepository.findById(id).orElseThrow(() ->new EntityNotFoundException(("Book not found for id : "+id) ) ) ;

        Book updatedBook = BookMapper.returnBook(createBookDto,findBook.getUser()) ;
            updatedBook.setId(findBook.getId());

        Book saved_Book= bookRepository.save(updatedBook);

        return BookMapper.returnBookDto(saved_Book);


    }


    @Transactional(readOnly = true)  // Override for reads
    @Cacheable(cacheNames = "bookDtos",key = "#id")
    public BookDto getBook(long id) {

        System.out.println("fetching from db");

        Book book=bookRepository.findById(id).orElseThrow(() ->new EntityNotFoundException(("Book not found for id : "+id) ) ) ;

        return BookMapper.returnBookDto(book);

    }





    @Transactional(readOnly = true)  // Override for reads
    @Cacheable(cacheNames = "AllbookDtos", key="1L")
    public List<BookDto> getAllBooks(){

        System.out.println("fetching from db");

        Iterable<Book> books=bookRepository.findAll();


        List<BookDto>bookDtos =new ArrayList<>( );
        books.forEach(book ->bookDtos.add(BookMapper.returnBookDto( book  ) )  ) ;


        return bookDtos ;

    }



    @Caching(evict = {
            @CacheEvict(cacheNames = "AllbookDtos", key = "1L"),
            @CacheEvict(cacheNames = "bookDtos", key = "#id")
    })
    public String deleteBook(long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with ID " + id + " not found");
        }

        bookRepository.deleteById(id);
        return "Book deleted";
    }



}
