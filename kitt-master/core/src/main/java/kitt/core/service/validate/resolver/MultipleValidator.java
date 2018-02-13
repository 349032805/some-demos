package kitt.core.service.validate.resolver;

import kitt.core.service.validate.constraints.Multiple;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by xiangyang on 15-7-28.
 */
public class MultipleValidator implements ConstraintValidator<Multiple, Integer> {


    private Integer number;

    @Override
    public void initialize(Multiple constraintAnnotation) {
        number = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value != null) {
            return value % number  == 0 ? true : false;
        }
        return false;
    }
}
