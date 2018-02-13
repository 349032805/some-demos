package kitt.core.domain;

import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 16/1/13.
 */
public class ShipAnchorPointInfo {
    private int id;
    private String info;                //过程信息
    private String processtime;         //过程发生时间格式 yyyy-MM-dd HH:mm:ss
    private String sign;                //信息状态 0 正常 1 删除
    private String souceId;             //易煤网流水
    private int infoid;                 //船舶动态信息id
    private int sort;                   //排序
    private LocalDateTime createtime;   //数据创建日期
    private LocalDateTime lastupdatetime;//最后一次更新时间
    private int isdelete;               //逻辑删除   1 删除   0未删除
    private int cdId;                   //船东id
    private int olddata;                //标识是新数据还是老数据   0:新数据  1:老数据

    public ShipAnchorPointInfo(){}

    public ShipAnchorPointInfo(int id, String info, String processtime, String sign, String souceId, int infoid, int sort, LocalDateTime createtime, LocalDateTime lastupdatetime) {
        this.id = id;
        this.info = info;
        this.processtime = processtime;
        this.sign = sign;
        this.souceId = souceId;
        this.infoid = infoid;
        this.sort = sort;
        this.createtime = createtime;
        this.lastupdatetime = lastupdatetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getProcesstime() {
        return processtime;
    }

    public void setProcesstime(String processtime) {
        this.processtime = processtime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSouceId() {
        return souceId;
    }

    public void setSouceId(String souceId) {
        this.souceId = souceId;
    }

    public int getInfoid() {
        return infoid;
    }

    public void setInfoid(int infoid) {
        this.infoid = infoid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
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

    public int getCdId() {
        return cdId;
    }

    public void setCdId(int cdId) {
        this.cdId = cdId;
    }

    public int getOlddata() {
        return olddata;
    }

    public void setOlddata(int olddata) {
        this.olddata = olddata;
    }

    @Override
    public String toString() {
        return "ShipAnchorPointInfo{" +
                "id=" + id +
                ", info='" + info + '\'' +
                ", processtime='" + processtime + '\'' +
                ", sign='" + sign + '\'' +
                ", souceId='" + souceId + '\'' +
                ", infoid=" + infoid +
                ", sort=" + sort +
                ", createtime=" + createtime +
                ", lastupdatetime=" + lastupdatetime +
                '}';
    }
}
