package ehcache.example.ehCache.validator;

import ehcache.example.ehCache.exceptions.ObjectNotValidException;
import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectValidator<T> {


    private final ValidatorFactory factory= Validation.buildDefaultValidatorFactory();

    private final Validator validator= factory.getValidator() ;



    public void validate( T objectToValidate ){


        Set< ConstraintViolation<T> > violations=validator.validate(objectToValidate) ;

        if(!violations.isEmpty()){

          Set<String> message=  violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet()) ;

                           throw new ObjectNotValidException(message) ;

        }







    }












}
