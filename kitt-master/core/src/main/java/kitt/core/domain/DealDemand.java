package kitt.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by xiajing on 15-9-9.
 */
public class DealDemand implements Serializable {

    private String securephone; //手机号

    private String deliveryplace; //提货地

    private String otherplace; //提货地为其它时使用

    private String coaltype; //煤种


    private int NCV;        //低位热值

    private int supplyton;  //申供吨数

    private int quote;      //报价

    private LocalDateTime lastupdatetime;   //最后更新时间

    public DealDemand(){}

    public DealDemand(String securephone, String deliveryplace, String otherplace, String coaltype, int NCV, int supplyton, int quote, LocalDateTime lastupdatetime) {
        this.securephone = securephone;
        this.deliveryplace = deliveryplace;
        this.otherplace = otherplace;
        this.coaltype = coaltype;
        this.NCV = NCV;
        this.supplyton = supplyton;
        this.quote = quote;
        this.lastupdatetime = lastupdatetime;
    }

    public String getSecurephone() {
        return securephone;
    }

    public void setSecurephone(String securephone) {
        this.securephone = securephone;
    }

    public String getDeliveryplace() {
        return deliveryplace;
    }

    public void setDeliveryplace(String deliveryplace) {
        this.deliveryplace = deliveryplace;
    }

    public String getOtherplace() {
        return otherplace;
    }

    public void setOtherplace(String otherplace) {
        this.otherplace = otherplace;
    }

    public String getCoaltype() {
        return coaltype;
    }

    public void setCoaltype(String coaltype) {
        this.coaltype = coaltype;
    }

    public int getNCV() {
        return NCV;
    }

    public void setNCV(int NCV) {
        this.NCV = NCV;
    }


    public int getSupplyton() {
        return supplyton;
    }

    public void setSupplyton(int supplyton) {
        this.supplyton = supplyton;
    }

    public int getQuote() {
        return quote;
    }

    public void setQuote(int quote) {
        this.quote = quote;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }
}
