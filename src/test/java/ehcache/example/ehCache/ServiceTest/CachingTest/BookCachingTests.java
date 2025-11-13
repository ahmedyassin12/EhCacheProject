
package ehcache.example.ehCache.ServiceTest.CachingTest;

import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Dao.UserDao;
import ehcache.example.ehCache.Dto.BookDto;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Service.BookService;
import ehcache.example.ehCache.mapper.BookMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import javax.cache.CacheManager;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BookCachingTests {


    @Autowired
    private Bookdao bookRepository;

    @Autowired
    BookService bookService ;
    @Autowired
    private CacheManager cacheManager;


    private Long IdBook;
    private Book book;

    private User user;
    private CreateBookDto createBookDto;
    @Autowired
    private UserDao userDao;


    @BeforeEach
    void setUp() {

        IdBook=1l;
        user= User.builder().password("T9x!qP4@vZ7#fLba1")
                .username("qaasdfmjbbbj").name("hiya").id(1l) .email("fqmsldk@gmail.com")
                .role(Role.ADMIN)

                .enabled(true)
                .build() ;
        userDao.save(user);


        createBookDto= CreateBookDto.builder()
                .author("king")
                .edition("idk")
                .publisher("tek")
                .category("fathiza")
                .user_Id(1l)
                .name("broo")
                .build();

        bookService.addBook(createBookDto);
        // Clear cache before each test
        cacheManager.getCache("bookDtos").clear();
        cacheManager.getCache("AllbookDtos").clear();


    }



    @Test
    public void ComparingCachingVsUncaching(){

        bookService.getAllBooks(); // since first call from DB


        //here we mesure average of the speed of our method "getAllBooks"
        long total = 0;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            bookService.getAllBooks(); // or endpoint call
            long end = System.nanoTime();
            total += (end - start);
        }
        System.out.println("Average time: " + (total / 10) / 1_000_000 + " ms");


    }

    @Test
    public void BookService_getAllBookDtosCache_RetrurnsAllBookDtos()throws Exception {

        List <BookDto> bookDtos =bookService.getAllBooks() ;


        List<BookDto> bookDtosFromCache =(List<BookDto>) cacheManager.getCache("AllbookDtos").get(1L);




        Assertions.assertThat(bookDtos.size()).
                isEqualTo(bookDtosFromCache.size());

        //assert that the first index has same value in both Lists :
        Assertions.assertThat(bookDtos.get(0).getName()).isEqualTo(bookDtosFromCache.get(0).getName());
        Assertions.assertThat(bookDtos.get(0).getCategory()).isEqualTo(bookDtosFromCache.get(0).getCategory());
        Assertions.assertThat(bookDtos.get(0).getEdition()).isEqualTo(bookDtosFromCache.get(0).getEdition());
        Assertions.assertThat(bookDtos.get(0).getPublisher()).isEqualTo(bookDtosFromCache.get(0).getPublisher());
        Assertions.assertThat(bookDtos.get(0).getAuthor()).isEqualTo(bookDtosFromCache.get(0).getAuthor());




        //now add another book to see if Its Evicted in our method :
        CreateBookDto  createBookDto1=CreateBookDto.builder().name("woha")
                .publisher("aida").category("animals").author("7iho").edition("scnd").user_Id(user.getId())
                .build();
        bookService.addBook(createBookDto1 );

        List<BookDto> book_check_Eviction=bookService.getAllBooks() ;
        List<BookDto> cachedList = (List<BookDto>) cacheManager.getCache("AllbookDtos").get(1L);

        Assertions.assertThat(book_check_Eviction.size()).isEqualTo(cachedList.size()) .isEqualTo(2);

        Assertions.assertThat(book_check_Eviction.get(0).getEdition()).isEqualTo(cachedList.get(0).getEdition());





    }





        @Test
        public void BookService_UpdateBookDtoCache_returnsBookDto(){


            //Continue Arrangement


            //Act 1 : testing Update Cache on our getBook method :
            BookDto bookDto1 =bookService.getBook(IdBook);

            BookDto bookDtoFromCache =(BookDto) cacheManager.getCache("bookDtos").get(IdBook);

            Assertions.assertThat(bookDtoFromCache.getName()).isEqualTo(bookDto1.getName()) ;
            Assertions.assertThat(bookDtoFromCache.getAuthor()).isEqualTo(bookDto1.getAuthor()) ;

            bookDto1.setName("kawther");
            bookDto1.setAuthor("lecringe");

            CreateBookDto createBookDto = BookMapper.returnCreateBookDto(bookDto1,user.getId()); // Debug here if needed
            BookDto updatedBookDto = bookService.updateBookById(createBookDto, IdBook); // Now debug this line
           BookDto bookDto2FromCache =(BookDto) cacheManager.getCache("bookDtos").get(IdBook);

            //Assert
            Assertions.assertThat(updatedBookDto.getName()).isEqualTo("kawther") ;
            Assertions.assertThat(updatedBookDto.getAuthor()).isEqualTo("lecringe") ;

            Assertions.assertThat(updatedBookDto.getName()).isEqualTo(bookDto2FromCache.getName()) ;
            Assertions.assertThat(updatedBookDto.getAuthor()).isEqualTo(bookDto2FromCache.getAuthor()) ;

            //Act 2 : testing Update Cache on our getAllBooks method :

            //inorder to create cache after evicting it

            bookService.getAllBooks() ;
      List<BookDto> bookDtosFromCache =(List<BookDto>) cacheManager.getCache("AllbookDtos").get(1L);


      Assertions.assertThat(bookDtosFromCache.get(0).getName()).isEqualTo(updatedBookDto.getName());
            Assertions.assertThat(bookDtosFromCache.get(0).getAuthor()).isEqualTo(updatedBookDto.getAuthor());

        }

            @Test
            public void BookService_GetBookDtoByIDCache_returnBookDto(){

                //Arrange


                //Act
                BookDto book1=bookService.getBook(IdBook);

                BookDto bookDtoFromCache =(BookDto) cacheManager.getCache("bookDtos").get(IdBook);

                //Assert


                Assertions.assertThat(bookDtoFromCache).isNotNull() ;
                Assertions.assertThat(book1.toString()).isEqualTo(bookDtoFromCache.toString());




            }


    @Test
    public void BookService_DeleteBookDtoCache_returnNull(){

        // Setup

        // Prime cache
        bookService.getBook(IdBook);

        // Verify deletion
        bookService.deleteBook(IdBook);

        // Verify cache "booDtos" was cleared
        assertThat(cacheManager.getCache("bookDtos").get(IdBook)).isNull();

        //verify chache "allbookDtos" was cleared

        assertThat(cacheManager.getCache("AllbookDtos").get(1L)).isNull();

    }



}


