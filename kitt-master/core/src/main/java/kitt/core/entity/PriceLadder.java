package kitt.core.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by lich on 15/9/2.
 */
public class PriceLadder extends  BaseEntity implements Serializable {
    private int id;
    private int sellinfoid;                 //sellinfo表对应的id
    private int squence;                    //次序，第几个阶梯价
    private int price;                      //价格
    private int amount1;                    //左边数量
    private int amount2;                    //右边数量
    private int userid;                     //用户id
    private String username;                //用户名字

    public PriceLadder() {
    }

    public PriceLadder(int squence, int price, int amount1, int amount2) {
        this.price = price;
        this.amount1 = amount1;
        this.amount2 = amount2;
        this.squence = squence;
    }

    public PriceLadder(int sellinfoid, int squence, int price, int amount1, int amount2, LocalDateTime createtime, int userid, String username) {
        this.sellinfoid = sellinfoid;
        this.squence = squence;
        this.price = price;
        this.amount1 = amount1;
        this.amount2 = amount2;
        this.createdDate = createdDate;
        this.userid = userid;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSellinfoid() {
        return sellinfoid;
    }

    public void setSellinfoid(int sellinfoid) {
        this.sellinfoid = sellinfoid;
    }

    public int getSquence() {
        return squence;
    }

    public void setSquence(int squence) {
        this.squence = squence;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount1() {
        return amount1;
    }

    public void setAmount1(int amount1) {
        this.amount1 = amount1;
    }

    public int getAmount2() {
        return amount2;
    }

    public void setAmount2(int amount2) {
        this.amount2 = amount2;
    }

    public LocalDateTime getCreatetime() {
        return createdDate;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createdDate = createtime;
    }
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
