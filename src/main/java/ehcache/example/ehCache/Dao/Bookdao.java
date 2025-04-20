package ehcache.example.ehCache.Dao;

import ehcache.example.ehCache.Entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Bookdao extends CrudRepository<Book,Long>  {


    /*
    @Transactional
    @Modifying
    @Query("update Book u set u.name=name where u.id=id")
    int updateAddress(long id, String name);
*/

    @Query("select b from Book b where b.category=:category")
    Optional<Book> findByCategory(String category);



}
