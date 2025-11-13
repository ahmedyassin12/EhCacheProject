package ehcache.example.ehCache.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBookDto {



    @NotEmpty
    private String name;

    @NotEmpty
    private String category;

    @NotEmpty
    private String author;

    @NotEmpty
    private String publisher;

    @NotEmpty
    private String edition;

    @NotNull
    private Long user_Id ;






}
