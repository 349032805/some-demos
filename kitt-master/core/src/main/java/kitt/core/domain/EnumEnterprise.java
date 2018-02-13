package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by liuxinjie on 15/12/21.
 * 企业枚举类型
 */
public enum EnumEnterprise implements Serializable {
    Meeting_HostUnit("会议主办单位"),                  //会议:主办单位
    Meeting_AssistUnit("会议协办单位"),                //会议:协办单位
    Meeting_AttendEnterprise("会议参加企业"),          //会议:参加企业
    Meeting_AttendMedia("会议参加媒体");               //会议:参加媒体

    public String value;

    EnumEnterprise(String value) {
        this.value=value;
    }
    EnumEnterprise(){}

    public String value() {
        return this.value;
    }

}
