package kitt.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by lich on 15/12/14.
 */
public class Logisticsvender {
    private int id;
    private String vendername;               //物流供应商名
    private String webserviceurl;            //供应商webservice链接
    private int usernumber;                  //成单用户数
    private BigDecimal score;                //评价分
    private LocalDateTime createtime;        //创建时间
    private LocalDateTime lastupdatetime;    //最后更新时间
    private int isdelete;                    //是否删除 0:未删除  1:删除



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendername() {
        return vendername;
    }

    public void setVendername(String vendername) {
        this.vendername = vendername;
    }

    public String getWebserviceurl() {
        return webserviceurl;
    }

    public void setWebserviceurl(String webserviceurl) {
        this.webserviceurl = webserviceurl;
    }

    public int getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(int usernumber) {
        this.usernumber = usernumber;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
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

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

}
