package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by lich on 15/11/13.
 */
public class TenderQuotes implements Serializable {
    private int id;
    private String companyname;
    private int userid;
    @NotNull(message = "招标id不能为空")
    private int tenderdeclarationid;
    @NotNull(message = "招标编码不能为空")
    private String tendercode;
    private String status;
    private String traderphone;
    private int isdelete;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;          //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;  //最后更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getTenderdeclarationid() {
        return tenderdeclarationid;
    }

    public void setTenderdeclarationid(int tenderdeclarationid) {
        this.tenderdeclarationid = tenderdeclarationid;
    }

    public String getTendercode() {
        return tendercode;
    }

    public void setTendercode(String tendercode) {
        this.tendercode = tendercode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTraderphone() {
        return traderphone;
    }

    public void setTraderphone(String traderphone) {
        this.traderphone = traderphone;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }
}
