package kitt.core.domain;

import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 15/12/25.
 */
public class Returnvisit {
    private int id;
    private String souceid;
    private String status;
    private String comment;
    private LocalDateTime createtime;

    public Returnvisit(){}

    public Returnvisit(String souceid, String status, String comment) {
        this.souceid = souceid;
        this.status = status;
        this.comment = comment;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSouceId() {
        return souceid;
    }

    public void setSouceId(String souceId) {
        this.souceid = souceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "Returnvisit{" +
                "id=" + id +
                ", souceId='" + souceid + '\'' +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", createtime=" + createtime +
                '}';
    }
}
