    package ehcache.example.ehCache.CustomeAnnotation;
    import jakarta.validation.ConstraintValidator;
    import jakarta.validation.ConstraintValidatorContext;
    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;

    public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, String> {



        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            String pattern="dd-MM-yyyy" ;

            if (value == null) return false; // or false, depending on your needs
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate.parse(value, formatter);
                return true;
            } catch (Exception e) {
                return false;
            }
        }




    }












