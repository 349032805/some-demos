package kitt.core.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by lich on 16/1/13.
 */
public class ShipRequest {
    private String ordercode;
    @NotBlank
    private String No;
    @NotBlank
    private String TransportName;
    @NotBlank
    private String TransportContacterName;
    @NotBlank
    private String TransportContacterPhone;
    @NotBlank
    private String CCEmpName;
    @NotBlank
    private String CCEmpPhone;
    @NotBlank
    private String PaymentCondition;
    @NotBlank
    private String SettlementKind;
    @NotBlank
    private String NeedBill;
    @NotNull
    private BigDecimal Price;
    @NotBlank
    private String PriceType;
    @NotNull
    private BigDecimal BaseFees;
    private BigDecimal OtherFees;
    private int Capacity;
    private String ShipName;
    private String NoLoadPort;
    private String NoLoadDate;
    private BigDecimal DemurrageFees;
    private String DemurrageFeesType;
    @NotNull
    private BigDecimal TotalAmount;
    private String Remark;
    private String OperateDate;
    private String ContractAttachUrl;
    private BigDecimal PayTotalAmount;
    private BigDecimal PayTotalWeight;
    private int DemurrageDay;

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getTransportName() {
        return TransportName;
    }

    public void setTransportName(String transportName) {
        TransportName = transportName;
    }

    public String getTransportContacterName() {
        return TransportContacterName;
    }

    public void setTransportContacterName(String transportContacterName) {
        TransportContacterName = transportContacterName;
    }

    public String getTransportContacterPhone() {
        return TransportContacterPhone;
    }

    public void setTransportContacterPhone(String transportContacterPhone) {
        TransportContacterPhone = transportContacterPhone;
    }

    public String getCCEmpName() {
        return CCEmpName;
    }

    public void setCCEmpName(String CCEmpName) {
        this.CCEmpName = CCEmpName;
    }

    public String getCCEmpPhone() {
        return CCEmpPhone;
    }

    public void setCCEmpPhone(String CCEmpPhone) {
        this.CCEmpPhone = CCEmpPhone;
    }

    public String getPaymentCondition() {
        return PaymentCondition;
    }

    public void setPaymentCondition(String paymentCondition) {
        PaymentCondition = paymentCondition;
    }

    public String getSettlementKind() {
        return SettlementKind;
    }

    public void setSettlementKind(String settlementKind) {
        SettlementKind = settlementKind;
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

    public BigDecimal getBaseFees() {
        return BaseFees;
    }

    public void setBaseFees(BigDecimal baseFees) {
        BaseFees = baseFees;
    }

    public BigDecimal getOtherFees() {
        return OtherFees;
    }

    public void setOtherFees(BigDecimal otherFees) {
        OtherFees = otherFees;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public String getShipName() {
        return ShipName;
    }

    public void setShipName(String shipName) {
        ShipName = shipName;
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

    public String getOperateDate() {
        return OperateDate;
    }

    public void setOperateDate(String operateDate) {
        OperateDate = operateDate;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getContractAttachUrl() {
        return ContractAttachUrl;
    }

    public void setContractAttachUrl(String contractAttachUrl) {
        ContractAttachUrl = contractAttachUrl;
    }

    public BigDecimal getPayTotalAmount() {
        return PayTotalAmount;
    }

    public void setPayTotalAmount(BigDecimal payTotalAmount) {
        PayTotalAmount = payTotalAmount;
    }

    public BigDecimal getPayTotalWeight() {
        return PayTotalWeight;
    }

    public void setPayTotalWeight(BigDecimal payTotalWeight) {
        PayTotalWeight = payTotalWeight;
    }

    public int getDemurrageDay() {
        return DemurrageDay;
    }

    public void setDemurrageDay(int demurrageDay) {
        DemurrageDay = demurrageDay;
    }
}
