package ehcache.example.ehCache.exceptions;


import java.util.Set;



public class ObjectNotValidException extends RuntimeException{


    private final Set<String> errors ;


    public ObjectNotValidException(Set<String> errors){

        super(errors.toString());

        this.errors=errors;


    }


    public  Set<String> geterrors(){
        return this.errors;
    }



}
