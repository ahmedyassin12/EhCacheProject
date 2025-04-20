package ehcache.example.ehCache.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Book implements Serializable{

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;

        private String name;
        private String category;
        private String author;
        private String publisher;
        private String edition;







}
