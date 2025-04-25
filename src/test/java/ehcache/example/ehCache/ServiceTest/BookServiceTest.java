package ehcache.example.ehCache.ServiceTest;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.BookService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

public class BookServiceTest {

    @Mock
    private Bookdao bookdao ;

    @InjectMocks
    private BookService bookService ;



    @Test
    public void BookService_AddBook_returnBook(){

        Book book=Book.builder().author("king")
                .category("kids").edition("idk").publisher("5altek")
                .build();

        when(bookdao.save(Mockito.any(Book.class))).thenReturn(book) ;


        Book savedBook=bookService.addBook(book) ;

        Assertions.assertThat(savedBook).isNotNull();
        Assertions.assertThat(savedBook.getId()).isEqualTo(book.getId());
        Assertions.assertThat(savedBook.getName()).isEqualTo(book.getName());




    }




    @Test
    public void BookService_GetBookById_returnBook(){

        Long IdBook=1l ;
        Book book=Book.builder().id(IdBook).author("king")
                .category("kids").edition("idk").publisher("ltek")
                .build();
            when(bookdao.findById(IdBook)).thenReturn(Optional.of(book)) ;


        Book findBook=bookService.getBook(IdBook) ;
       // System.out.println(findBook.getId()+);

        Assertions.assertThat(findBook).isNotNull();
        Assertions.assertThat(findBook.getId()).isEqualTo(IdBook);



    }

    @Test
    public void BookService_updateBookById_returnBook(){

        Long IdBook=1l ;
        Book book=Book.builder().id(1l).author("king")
                .category("kids").edition("idk").publisher("ltek")
                .build();
        when(bookdao.findById(IdBook)).thenReturn(Optional.ofNullable(book)) ;

        Book findBook=bookService.getBook(IdBook) ;

        when(bookdao.save(Mockito.any(Book.class))).thenReturn(findBook) ;

        findBook.setAuthor("haloo");
        findBook.setName("the Trao");

        Book Updated_Book=bookService.updateBookById(findBook,IdBook);

        // System.out.println(findBook.getId()+);

        Assertions.assertThat(Updated_Book).isNotNull();
        Assertions.assertThat(Updated_Book.getAuthor()).isEqualTo("haloo");
        Assertions.assertThat(Updated_Book.getName()).isEqualTo("the Trao");

    }




@Test
public void BookService_GetAllBooks_returnBooks(){



    Book book1=Book.builder().id(1).author("king").name("hhh")
            .category("kids").edition("idk").publisher("altek")
            .build();

    Book book2=Book.builder().id(2).author("o2k")
            .category("+18").edition("nam9hb").publisher("3amabozaka")
            .build();

List<Book> books=new ArrayList<>();
books.add(book1);
books.add(book2);

    when(bookdao.findAll()).thenReturn(books) ;


    List<Book> book_List= bookService.getAllBooks();




    Assertions.assertThat(book_List).size().isEqualTo(2) ;
    Assertions.assertThat(book_List.get(0).getName()).isEqualTo(book1.getName()) ;
    Assertions.assertThat(book_List.get(1).getName()).isEqualTo(book2.getName()) ;
    Assertions.assertThat(book_List.get(0).getId()).isEqualTo(book1.getId()) ;
    Assertions.assertThat(book_List.get(1).getId()).isEqualTo(book2.getId()) ;


}


    @Test
    public void BookService_DeleteBookById_returnNull(){

        //arrange
        Long idBook=1l ;
        Book book=Book.builder().id(1l).author("king")
                .category("kids").edition("idk").publisher("5altek")
                .build();


        doNothing().when(bookdao).deleteById(idBook);

        // Act
      String check=  bookService.deleteBook(idBook);

        // Assert: check if deleteById was called

Assertions.assertThat(check).isEqualTo("Book deleted") ;



    }






}