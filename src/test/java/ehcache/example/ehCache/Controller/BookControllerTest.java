package ehcache.example.ehCache.Controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = Book.builder()
                .id(1)
                .name("Book One")
                .author("Author A")
                .category("Fiction")
                .publisher("Pub A")
                .edition("1st")
                .build();

        book2 = Book.builder()
                .id(2)
                .name("Book Two")
                .author("Author B")
                .category("History")
                .publisher("Pub B")
                .edition("2nd")
                .build();
    }


    @Test
    void BookController_getAllBooks_ReturnsBookList() throws Exception {
        //arrange
        when(bookService.getAllBooks()).thenReturn(List.of(book1, book2));

        MvcResult mvcResult = mockMvc.perform(get("/getAllBooks"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<Book> responseBooks = objectMapper.readValue(jsonResponse, new TypeReference<List<Book>>() {});

        //assert
        assertThat(responseBooks).hasSize(2);
        assertThat(responseBooks.get(0).getName()).isEqualTo(book1.getName());
        assertThat(responseBooks.get(0).getId()).isEqualTo(book1.getId());
        assertThat(responseBooks.get(0).getCategory()).isEqualTo(book1.getCategory());
        assertThat(responseBooks.get(0).getPublisher()).isEqualTo(book1.getPublisher());

        assertThat(responseBooks.get(1).getName()).isEqualTo(book2.getName());
        assertThat(responseBooks.get(1).getId()).isEqualTo(book2.getId());
        assertThat(responseBooks.get(1).getCategory()).isEqualTo(book2.getCategory());
        assertThat(responseBooks.get(1).getPublisher()).isEqualTo(book2.getPublisher());
    }
    @Test
    void BookController_addBook_ReturnsSavedBook() throws Exception {
        when(bookService.addBook(any(Book.class))).thenReturn(book1);

        mockMvc.perform(post("/addbook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(book1.getName()));

    }

    @Test
    void BookController_updateBook_ReturnsUpdatedBook() throws Exception {
        when(bookService.updateBook(any(Book.class))).thenReturn(book2);

        mockMvc.perform(put("/updateBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(book2.getName()));
    }

    @Test
    void BookController_getBookById_ReturnsBook() throws Exception {
        when(bookService.getBook(1L)).thenReturn(book1);

        mockMvc.perform(get("/getBook/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(book1.getName()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()));
    }

    @Test
    void BookController_deleteBook_ReturnsConfirmation() throws Exception {
        when(bookService.deleteBook(1L)).thenReturn("Book deleted");

        mockMvc.perform(delete("/deleteBook/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted"));
    }


}