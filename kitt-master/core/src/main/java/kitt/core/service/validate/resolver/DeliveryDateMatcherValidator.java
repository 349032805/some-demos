package kitt.core.service.validate.resolver;


import kitt.core.service.validate.constraints.DeliveryDateMatcher;
import kitt.core.util.ReflectionsUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Created by xiangyang on 15-7-28.
 * 说明：交货时间:开始时间必须大于当前时间，截止时间必须大于开始时间
 */

public class DeliveryDateMatcherValidator implements ConstraintValidator<DeliveryDateMatcher,Object> {

    private String startDateField;
    private String endDateField;


    //得到当前的注解信息
    @Override
    public void initialize(DeliveryDateMatcher constraintAnnotation) {
        //获取字段名
        this.startDateField=constraintAnnotation.startDate();
        this.endDateField=constraintAnnotation.endDate();
    }
    //自定义校验逻辑：开始时间不能比结束时间大
    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        System.out.println(target);
        LocalDate startDate=(LocalDate)ReflectionsUtils.getFieldValue(target, startDateField);
        LocalDate endDate=(LocalDate)ReflectionsUtils.getFieldValue(target, endDateField);
        if(startDate==null||endDate==null){
            return false;
        }
        if(endDate.isBefore(startDate)||endDate.equals(startDate)){
            return false;
        }
        return true;
    }
}
