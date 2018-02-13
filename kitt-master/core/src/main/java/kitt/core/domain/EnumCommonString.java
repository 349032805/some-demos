package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by liuxinjie on 16/1/5.
 * 字符串常量枚举类型
 */
public enum EnumCommonString implements Serializable {
    WeiXin("微信"),                                       //标识微信字符串
    WaitVerify("待审核"),                                  //待审核
    VerifyPass("审核通过"),                                //审核通过
    VerifyNotPass("审核未通过");                            //审核未通过

    public String value;

    EnumCommonString(String value) {
        this.value=value;
    }
    EnumCommonString(){}

    public String value() {
        return this.value;
    }
}
