package kitt.site.ext.freemarker.method;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fanjun on 16/2/19.
 */
public class SubsWithSignMethod implements TemplateMethodModelEx {
//    截取以符号为界限的字符串,第一个参数为原字符,第二个为数字,第三个为符号
    public Object exec(List arguments) throws TemplateModelException {
        if(arguments.size()!=3)
            throw new TemplateModelException("arguments takes at least 3 parameter");
        String str  = arguments.get(0).toString();
        int param2  = Integer.parseInt(arguments.get(1).toString());
        String sign = arguments.get(2).toString();

        String value = "";
        if(StringUtils.isNotBlank(str)){
//            value = str.substring(param2,str.lastIndexOf(sign));
            String strArr[] = str.split(sign);
            value = strArr[param2];
        }

        return value;
    }
}
