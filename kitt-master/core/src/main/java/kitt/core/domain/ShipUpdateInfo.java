package kitt.core.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by lich on 16/2/22.
 */
public class ShipUpdateInfo {
    @NotBlank
    private String No;
    @NotBlank
    private String NeedBill;
    @NotNull
    private BigDecimal Price;
    @NotBlank
    private String PriceType;
    @NotNull
    private int Capacity;
    @NotBlank
    private String NoLoadPort;
    @NotBlank
    private String NoLoadDate;
    @NotNull
    private BigDecimal TotalAmount;
    @NotBlank
    private String Remark;
    @NotNull
    private BigDecimal DemurrageFees;
    @NotBlank
    private String DemurrageFeesType;
    @NotNull
    private BigDecimal OtherFees;
    @NotNull
    private int DemurrageDay;

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getNeedBill() {
        return NeedBill;
    }

    public void setNeedBill(String needBill) {
        NeedBill = needBill;
    }

    public BigDecimal getPrice() {
        return Price;
    }

    public void setPrice(BigDecimal price) {
        Price = price;
    }

    public String getPriceType() {
        return PriceType;
    }

    public void setPriceType(String priceType) {
        PriceType = priceType;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public String getNoLoadPort() {
        return NoLoadPort;
    }

    public void setNoLoadPort(String noLoadPort) {
        NoLoadPort = noLoadPort;
    }

    public String getNoLoadDate() {
        return NoLoadDate;
    }

    public void setNoLoadDate(String noLoadDate) {
        NoLoadDate = noLoadDate;
    }

    public BigDecimal getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public BigDecimal getDemurrageFees() {
        return DemurrageFees;
    }

    public void setDemurrageFees(BigDecimal demurrageFees) {
        DemurrageFees = demurrageFees;
    }

    public String getDemurrageFeesType() {
        return DemurrageFeesType;
    }

    public void setDemurrageFeesType(String demurrageFeesType) {
        DemurrageFeesType = demurrageFeesType;
    }

    public BigDecimal getOtherFees() {
        return OtherFees;
    }

    public void setOtherFees(BigDecimal otherFees) {
        OtherFees = otherFees;
    }

    public int getDemurrageDay() {
        return DemurrageDay;
    }

    public void setDemurrageDay(int demurrageDay) {
        DemurrageDay = demurrageDay;
    }
}
