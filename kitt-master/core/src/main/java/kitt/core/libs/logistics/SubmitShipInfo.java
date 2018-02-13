package kitt.core.libs.logistics;

import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 16/1/9.
 */
public class SubmitShipInfo {
    private String No;                  //易煤网的物流单编号, 物流单的唯一标识 sourceid
    private String StartAddress;        //起运地／受载港
    private String ArrivalAddress;      //到达地／目的港
    private int Capacity;               //吨位／吨位
    private String CargoName;           //品名／货种
    private String StartDate;           //起运日期／受载日期
    private String Remark;              //备注／备注
    private String AddTime;             //添加时间 yyyy-MM-dd HH:mm:ss
    private String IP;                  //易煤网的IP
    private String MemberMobile;        //会员手机／货主手机
    private String MemberName;          //会员姓名／货主姓名
    private String CompanyName;         //会员公司名称／货主公司名称
    private int IsTrade;                //易煤网：统计出会员当前公司是否有交易记录(0有，1、无)

    public SubmitShipInfo() {}

    public SubmitShipInfo(String no, String startAddress, String arrivalAddress, int capacity, String cargoName, String startDate, String remark, String addTime, String IP, String memberMobile, String memberName, String companyName, int isTrade) {
        No = no;
        StartAddress = startAddress;
        ArrivalAddress = arrivalAddress;
        Capacity = capacity;
        CargoName = cargoName;
        StartDate = startDate;
        Remark = remark;
        AddTime = addTime;
        this.IP = IP;
        MemberMobile = memberMobile;
        MemberName = memberName;
        CompanyName = companyName;
        IsTrade = isTrade;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getStartAddress() {
        return StartAddress;
    }

    public void setStartAddress(String startAddress) {
        StartAddress = startAddress;
    }

    public String getArrivalAddress() {
        return ArrivalAddress;
    }

    public void setArrivalAddress(String arrivalAddress) {
        ArrivalAddress = arrivalAddress;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public String getCargoName() {
        return CargoName;
    }

    public void setCargoName(String cargoName) {
        CargoName = cargoName;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getAddTime() {
        return AddTime;
    }

    public void setAddTime(String addTime) {
        AddTime = addTime;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getMemberMobile() {
        return MemberMobile;
    }

    public void setMemberMobile(String memberMobile) {
        MemberMobile = memberMobile;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public int getIsTrade() {
        return IsTrade;
    }

    public void setIsTrade(int isTrade) {
        IsTrade = isTrade;
    }

    @Override
    public String toString() {
        return "{" +
                "\"No\":\"" + No + '"' +
                ",\"StartAddress\":\"" + StartAddress + "\""+
                ",\"ArrivalAddress\":\"" + ArrivalAddress + "\""+
                ",\"Capacity\":" + Capacity +
                ",\"CargoName\":\"" + CargoName + "\""+
                ",\"StartDate\":\"" + StartDate + "\""+
                ",\"Remark\":\"" + Remark + "\""+
                ",\"AddTime\":\"" + AddTime + "\""+
                ",\"IP\":\"" + IP + "\""+
                ",\"MemberMobile\":\"" + MemberMobile + "\""+
                ",\"MemberName\":\"" + MemberName + "\""+
                ",\"CompanyName\":\"" + CompanyName + "\""+
                ",\"IsTrade\":" + IsTrade +
                '}';
    }
}
