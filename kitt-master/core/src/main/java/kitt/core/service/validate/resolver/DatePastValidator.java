package kitt.core.service.validate.resolver;

import kitt.core.service.validate.constraints.DatePast;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by xiangyang on 15-7-28.
 */
public class DatePastValidator implements ConstraintValidator<DatePast,Object> {

    @Override
    public void initialize(DatePast constraintAnnotation) {}

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value instanceof LocalDate){
            return ((LocalDate) value).isBefore(LocalDate.now());
        }
        if(value instanceof LocalDateTime){
            return ((LocalDateTime) value).isBefore(LocalDateTime.now());
        }
        return false;
    }
}
