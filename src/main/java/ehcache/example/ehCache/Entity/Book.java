package ehcache.example.ehCache.Entity;

import jakarta.persistence.*;
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


        @ManyToOne
        @JoinColumn(name="user_id")

        private User user;






}
