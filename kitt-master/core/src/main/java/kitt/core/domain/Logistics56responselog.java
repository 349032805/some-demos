package kitt.core.domain;

import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 15/12/21.
 */
public class Logistics56responselog {
    private int id;
    private String souceid;
    private int code;
    private int databodysize;
    private String message;
    private String databody;
    private LocalDateTime createtime;

    public Logistics56responselog() {}

    public Logistics56responselog(int id, String souceid, int code, int databodysize, String message, String databody, LocalDateTime createtime) {
        this.id = id;
        this.souceid = souceid;
        this.code = code;
        this.databodysize = databodysize;
        this.message = message;
        this.databody = databody;
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSouceid() {
        return souceid;
    }

    public void setSouceid(String souceid) {
        this.souceid = souceid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getDatabodysize() {
        return databodysize;
    }

    public void setDatabodysize(int databodysize) {
        this.databodysize = databodysize;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatabody() {
        return databody;
    }

    public void setDatabody(String databody) {
        this.databody = databody;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "Logistics56responselog{" +
                "id=" + id +
                ", souceid=" + souceid +
                ", code=" + code +
                ", databodysize=" + databodysize +
                ", message='" + message + '\'' +
                ", databody='" + databody + '\'' +
                ", createtime=" + createtime +
                '}';
    }
}
