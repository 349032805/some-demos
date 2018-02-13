package kitt.core.service.validate.constraints;

import kitt.core.service.validate.resolver.DeliveryDateMatcherValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Created by xiangyang on 15-7-28.
 */
@Target({ TYPE,FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = {DeliveryDateMatcherValidator.class})
@Documented
public @interface DeliveryDateMatcher {
    //默认错误消息
    String message() default "开始时间不能大于结束时间";

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};
    //开始时间字段名
    String  startDate();
    //结束时间字段名
    String  endDate();




}
