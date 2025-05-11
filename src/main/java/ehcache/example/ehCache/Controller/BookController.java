package ehcache.example.ehCache.Controller;

import ehcache.example.ehCache.Dto.BookDto;
import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.util.List;


@RequestMapping("/api/C1/Book")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/getAllBooks")
    public ResponseEntity< List<BookDto> > getAllBooks(){

        return  new ResponseEntity<>(bookService.getAllBooks(),HttpStatus.OK);

    }
    @PostMapping("/addbook")

    public ResponseEntity <?> addBook(@RequestBody  CreateBookDto createBookDto ){



        return new ResponseEntity<>(bookService.addBook(createBookDto), HttpStatus.OK );
    }

    @PutMapping("/updateBook/{IdBook}")
    public ResponseEntity< BookDto>  updateBook(@RequestBody  CreateBookDto createBookDto, @PathVariable Long IdBook) {
        return new ResponseEntity<>(bookService.updateBookById(createBookDto,IdBook),HttpStatus.OK) ;
    }

    @GetMapping("/getBook/{id}")
    public ResponseEntity <BookDto> getBook(@PathVariable long id){

        return new ResponseEntity<>(bookService.getBook(id),HttpStatus.OK );
    }

    @DeleteMapping("/deleteBook/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable long id) {
        return new ResponseEntity<>(bookService.deleteBook(id),HttpStatus.OK);

    }

/*
    @PostConstruct
    public void init(){

        bookService.InitBook();



    }*/
}
