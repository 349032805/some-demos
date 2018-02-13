package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by liuxinjie on 15/12/18.
 * 人员枚举类型
 */
public enum EnumPerson implements Serializable {
    Meeting_HonoredGuest("参加会议嘉宾"),
    Meeting_AttendGuest("参加会议的人员");

    public String value;

    EnumPerson(String value) {
        this.value=value;
    }
    EnumPerson(){}

    public String value() {
        return this.value;
    }
}
