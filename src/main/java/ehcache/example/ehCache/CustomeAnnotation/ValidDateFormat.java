package ehcache.example.ehCache.CustomeAnnotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFormat {
    String message() default "Invalid date format. Use dd-MM-yyyy";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
