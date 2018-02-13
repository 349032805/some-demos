package kitt.core.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by fanjun on 15-1-21.
 */
public class Finance implements Serializable {

    //金融,煤易贷和煤易融联系
    private int id;
    private String type;                //类型
    private String companyname;         //公司名称
    private String address;             //公司地址
    private String businessarea;        //业务区域
    private BigDecimal amountnum;           //融资金额(单位万元)
    private String contact;             //联系人
    private String phone;               //联系电话
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;   //创建时间
    private String status;              //状态
    private String solvedmanid;         //处理人 id
    private String solvedmanusername;   //处理人 登录名
    private String solvedmanname;       //处理人 姓名
    private String solvedremarks;       //处理备注
    private boolean isdelete;           //是否删除

    public Finance(){

    }

    public Finance(String type, String companyname, String address, String businessarea, BigDecimal amountnum, String contact, String phone, LocalDateTime createtime) {
        this.setType(type);
        this.setCompanyname(companyname);
        this.setAddress(address);
        this.setBusinessarea(businessarea);
        this.amountnum = amountnum;
        this.setContact(contact);
        this.setPhone(phone);
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (!StringUtils.isBlank(type)) {
            type = type.trim();
        }
        if (!StringUtils.isBlank(type) && type.length() > 9) {
            this.type = type.substring(0, 9);
        } else {
            this.type = type;
        }
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        if (!StringUtils.isBlank(companyname)) {
            companyname = companyname.trim();
        }
        if (!StringUtils.isBlank(companyname) && companyname.length() > 45) {
            this.companyname = companyname.substring(0, 45);
        } else {
            this.companyname = companyname;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (!StringUtils.isBlank(address)) {
            address = address.trim();
        }
        if (!StringUtils.isBlank(address) && address.length() > 45) {
            this.address = address.substring(0, 45);
        } else {
            this.address = address;
        }
    }

    public String getBusinessarea() {
        return businessarea;
    }

    public void setBusinessarea(String businessarea) {
        if (!StringUtils.isBlank(businessarea)) {
            businessarea = businessarea.trim();
        }
        if (!StringUtils.isBlank(businessarea) && businessarea.length() > 45) {
            this.businessarea = businessarea.substring(0, 45);
        } else {
            this.businessarea = businessarea;
        }
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if (!StringUtils.isBlank(contact)) {
            contact = contact.trim();
        }
        if (!StringUtils.isBlank(contact) && contact.length() > 18) {
            this.contact = contact.substring(0, 18);
        } else {
            this.contact = contact;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (!StringUtils.isBlank(phone)) {
            phone = phone.trim();
        }
        if (!StringUtils.isBlank(phone) && phone.length() > 18) {
            this.phone = phone.substring(0, 18);
        } else {
            this.phone = phone;
        }
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public BigDecimal getAmountnum() {
        return amountnum;
    }

    public void setAmountnum(BigDecimal amountnum) {
        this.amountnum = amountnum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!StringUtils.isBlank(status)) {
            status = status.trim();
        }
        if (!StringUtils.isBlank(status) && status.length() > 15) {
            status.substring(0, 15);
        } else {
            this.status = status;
        }
    }

    public String getSolvedmanid() {
        return solvedmanid;
    }

    public void setSolvedmanid(String solvedmanid) {
        this.solvedmanid = solvedmanid;
    }

    public String getSolvedmanusername() {
        return solvedmanusername;
    }

    public void setSolvedmanusername(String solvedmanusername) {
        this.solvedmanusername = solvedmanusername;
    }

    public String getSolvedremarks() {
        return solvedremarks;
    }

    public void setSolvedremarks(String solvedremarks) {
        if (!StringUtils.isBlank(solvedremarks)) {
            solvedremarks = solvedremarks.trim();
        }
        if (!StringUtils.isBlank(solvedremarks) && solvedremarks.length() > 250) {
            this.solvedremarks = solvedremarks.substring(0, 250);
        } else {
            this.solvedremarks = solvedremarks;
        }
    }

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public String getSolvedmanname() {
        return solvedmanname;
    }

    public void setSolvedmanname(String solvedmanname) {
        this.solvedmanname = solvedmanname;
    }
}


