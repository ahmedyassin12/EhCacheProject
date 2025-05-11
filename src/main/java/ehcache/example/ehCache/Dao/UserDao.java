package ehcache.example.ehCache.Dao;

import ehcache.example.ehCache.Entity.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends CrudRepository<User,Long> {


    Optional<User> findUserByName(String name);


    Optional<User> findUserByUsername(String username);



    User findUserByEmail(@Email String email);


}
