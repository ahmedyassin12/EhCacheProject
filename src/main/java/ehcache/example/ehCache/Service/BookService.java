package ehcache.example.ehCache.Service;


import ehcache.example.ehCache.Dao.Bookdao;

import ehcache.example.ehCache.Entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional  // Default for all methods

public class BookService {
    @Autowired
    private Bookdao bookRepository;
    @Autowired
    private Bookdao bookdao;


    public void InitBook(){

        Book book=Book.builder().edition("hh").id(1).name("hiya").author("who").category("kids").publisher("moi")

                .build();
        Book book2=Book.builder().edition("king").name("me").author("idk").category("mmm").publisher("moi")

                .build();

        bookRepository.save(book);

        bookRepository.save(book2);


    }

    public Book addBook(Book book) {
        bookRepository.save(book);
        return book;
    }

      @CachePut(cacheNames = "books",key = "#book.id")
    public Book updateBook(Book book) {
        bookRepository.save(book);
        return book;
    }

    public Book updateBookById(Book book,Long id) {
        Optional<Book> savedBook=bookRepository.findById(id);
        bookRepository.save(savedBook.get());
        return savedBook.get();
    }

    @Transactional(readOnly = true)  // Override for reads
    @Cacheable(cacheNames = "books",key = "#id")
    public Book getBook(long id) {

        System.out.println("fetching from DB");

        return  bookRepository.findById(id).orElseThrow();

    }

    @Transactional(readOnly = true)  // Override for reads
    @Cacheable(cacheNames = "Allbooks", key="1L")
    public List<Book> getAllBooks(){
        Iterable<Book> books=bookdao.findAll();

        List<Book>bookList=new ArrayList<>();

        books.forEach(book -> bookList.add(book));

        return bookList ;


    }



    @CacheEvict(cacheNames = "books",key = "#id")
    public String deleteBook(long id) {

        bookRepository.deleteById(id);
        return "Book deleted";
    }



}
