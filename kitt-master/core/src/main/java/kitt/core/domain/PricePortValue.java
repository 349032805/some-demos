package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 16/1/21.
 */
public class PricePortValue implements Serializable {
    private int id;                          //id
    private int priceportid;                 //港口价格表ID
    private String value;                    //价格
    private String value02;                  //价格右边值
    private String date;                     //价格对应日期
    private boolean isdelete;                //是否删除
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;        //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;    //最后一次更新时间
    private int lastupdatemanid;             //最后一次操作人id
    private String lastupdatemanusername;    //最后一次操作人账号
    private String lastupdatemanname;        //最后一次操作人姓名

    public PricePortValue() {
    }

    public PricePortValue(int priceportid, String value, String value02, String date, int lastupdatemanid, String lastupdatemanusername, String lastupdatemanname) {
        this.priceportid = priceportid;
        this.value = value;
        this.value02 = value02;
        this.date = date;
        this.lastupdatemanid = lastupdatemanid;
        this.lastupdatemanusername = lastupdatemanusername;
        this.lastupdatemanname = lastupdatemanname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriceportid() {
        return priceportid;
    }

    public void setPriceportid(int priceportid) {
        this.priceportid = priceportid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue02() {
        return value02;
    }

    public void setValue02(String value02) {
        this.value02 = value02;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
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

    public int getLastupdatemanid() {
        return lastupdatemanid;
    }

    public void setLastupdatemanid(int lastupdatemanid) {
        this.lastupdatemanid = lastupdatemanid;
    }

    public String getLastupdatemanusername() {
        return lastupdatemanusername;
    }

    public void setLastupdatemanusername(String lastupdatemanusername) {
        this.lastupdatemanusername = lastupdatemanusername;
    }

    public String getLastupdatemanname() {
        return lastupdatemanname;
    }

    public void setLastupdatemanname(String lastupdatemanname) {
        this.lastupdatemanname = lastupdatemanname;
    }
}
