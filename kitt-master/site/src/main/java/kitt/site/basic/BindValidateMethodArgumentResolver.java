package kitt.site.basic;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;

/**
 * Created by xiangyang on 15-6-4.
 */
@Service
public class BindValidateMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return paramType == JsonController.BindResult.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        /*       int expectIndex = parameter.getMethod().getParameters().length-1;
            int actualindex = parameter.getParameterIndex();
        if (actualindex != expectIndex) {
            throw new IllegalStateException(
                    "bindResult验证参数必须是方法参数最后一个" + parameter.getMethod());
        }*/
        return new JsonController.BindResult();
    }
}
