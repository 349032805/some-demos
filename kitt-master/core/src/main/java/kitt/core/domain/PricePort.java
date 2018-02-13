package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by liuxinjie on 16/1/21.
 */
public class PricePort extends CoalBaseData implements Serializable {
    private int id;                                     //id
    private String region;                              //地区
    private String name;                                //名称
    private String pricetype;                           //价格类型
    private String team;                                //团队
    private boolean isdelete;                           //是否删除
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;                   //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;               //最后一次更新时间
    private int lastupdatemanid;                        //最后一次操作人id
    private String lastupdatemanusername;               //最后一次操作人账号
    private String lastupdatemanname;                   //最后一次操作人姓名
    private boolean isshow;                             //是否在前台显示
    private int sequence;                               //显示顺序
    private String unit;                                //价格单位
    private String placedispatch;                       //发运地
    private String placereceipt;                        //收货地
    private String value;                               //最新一条数据 —— 价格
    private String value02;                             //最新一条数据 —— 价格右边值
    private String date;                                //最新一条数据 —— 价格对应日期

    private List<PricePortValue> pricePortValueList;    //指标数值

    public PricePort() {
    }

    public PricePort(Integer NCV, Integer NCV02, BigDecimal RS, BigDecimal RS02, BigDecimal TM, BigDecimal TM02, BigDecimal RV, BigDecimal RV02, BigDecimal ASH, BigDecimal ASH02, Integer bondindex, Integer bondindex02, String region, String name, String pricetype, String unit, String team, String placedispatch, String placereceipt, int lastupdatemanid, String lastupdatemanusername, String lastupdatemanname) {
        super(NCV, NCV02, RS, RS02, TM, TM02, RV, RV02, ASH, ASH02, bondindex, bondindex02);
        this.region = region;
        this.name = name;
        this.pricetype = pricetype;
        this.unit = unit;
        this.team = team;
        this.placedispatch = placedispatch;
        this.placereceipt = placereceipt;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPricetype() {
        return pricetype;
    }

    public void setPricetype(String pricetype) {
        this.pricetype = pricetype;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
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

    public List<PricePortValue> getPricePortValueList() {
        return pricePortValueList;
    }

    public void setPricePortValueList(List<PricePortValue> pricePortValueList) {
        this.pricePortValueList = pricePortValueList;
    }

    public boolean isshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPlacedispatch() {
        return placedispatch;
    }

    public void setPlacedispatch(String placedispatch) {
        this.placedispatch = placedispatch;
    }

    public String getPlacereceipt() {
        return placereceipt;
    }

    public void setPlacereceipt(String placereceipt) {
        this.placereceipt = placereceipt;
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
}
