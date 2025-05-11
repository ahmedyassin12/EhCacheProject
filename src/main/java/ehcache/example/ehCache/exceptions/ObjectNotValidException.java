package ehcache.example.ehCache.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class ObjectNotValidException extends RuntimeException{


    private final Set<String> errors ;


}
