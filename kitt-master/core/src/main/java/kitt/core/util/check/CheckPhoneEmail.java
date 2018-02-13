package kitt.core.util.check;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Created by liuxinjie on 15/9/28.
 */
@Service
public class CheckPhoneEmail {

    /**
     * 检验手机号是否是正确的手机号
     * @param securephone        手机号码
     * @return                   true: 符合要求, false: 不是正确的手机号
     */
    public boolean doCheckMobilePhoneNumMethod(String securephone) {
        return (null != securephone) && Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$").matcher(securephone).matches(); // 验证手机号
    }

    /**
     * 检查邮箱格式是否正确
     * @param email              邮箱
     * @return                   true: 符合要求, false: 不是正确的油箱
     */
    public boolean doCheckEmailMethod(String email) {
        return (null != email) && Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$").matcher(email).matches(); // 验证email
    }

    


}
