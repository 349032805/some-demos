package kitt.core.domain;

import java.time.LocalDateTime;

/**
 * Created by xiangyang on 16/1/8.
 */
public class ReservationResult {

    private Integer id;
    //供应id
    private Integer reservationId;
    //线下交易员组id
    private Integer brokerGroupId;
    //线下交易员
    private Integer brokerId;
    //处理人
    private Integer adminId;
    //线上交易员姓名
    private String onlineBroker;
    //处理状态 1 未沟通,2 沟通中未完成采购 3 沟通结果不进行采购  4 沟通结束,采取线下跟进 5 采购已确定
    private Integer status;
    //broker处理意见
    private String  brokerSuggest;
    //客户处理意见
    private String customerServiceSuggest;
    private LocalDateTime createtime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getOnlineBroker() {
        return onlineBroker;
    }

    public void setOnlineBroker(String onlineBroker) {
        this.onlineBroker = onlineBroker;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }
}
