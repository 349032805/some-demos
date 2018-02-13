package kitt.core.domain.result;


import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xiangyang on 16/1/6.
 */
public class ReservationRs {
    private Integer id;
    private String companyName;
    private String companyAddress;
    private LocalDateTime createtime;
    private String linkman;
    private String phone;
    private String coalType;
    private String supplyId;
    private Integer sellInfoId;
    private String onlineBroker;
    private String brokerName;
    private Integer status;
    private Integer amount;
    private LocalDateTime reservationDate;
    private LocalDateTime revisitDate;
    private LocalDate deliveryDate;
    private Integer provinceId;
    private Integer cityId;
    private Integer brokerGroupId;
    private Integer brokerId;
    //broker处理意见
    private String  brokerSuggest;
    //客户处理意见
    private String customerServiceSuggest;
    private String adminName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private List<ReservationRs> rsList;
    private String type;                                   //客服追踪类型,来源, 值等于revervation 表中type
    private int logistics;                                 //1: 需要易煤网提供物流服务, 2: 不需要易煤网提供物流服务
    private int finance;                                   //1: 需要易煤网提供金融服务, 2: 不需要易煤网提供金融服务

    public List<ReservationRs> getRsList() {
        return rsList;
    }

    public void setRsList(List<ReservationRs> rsList) {
        this.rsList = rsList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCoalType() {
        return coalType;
    }

    public void setCoalType(String coalType) {
        this.coalType = coalType;
    }

    public String getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(String supplyId) {
        this.supplyId = supplyId;
    }

    public String getOnlineBroker() {
        return onlineBroker;
    }

    public void setOnlineBroker(String onlineBroker) {
        this.onlineBroker = onlineBroker;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getRevisitDate() {
        return revisitDate;
    }

    public void setRevisitDate(LocalDateTime revisitDate) {
        this.revisitDate = revisitDate;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getBrokerGroupId() {
        return brokerGroupId;
    }

    public void setBrokerGroupId(Integer brokerGroupId) {
        this.brokerGroupId = brokerGroupId;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public Integer getSellInfoId() {
        return sellInfoId;
    }

    public void setSellInfoId(Integer sellInfoId) {
        this.sellInfoId = sellInfoId;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrokerSuggest() {
        return brokerSuggest;
    }

    public void setBrokerSuggest(String brokerSuggest) {
        this.brokerSuggest = brokerSuggest;
    }

    public String getCustomerServiceSuggest() {
        return customerServiceSuggest;
    }

    public void setCustomerServiceSuggest(String customerServiceSuggest) {
        this.customerServiceSuggest = customerServiceSuggest;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLogistics() {
        return logistics;
    }

    public void setLogistics(int logistics) {
        this.logistics = logistics;
    }

    public int getFinance() {
        return finance;
    }

    public void setFinance(int finance) {
        this.finance = finance;
    }
}
