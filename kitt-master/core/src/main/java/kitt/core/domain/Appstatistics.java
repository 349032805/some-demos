package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 15/8/24.
 */
public class Appstatistics implements Serializable {
    private int id;
    @NotNull
    private String equipmentnumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastlogintime;
    private int Logintimes;
    private boolean isdelete;

    public Appstatistics(){}

    public Appstatistics(int id, String equipmentnumber, LocalDateTime createtime, LocalDateTime lastlogintime, int logintimes, boolean isdelete) {
        this.id = id;
        this.equipmentnumber = equipmentnumber;
        this.createtime = createtime;
        this.lastlogintime = lastlogintime;
        Logintimes = logintimes;
        this.isdelete = isdelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEquipmentnumber() {
        return equipmentnumber;
    }

    public void setEquipmentnumber(String equipmentnumber) {
        this.equipmentnumber = equipmentnumber;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getLastlogintime() {
        return lastlogintime;
    }

    public void setLastlogintime(LocalDateTime lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    public int getLogintimes() {
        return Logintimes;
    }

    public void setLogintimes(int logintimes) {
        Logintimes = logintimes;
    }

    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }
}
