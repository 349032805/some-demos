package kitt.core.service.validate.constraints;

import kitt.core.service.validate.resolver.DatePastValidator;
import kitt.core.service.validate.resolver.MultipleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by xiangyang on 15-8-3.
 * 说明: 倍数,e.g:需求吨数必须是50的倍数
 */
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = {MultipleValidator.class})
@Documented
public @interface Multiple {

    //默认错误消息
    String message() default "必须是整倍数";

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};

    int value();

}
