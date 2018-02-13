package kitt.core.service.validate.resolver;

import kitt.core.service.validate.constraints.DateFuture;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by xiangyang on 15-7-28.
 */
public class DateFutureValidator implements ConstraintValidator<DateFuture,Object> {

    @Override
    public void initialize(DateFuture constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value instanceof LocalDate){
            return ((LocalDate) value).isAfter(LocalDate.now());
        }
        if(value instanceof LocalDateTime){
            return ((LocalDateTime) value).isAfter(LocalDateTime.now());
        }
        return false;
    }
}
