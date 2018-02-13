package kitt.site.service;

import kitt.site.basic.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xiangyang on 15-7-27.
 */
@Component
public class BeanValidators {
    public static Validator validator;

    @Autowired
    @Qualifier(value="validator")
    public  void setValidator(Validator validator) {
        BeanValidators.validator = validator;
    }

    /**
     * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException.
     */
    public static void  validateWithException(Object object, Class<?>... groups) throws BusinessException {
        Set constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            //如果后端数据校验出错，拿第一条错误信息，以弹框形式展示错误信息
            throw new BusinessException((String)extractMessage(constraintViolations).get(0));
        }
    }
    /**
     * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>中为List<message>.
     */
    public static List<String> extractMessage(ConstraintViolationException e) {
        return extractMessage(e.getConstraintViolations());
    }

    /**
     * 辅助方法, 转换Set<ConstraintViolation>为List<message>
     */
    public static List<String> extractMessage(Set<? extends ConstraintViolation> constraintViolations) {
        List<String> errorMessages = new ArrayList<String>();
        for (ConstraintViolation violation : constraintViolations) {
            errorMessages.add(violation.getMessage());
        }
        return errorMessages;
    }

    public static boolean  validateWithMessage(Object object, Class<?>... groups)  {
        Set constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            return false;
        }else{
            return true;
        }
    }
}
