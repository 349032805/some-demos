package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by jack on 6/8/15.
 */
public class IndIndexCfg implements Serializable {
    private String id;                     //指数id（内部）
    private String name;                   //指数名称
    private String parentid;               //父亲id
    private boolean isleaf;                //是否是叶子节点
    private int level;                     //层级
    private Integer sequence;              //顺序， 默认值是 10
    private int fqcy;                      //频率，1：日、2：周、3：月、4：季、5：年
    private String unit;                   //单位
    private String origin;                 //来源
    private int showstyle;                 //图表类型
    private String remarks;                //备注
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;  //最后一次更新时间
    private int lastupdateuserid;          //最后一次修改的管理员id
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;      //创建时间
    private int createuserid;              //创建人id
    private boolean defaultone;            //是否是默认选项
    private Integer precesion;             //精确度，小数位
    private boolean isdelete;              //是否被删除
    private boolean isshow;                //是否在前台显示
    private Integer weekdate;              //周数据固定是星期几
    private boolean display;               //后台目录是否折叠，显示

    //父节点是否存在
    private boolean parentIsExist;        //前台判断子节点的父节点是否存在于集合
    //排序id
    private String orderid;

    public IndIndexCfg(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public boolean isleaf() {
        return isleaf;
    }

    public void setIsleaf(boolean isleaf) {
        this.isleaf = isleaf;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public int getFqcy() {
        return fqcy;
    }

    public void setFqcy(int fqcy) {
        this.fqcy = fqcy;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getShowstyle() {
        return showstyle;
    }

    public void setShowstyle(int showstyle) {
        this.showstyle = showstyle;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public int getLastupdateuserid() {
        return lastupdateuserid;
    }

    public void setLastupdateuserid(int lastupdateuserid) {
        this.lastupdateuserid = lastupdateuserid;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public int getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(int createuserid) {
        this.createuserid = createuserid;
    }

    public boolean isDefaultone() {
        return defaultone;
    }

    public void setDefaultone(boolean defaultone) {
        this.defaultone = defaultone;
    }

    public Integer getPrecesion() {
        return precesion;
    }

    public void setPrecesion(Integer precesion) {
        this.precesion = precesion;
    }

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public boolean isshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }

    public boolean isParentIsExist() {
        return parentIsExist;
    }

    public void setParentIsExist(boolean parentIsExist) {
        this.parentIsExist = parentIsExist;
    }

    public Integer getWeekdate() {
        return weekdate;
    }

    public void setWeekdate(Integer weekdate) {
        this.weekdate = weekdate;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
}
