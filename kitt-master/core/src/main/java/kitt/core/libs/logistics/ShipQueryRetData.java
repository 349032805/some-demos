package kitt.core.libs.logistics;

import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 16/1/9.
 */
public class ShipQueryRetData {
    private int id;                 //船舶船期Id
    private int cid;                //船舶id
    private String cnShipName;      //船舶中文名
    private String enShipName;      //船舶英文名
    private int levelInfo;          //船舶等级星级
    private String loadTon;         //船舶(吨位/载重吨)
    private String loadDate;        //船舶船期受载日期
    private int shipTypeID;         //船型ID（1、一般干货船，2、集散两用船 4、普通散货船）
    private int loadPortID;         //目的港，空载港ID
    private int goodsTypeID;        //货种ID
    private int Recommend;          //船舶船期是否推荐
    private String portName;        //港口名称
    private String goodsName;       //物种名称
    private int fixedRoute;         //船舶船期-是否为固定航线(0-否，1-是)
    private int planState;          //船舶船期-计划状态(0-无运载计划，1-已有运载计划)
    private String editDate;        //船舶船期-更新时间(格式:4天以前,4分以前等等)
    private int theTrue;            //船舶-是否已验证( 0-不通过，1-审核通过)
    private String shipLong;        //船舶-船长
    private String shipWidth;       //船舶-船宽
    private String typeDeep;        //船舶-型深
    private String TypeName;        //船型名称/船舶类型（1、一般干货船，2、集散两用船 4、普通散货船）
    private String Makedate;        //船舶-建造年代
    private String addTime;         //船舶船期-更新时间
    private String loadPortName;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCnShipName() {
        return cnShipName;
    }

    public void setCnShipName(String cnShipName) {
        this.cnShipName = cnShipName;
    }

    public String getEnShipName() {
        return enShipName;
    }

    public void setEnShipName(String enShipName) {
        this.enShipName = enShipName;
    }

    public int getLevelInfo() {
        return levelInfo;
    }

    public void setLevelInfo(int levelInfo) {
        this.levelInfo = levelInfo;
    }

    public String getLoadTon() {
        return loadTon;
    }

    public void setLoadTon(String loadTon) {
        this.loadTon = loadTon;
    }

    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String loadDate) {
        this.loadDate = loadDate;
    }

    public int getShipTypeID() {
        return shipTypeID;
    }

    public void setShipTypeID(int shipTypeID) {
        this.shipTypeID = shipTypeID;
    }

    public int getLoadPortID() {
        return loadPortID;
    }

    public void setLoadPortID(int loadPortID) {
        this.loadPortID = loadPortID;
    }

    public int getGoodsTypeID() {
        return goodsTypeID;
    }

    public void setGoodsTypeID(int goodsTypeID) {
        this.goodsTypeID = goodsTypeID;
    }

    public int getRecommend() {
        return Recommend;
    }

    public void setRecommend(int recommend) {
        Recommend = recommend;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getFixedRoute() {
        return fixedRoute;
    }

    public void setFixedRoute(int fixedRoute) {
        this.fixedRoute = fixedRoute;
    }

    public int getPlanState() {
        return planState;
    }

    public void setPlanState(int planState) {
        this.planState = planState;
    }

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    public int getTheTrue() {
        return theTrue;
    }

    public void setTheTrue(int theTrue) {
        this.theTrue = theTrue;
    }

    public String getShipLong() {
        return shipLong;
    }

    public void setShipLong(String shipLong) {
        this.shipLong = shipLong;
    }

    public String getShipWidth() {
        return shipWidth;
    }

    public void setShipWidth(String shipWidth) {
        this.shipWidth = shipWidth;
    }

    public String getTypeDeep() {
        return typeDeep;
    }

    public void setTypeDeep(String typeDeep) {
        this.typeDeep = typeDeep;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public String getMakedate() {
        return Makedate;
    }

    public void setMakedate(String makedate) {
        Makedate = makedate;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getLoadPortName() {
        return loadPortName;
    }

    public void setLoadPortName(String loadPortName) {
        this.loadPortName = loadPortName;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "ShipQueryRetData{" +
                "id=" + id +
                ", cid=" + cid +
                ", cnShipName='" + cnShipName + '\'' +
                ", enShipName='" + enShipName + '\'' +
                ", levelInfo=" + levelInfo +
                ", loadTon='" + loadTon + '\'' +
                ", loadDate='" + loadDate + '\'' +
                ", shipTypeID=" + shipTypeID +
                ", loadPortID=" + loadPortID +
                ", goodsTypeID=" + goodsTypeID +
                ", Recommend=" + Recommend +
                ", portName='" + portName + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", fixedRoute=" + fixedRoute +
                ", planState=" + planState +
                ", editDate='" + editDate + '\'' +
                ", theTrue=" + theTrue +
                ", shipLong='" + shipLong + '\'' +
                ", shipWidth='" + shipWidth + '\'' +
                ", typeDeep='" + typeDeep + '\'' +
                ", TypeName='" + TypeName + '\'' +
                ", Makedate='" + Makedate + '\'' +
                ", addTime='" + addTime + '\'' +
                '}';
    }
}
