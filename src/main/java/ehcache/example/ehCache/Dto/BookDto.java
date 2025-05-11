package ehcache.example.ehCache.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDto {


    private String name;

    private Long id ;

    private String category;

    private String author;

    private String publisher;

    private String edition;






}
