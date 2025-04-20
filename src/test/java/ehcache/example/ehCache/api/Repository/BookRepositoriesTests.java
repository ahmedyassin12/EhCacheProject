package ehcache.example.ehCache.api.Repository;


import ehcache.example.ehCache.Dao.Bookdao;
import ehcache.example.ehCache.Entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BookRepositoriesTests {


    @Autowired
    private Bookdao bookdao ;


    @Test

    public void BookDao_savebook_returnSavedBook(){

        //Arrange

        Book book=Book.builder().id(1).author("king")
                .category("kids").edition("idk").publisher("5altek")
                .build();



        //Act

       Book savedBook= bookdao.save(book) ;


        //Assert

        Assertions.assertThat(savedBook).isNotNull() ;

        Assertions.assertThat(savedBook.getId()).isGreaterThan(0) ;

       // Assertions.assertThat(savedBook.getName()).isNotNull() ;


    }


    @Test
    public void BookDao_showAllBooks_returnAllBooks(){

        //Arrange


        Book book1=Book.builder().id(1).author("king").name("hhh")
                .category("kids").edition("idk").publisher("altek")
                .build();

        Book book2=Book.builder().id(2).author("o2k")
                .category("+18").edition("nam9hb").publisher("3amabozaka")
                .build();


        //Act

        bookdao.save(book1);
        bookdao.save(book2);

        Iterable <Book>books =bookdao.findAll() ;

        List <Book> book_List= StreamSupport.stream(books.spliterator(),false)
                .collect(Collectors.toList());


        //Assert


        Assertions.assertThat(book_List).isNotNull() ;

        Assertions.assertThat(book_List.size()).isEqualTo(2);
        Assertions.assertThat(book_List.get(0).getId()).isEqualTo(book1.getId())  ;
        Assertions.assertThat(book_List.get(0).getName()).isEqualTo(book1.getName());

        Assertions.assertThat(book_List.get(1).getId()).isEqualTo(book2.getId())  ;
        Assertions.assertThat(book_List.get(1).getName()).isEqualTo(book2.getName());


    }





    @Test
    public void BookDao_findBookById_returnBook(){


        // Arrange
        Book book1=Book.builder().id(1).author("king").name("hhh")
                .category("kids").edition("idk").publisher("3altek")
                .build();
        bookdao.save(book1);

        //Act

        Optional<Book> find_book=bookdao.findById(book1.getId()) ;



        //Assert

        Assertions.assertThat(find_book).isNotNull();
        Assertions.assertThat(find_book.get().getId()).isEqualTo(book1.getId() ) ;


    }

    @Test
    public void BookDao_findBookByCategory_returnBook(){


        // Arrange
        Book book1=Book.builder().id(1).author("king").name("hhh")
                .category("kids").edition("idk").publisher("tek")
                .build();
        bookdao.save(book1);

        //Act

        Optional<Book> find_book=bookdao.findByCategory(book1.getCategory()) ;



        //Assert

        Assertions.assertThat(find_book).isNotNull();
        Assertions.assertThat(find_book.get().getCategory()).isEqualTo(book1.getCategory() ) ;


    }


    @Test
    public void BookDao_UpdateBook_returnBook(){


        // Arrange
        Book book1=Book.builder().id(1).author("king").name("hhh")
                .category("kids").edition("idk").publisher("5ek")
                .build();
        bookdao.save(book1);

        //Act

        book1.setAuthor("karakouz");
        Book updated_book=bookdao.save(book1) ;



        //Assert

        Assertions.assertThat(updated_book.getAuthor()).isNotNull();
        Assertions.assertThat(updated_book.getAuthor()).isEqualTo("karakouz" ) ;


    }
    @Test
    public void BookDao_DeleteBook_returnBookIsEmpty(){


        // Arrange
        Book book1=Book.builder().id(1).author("king").name("hhh")
                .category("kids").edition("idk").publisher("5altek")
                .build();
        bookdao.save(book1);

        //Act

        bookdao.deleteById(book1.getId()); ;



        //Assert

        Assertions.assertThat(bookdao.findById(book1.getId())).isEmpty();


    }




}
