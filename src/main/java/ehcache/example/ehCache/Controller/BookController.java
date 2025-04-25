package ehcache.example.ehCache.Controller;

import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.BookService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;

import java.util.List;


@RequestMapping("/api/C1/Book")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/getAllBooks")
    public ResponseEntity< List<Book> > getAllBooks(){

        return  new ResponseEntity<>(bookService.getAllBooks(),HttpStatus.OK);

    }
    @PostMapping("/addbook")
    public ResponseEntity <Book> addBook(@RequestBody @Valid Book book){
        return new ResponseEntity<>(bookService.addBook(book), HttpStatus.OK );
    }

    @PutMapping("/updateBook")
    public ResponseEntity< Book>  updateBook(@RequestBody @Valid Book book) {
        return new ResponseEntity<>(bookService.updateBook(book),HttpStatus.OK) ;
    }

    @GetMapping("/getBook/{id}")
    public ResponseEntity <Book> getBook(@PathVariable long id){

        return new ResponseEntity<>(bookService.getBook(id),HttpStatus.OK );
    }

    @DeleteMapping("/deleteBook/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable long id) {
        return new ResponseEntity<>(bookService.deleteBook(id),HttpStatus.OK);

    }


    @PostConstruct
    public void init(){

        bookService.InitBook();



    }
}
