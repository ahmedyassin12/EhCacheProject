package ehcache.example.ehCache.ServiceTest;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.BookDto;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Service.BookService;
import ehcache.example.ehCache.mapper.BookMapper;
import ehcache.example.ehCache.validator.ObjectValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)

public class BookServiceTest {

    @Mock
    private Bookdao bookRepository;

    @Mock
    private UserDao userDao ;

    @Mock
    private ObjectValidator<CreateBookDto> bookValidator; // Add this mock
     // Add this mock

    @InjectMocks
    private BookService bookService ;


    private User user ;
    private CreateBookDto createBookDto;

    @BeforeEach
    void setUp() {

         user= User.builder().password("hohoho")
                .username("dqsmlfj").name("hiya").id(1l) .email("fqmsldk@gmail.com")
                .role(Role.ADMIN)
                .enabled(true)
                .build() ;


         createBookDto= CreateBookDto.builder().author("king")
                .category("kids")
                .edition("idk")
                .publisher("tek")
                .category("fathiza")
                .user_Id(1l)
                 .name("broo")
                .build();

    }



    @Test
    public void BookService_AddBook_returnBook(){

        // Mock validation to pass (no errors)
       doNothing().when(bookValidator).validate(createBookDto);

        when(userDao.findById(1l)).thenReturn(Optional.of(user)) ;
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(BookMapper.returnBook(createBookDto,user)) ;


        BookDto savedBook=bookService.addBook(createBookDto) ;

        Assertions.assertThat(savedBook).isNotNull();
        Assertions.assertThat(savedBook.getName()).isEqualTo(createBookDto.getName());
        Assertions.assertThat(savedBook.getPublisher()).isEqualTo(createBookDto.getPublisher());
        Assertions.assertThat(savedBook.getPublisher()).isEqualTo(createBookDto.getPublisher());




    }




    @Test
    public void BookService_GetBookById_returnBookDto(){

        Long IdBook=1l ;

        Book book = BookMapper.returnBook(createBookDto,user) ;
        when(bookRepository.findById(IdBook)).thenReturn(Optional.of(book)) ;




        BookDto findBook=bookService.getBook(IdBook) ;
       // System.out.println(findBook.getId()+);

        Assertions.assertThat(findBook).isNotNull();
        Assertions.assertThat(findBook.getPublisher()).isEqualTo(book.getPublisher());
        Assertions.assertThat(findBook.getEdition()).isEqualTo(book.getEdition());
        Assertions.assertThat(findBook.getName()).isEqualTo(book.getName());



    }

    @Test
    public void BookService_updateBookById_returnBook(){

        doNothing().when(bookValidator).validate(createBookDto);

        Long IdBook=1l ;

        Book book = BookMapper.returnBook(createBookDto,user) ;



        Book Updated_Book=BookMapper.returnBook(createBookDto,user);
Updated_Book.setAuthor("hohoho");
Updated_Book.setName("KInghong");
Updated_Book.setId(IdBook);

        when(bookRepository.findById(IdBook)).thenReturn(Optional.of(book)) ;
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(Updated_Book) ;

        createBookDto.setCategory("");

        BookDto result=bookService.updateBookById(createBookDto,IdBook);



        // System.out.println(findBook.getId()+);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("KInghong");

       Assertions.assertThat(result.getName()).isNotEqualTo(book.getName());
        Assertions.assertThat(result.getAuthor()).isNotEqualTo(book.getAuthor());

    }




@Test
public void BookService_GetAllBooks_returnBookDtos(){



    Book book1=Book.builder().id(1).author("king").name("hhh")
            .category("kids").edition("idk").publisher("altek")
            .user(user)
            .build();

    Book book2=Book.builder().id(2).author("o2k")
            .category("+18").edition("nam9b").publisher("3amabozaka")
            .user(user)
            .build();

List<Book> books=new ArrayList<>();
books.add(book1);
books.add(book2);


    when( bookRepository.findAll()).thenReturn( books );


    List<BookDto>bookDtos = bookService.getAllBooks() ;




    Assertions.assertThat(bookDtos).size().isEqualTo(2) ;
    Assertions.assertThat(bookDtos.get(0).getName()).isEqualTo(book1.getName()) ;
    Assertions.assertThat(bookDtos.get(1).getName()).isEqualTo(book2.getName()) ;
    Assertions.assertThat(bookDtos.get(0).getCategory()).isEqualTo(book1.getCategory()) ;
    Assertions.assertThat(bookDtos.get(1).getEdition()).isEqualTo(book2.getEdition()) ;


}



    @Test
    public void BookService_DeleteBookById_returnNull(){

        //arrange
        Long idBook=1l ;
        Book book=Book.builder().id(1l).author("king")
                .category("kids").edition("idk").publisher("5altek")
                .build();


        doNothing().when(bookRepository).deleteById(idBook);
        when(bookRepository.existsById(book.getId())).thenReturn(true );

        // Act
      String check=  bookService.deleteBook(idBook);

        // Assert: check if deleteById was called

Assertions.assertThat(check).isEqualTo("Book deleted") ;



    }





}