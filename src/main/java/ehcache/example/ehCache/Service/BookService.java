package ehcache.example.ehCache.Service;


import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private Bookdao bookRepository;


    public Iterator<Book> getAllBooks(){

    return bookRepository.findAll().iterator();


    }

    public void InitBook(){

        Book book=Book.builder().edition("hh").id(1).name("hiya").author("who").category("kids").publisher("moi")

                .build();

        bookRepository.save(book);


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

    @Cacheable(cacheNames = "books",key = "#id")
    public Book getBook(long id) {

        System.out.println("fetching from DB");

        return  bookRepository.findById(id).orElseThrow();

    }


    @CacheEvict(cacheNames = "books",key = "#id")
    public String deleteBook(long id) {

        bookRepository.deleteById(id);
        return "Book deleted";
    }



}
