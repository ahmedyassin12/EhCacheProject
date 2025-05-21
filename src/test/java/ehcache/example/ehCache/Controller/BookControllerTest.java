package ehcache.example.ehCache.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ehcache.example.ehCache.Dto.BookDto;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import ehcache.example.ehCache.Service.BookService;
import ehcache.example.ehCache.mapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = "spring.security.enabled=false")
@AutoConfigureMockMvc(addFilters = false)


public class BookControllerTest {

    private static final String BASE_URL = "/api/C1/Book";

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private CreateBookDto createBookDto;
    private CreateBookDto createBookDto1;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().password("hohohoA#123")
                .username("dqsmlfj").name("hiya").id(1l).email("fqmsldk@gmail.com")
                .role(Role.ADMIN)
                .enabled(true)
                .build();


        createBookDto = CreateBookDto.builder()
                .name("Book One")
                .author("Author A")
                .edition("first edition")
                .publisher("qsdfmlkj")
                .category("qmsflkj")
                .user_Id(user.getId())
                .build();

        createBookDto1 = CreateBookDto.builder()
                .name("Book Two")
                .author("Author B")
                .publisher("trao")
                .edition("first edition")
                .user_Id(user.getId())
                .build();
    }


    @Test
    public void BookController_addBook_ReturnsSavedBook() throws Exception {


        when(bookService.addBook(any(CreateBookDto.class))).thenReturn(BookMapper
                .returnBookDto(BookMapper.returnBook(createBookDto, user)));
        mockMvc.perform(post(BASE_URL + "/addbook")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookDto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Book One"))
                .andExpect(jsonPath("$.author").value("Author A"));


    }





    @Test
    public void getAllBooks_ReturnsBookList() throws Exception {

        BookDto bookDto1 = BookMapper.returnBookDto(BookMapper.returnBook(createBookDto, user));

        BookDto bookDto2 = BookMapper.returnBookDto(BookMapper.returnBook(createBookDto1, user));

        when(bookService.getAllBooks()).thenReturn(List.of(bookDto1, bookDto2));

        mockMvc.perform(get(BASE_URL + "/getAllBooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(createBookDto.getName()))
                .andExpect(jsonPath("$[0].author").value(createBookDto.getAuthor()))
                .andExpect(jsonPath("$[0].edition").value(createBookDto.getEdition()))

                .andExpect(jsonPath("$[1].name").value(createBookDto1.getName()))
                .andExpect(jsonPath("$[1].author").value(createBookDto1.getAuthor()))
                .andExpect(jsonPath("$[1].edition").value(createBookDto1.getEdition()))

                .andReturn();


    }


    @Test
    void updateBook_ReturnsUpdatedBook() throws Exception {
        BookDto bookDto1 = BookMapper.returnBookDto(BookMapper.returnBook(createBookDto, user));


        when(bookService.updateBookById(any( CreateBookDto.class ),eq(bookDto1.getId()))).thenReturn(bookDto1);

        mockMvc.perform(put(BASE_URL + "/updateBook/"+bookDto1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookDto1.getId()))
                .andExpect(jsonPath("$.name").value(bookDto1.getName()));
    }




    @Test
    void getBookById_ReturnsBook() throws Exception {
        BookDto bookDto1 = BookMapper.returnBookDto(BookMapper.returnBook(createBookDto, user));

        when(bookService.getBook(anyLong())).thenReturn(bookDto1);

        mockMvc.perform(get(BASE_URL + "/getBook/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookDto1.getId()))
                .andExpect(jsonPath("$.name").value(bookDto1.getName()));
    }


    @Test
    void deleteBook_ReturnsConfirmation() throws Exception {
        when(bookService.deleteBook(anyLong())).thenReturn("Book deleted");

        mockMvc.perform(delete(BASE_URL + "/deleteBook/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted"));

    }

}





