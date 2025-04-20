package ehcache.example.ehCache.Controller;

import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.BookService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/addbook")
    public Book addBook(@RequestBody Book book){
        return bookService.addBook(book);
    }

    @PutMapping("/updateBook")
    public Book updateBook(@RequestBody Book book) {
        return bookService.updateBook(book);
    }

    @GetMapping("/getBook/{id}")
    public Book getBook(@PathVariable long id){

        return bookService.getBook(id);
    }

    @DeleteMapping("/deleteBook/{id}")
    public String deleteBook(@PathVariable long id) {
        return bookService.deleteBook(id);
    }


    @PostConstruct
    public void init(){

        bookService.InitBook();



    }
}
