package kitt.core.service.validate.constraints;

import kitt.core.service.validate.resolver.DateFutureValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by xiangyang on 15-7-28.
 * 说明：验证注解的元素值（日期类型）比当前时间晚
 */
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = {DateFutureValidator.class})
@Documented
public @interface DateFuture {
    //默认错误消息
    String message() default "{此字段值不能比当前时间早}";

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};
}
