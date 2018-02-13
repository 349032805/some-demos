package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by liuxinjie on 16/1/28.
 */
public enum EnumReservation implements Serializable {
    Customer_Reservation("客户预约"),
    Release_Supply("发布供应");

    public String value;

    EnumReservation(String value) {
        this.value = value;
    }
    EnumReservation(){}

    public String value() {
        return this.value;
    }
}
