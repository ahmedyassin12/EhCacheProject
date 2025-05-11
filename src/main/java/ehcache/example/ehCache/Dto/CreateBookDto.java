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



    @NotNull
    @NotEmpty
    private String name;

    @NotEmpty
    @NotNull
    private String category;

    @NotEmpty
    @NotNull
    private String author;

    @NotEmpty
    @NotNull
    private String publisher;

    @NotEmpty
    @NotNull
    private String edition;

    @NotNull
    private Long user_Id ;





}
