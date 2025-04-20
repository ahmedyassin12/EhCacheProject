package ehcache.example.ehCache.ServiceTest.CachingTest;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.BookService;
import org.assertj.core.api.Assertions;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.cache.CacheManager;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookCachingTests {


    @MockBean
    private Bookdao bookdao;

    @Autowired
    BookService bookService ;
    @Autowired
    private CacheManager cacheManager;


    private Long IdBook;
    private Book book;



    @BeforeEach
    void setUp() {
         IdBook=1l ;
         book=Book.builder().id(IdBook).author("king")
                .category("kids").edition("idk").publisher("ltek")
                .build();
        when(bookdao.findById(IdBook)).thenReturn(Optional.ofNullable(book));

        // Clear cache before each test
        cacheManager.getCache("books").clear();
    }



    @Test
    public void BookService_UpdateBookCache_returnBook(){


        //Continue Arrangement

        when(bookdao.save(Mockito.any(Book.class))).thenReturn(book) ;

        //Act
        Book book1=bookService.getBook(IdBook);

        book1.setName("kawther");
        book1.setAuthor("lecringe");

        Book updatedBook=bookService.updateBook(book1);

        Book book2=bookService.getBook(IdBook);

        //Assert
        verify(bookdao, times(1)).findById(IdBook);
        Assertions.assertThat(updatedBook.getName()).isEqualTo(book2.getName()) ;
        Assertions.assertThat(updatedBook.getAuthor()).isEqualTo(book2.getAuthor()) ;




    }


    @Test
    public void BookService_GetBookByIDCache_returnBook(){

        //Arrange

        when(bookdao.findById(IdBook)).thenReturn(Optional.ofNullable(book));

        //Act
        Book book1=bookService.getBook(IdBook);
        Book book2=bookService.getBook(IdBook);

        //Assert

        verify(bookdao, times(1)).findById(IdBook);

        Assertions.assertThat(book1).isEqualTo(book2);




    }

    @Test
    public void BookService_DeleteBookCache_returnNull(){

        // Setup
        when(bookdao.findById(IdBook)).thenReturn(Optional.of(book));

        // Prime cache
        bookService.getBook(IdBook);

        // Verify deletion
        bookService.deleteBook(IdBook);
        verify(bookdao).deleteById(IdBook);

        // Verify cache was cleared
        assertThat(cacheManager.getCache("books").get(IdBook)).isNull();

        // Subsequent call should hit DB again
        bookService.getBook(IdBook);
        verify(bookdao, times(2)).findById(IdBook);




    }



}
