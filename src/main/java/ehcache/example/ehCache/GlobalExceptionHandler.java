package ehcache.example.ehCache;


import ehcache.example.ehCache.exceptions.ObjectNotValidException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {




    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?>handleException(IllegalStateException e){

return ResponseEntity
        .badRequest()
        .body(e.getMessage());


    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?>handleException(EntityNotFoundException e){

        return ResponseEntity
                .notFound()
                .build() ;


    }
    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?>handleException(ObjectNotValidException e){

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());


    }

}
