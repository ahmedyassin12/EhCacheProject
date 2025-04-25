package ehcache.example.ehCache.Dao;

import ehcache.example.ehCache.token.Token;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenDAO extends CrudRepository<Token,Long> {


    @Query("""
            select t from Token t inner join Admin a on t.admin.id=a.id
            
            where a.id=:userId and (t.expired=false or  t.revoked=false)
            
            
            """)
    List<Token> findAllValidTokenByUser(Long userId );


    Optional<Token> findByToken(String token ) ;


}
