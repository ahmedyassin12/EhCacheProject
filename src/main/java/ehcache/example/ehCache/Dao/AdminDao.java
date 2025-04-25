package ehcache.example.ehCache.Dao;

import ehcache.example.ehCache.Entity.Admin;
import jakarta.validation.constraints.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminDao extends CrudRepository<Admin,Long> {


    Optional<Admin> findAdminByName(String name);


    Optional<Admin> findAdminByUsername(String username);



    Admin findAdminByEmail(@Email String email);


}
