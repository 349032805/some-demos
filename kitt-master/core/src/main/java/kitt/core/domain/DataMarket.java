package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by liuxinjie on 15/9/21.
 */
public class DataMarket implements Serializable {
    private int id;
    private int type;                  //数据类型
    private String name;               //指标名称
    private String data01;             //数据1
    private String data02;             //数据2
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;        //数据日期  对应data01 和 data02的日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;                  //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;        //最后一次变化时间
    private int isdelete;                        //是否删除  1  删除  0   没有删除

    private int data011;
    private int data012;
    private int data021;
    private int data022;
    private int compare;
    private int sequence;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData01() {
        return data01;
    }

    public void setData01(String data01) {
        this.data01 = data01;
    }

    public String getData02() {
        return data02;
    }

    public void setData02(String data02) {
        this.data02 = data02;
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

    @Override
    public String toString() {
        return "DataMarket{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", data01='" + data01 + '\'' +
                ", data02='" + data02 + '\'' +
                ", date=" + date +
                ", createtime=" + createtime +
                ", lastupdatetime=" + lastupdatetime +
                ", isdelete=" + isdelete +
                '}';
    }

    public DataMarket() {
    }

    public DataMarket(int type, String name, String data01, String data02, LocalDate date, LocalDateTime createtime, LocalDateTime lastupdatetime, int isdelete) {
        this.type = type;
        this.name = name;
        this.data01 = data01;
        this.data02 = data02;
        this.date = date;
        this.createtime = createtime;
        this.lastupdatetime = lastupdatetime;
        this.isdelete = isdelete;
    }

    public int getData011() {
        return data011;
    }

    public void setData011(int data011) {
        this.data011 = data011;
    }

    public int getData012() {
        return data012;
    }

    public void setData012(int data012) {
        this.data012 = data012;
    }

    public int getData021() {
        return data021;
    }

    public void setData021(int data021) {
        this.data021 = data021;
    }

    public int getData022() {
        return data022;
    }

    public void setData022(int data022) {
        this.data022 = data022;
    }


    public int getCompare() {
        return compare;
    }

    public void setCompare(int compare) {
        this.compare = compare;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
