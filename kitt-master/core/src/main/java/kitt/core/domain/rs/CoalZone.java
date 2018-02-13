package kitt.core.domain.rs;

import java.math.BigDecimal;

/**
 * Created by fanjun on 16/1/13.
 */
public class CoalZone {

    private Integer displaymode;
    private Integer province;
    private Integer coaltype;
    private Integer deliverymode;
//    private String companyname;
    private Integer NCV01;
    private Integer NCV02;
    private BigDecimal TM01;
    private BigDecimal TM02;
    private BigDecimal RS01;
    private BigDecimal RS02;
    private BigDecimal ADV01;
    private BigDecimal ADV02;

    private Integer price01;
    private Integer price02;

    private String location;

    public CoalZone(){

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getDisplaymode() {
        return displaymode;
    }

    public void setDisplaymode(Integer displaymode) {
        this.displaymode = displaymode;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCoaltype() {
        return coaltype;
    }

    public void setCoaltype(Integer coaltype) {
        this.coaltype = coaltype;
    }

    public Integer getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(Integer deliverymode) {
        this.deliverymode = deliverymode;
    }

    /*public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }*/

    public Integer getNCV01() {
        return NCV01;
    }

    public void setNCV01(Integer NCV01) {
        this.NCV01 = NCV01;
    }

    public Integer getNCV02() {
        return NCV02;
    }

    public void setNCV02(Integer NCV02) {
        this.NCV02 = NCV02;
    }

    public BigDecimal getTM01() {
        return TM01;
    }

    public void setTM01(BigDecimal TM01) {
        this.TM01 = TM01;
    }

    public BigDecimal getTM02() {
        return TM02;
    }

    public void setTM02(BigDecimal TM02) {
        this.TM02 = TM02;
    }

    public BigDecimal getRS01() {
        return RS01;
    }

    public void setRS01(BigDecimal RS01) {
        this.RS01 = RS01;
    }

    public BigDecimal getRS02() {
        return RS02;
    }

    public void setRS02(BigDecimal RS02) {
        this.RS02 = RS02;
    }

    public BigDecimal getADV01() {
        return ADV01;
    }

    public void setADV01(BigDecimal ADV01) {
        this.ADV01 = ADV01;
    }

    public BigDecimal getADV02() {
        return ADV02;
    }

    public void setADV02(BigDecimal ADV02) {
        this.ADV02 = ADV02;
    }

    public Integer getPrice01() {
        return price01;
    }

    public void setPrice01(Integer price01) {
        this.price01 = price01;
    }

    public Integer getPrice02() {
        return price02;
    }

    public void setPrice02(Integer price02) {
        this.price02 = price02;
    }
}
